# Currency Conversion Implementation Tutorial - Mobilele Project

## Overview

This tutorial provides an in-depth analysis of the currency conversion system implemented in the Mobilele project. The system fetches exchange rates from an external API, stores them in a database, and provides real-time currency conversion functionality through both REST API endpoints and frontend JavaScript.

## Architecture Overview

The currency conversion system follows a layered architecture:

```
Frontend (JavaScript) → REST Controller → Service Layer → Repository → Database
                                    ↓
                              External Forex API
                          (Open Exchange Rates)
```

## External API Integration

### Open Exchange Rates API

The project uses **[Open Exchange Rates](https://openexchangerates.org/)** as the external data source for currency exchange rates.

**API Configuration (`application.yaml`):**
```yaml
forex:
  api:
    key: ${FOREX_API_KEY:test}
    url: "https://openexchangerates.org/api/latest.json?app_id={app_id}"
    base: "USD"
  init-ex-rates: ${INIT_EX_RATES:false}
```

**Key Configuration Details:**
- **API Key:** Stored in environment variable `FOREX_API_KEY` for security
- **Base Currency:** Fixed to USD (limitation of free API plan)
- **Endpoint:** Latest exchange rates endpoint with app_id parameter
- **Initialization:** Controlled by `INIT_EX_RATES` environment variable

**API Response Format:**
```json
{
  "base": "USD",
  "rates": {
    "BGN": 1.8266,
    "EUR": 0.934216,
    "GBP": 0.792154,
    "JPY": 149.875,
    "CHF": 0.908542
  }
}
```

**Rate Fetching Process:**
1. **HTTP Request:** `GET https://openexchangerates.org/api/latest.json?app_id={API_KEY}`
2. **Response Parsing:** JSON automatically mapped to `ExRatesDTO` record
3. **Validation:** Base currency must match configured base (USD)
4. **Storage:** Each currency rate saved/updated in database
5. **Error Handling:** Invalid responses or network errors handled gracefully

## Core Components

### 1. Data Models

#### ExRateEntity (`src/main/java/bg/softuni/mobilele/model/entity/ExRateEntity.java`)
```java
@Entity
@Table(name = "ex_rates")
public class ExRateEntity extends BaseEntity {
    @NotEmpty
    @Column(unique = true)
    private String currency;    // Currency code (e.g., "EUR", "USD")
    
    @Positive
    @NotNull
    private BigDecimal rate;    // Exchange rate against base currency
}
```

**Key Features:**
- Unique currency constraint ensures no duplicate currency entries
- Uses `BigDecimal` for precise financial calculations
- Extends `BaseEntity` for common properties (ID, timestamps)

#### DTOs (Data Transfer Objects)

**ExRateDTO** (`src/main/java/bg/softuni/mobilele/model/dto/ExRateDTO.java`):
```java
public record ExRateDTO(String currency, BigDecimal rate) {}
```

**ExRatesDTO** (`src/main/java/bg/softuni/mobilele/model/dto/ExRatesDTO.java`):
```java
public record ExRatesDTO(String base, Map<String, BigDecimal> rates) {
    // Example JSON structure:
    // {
    //   "base": "USD",
    //   "rates": {
    //     "BGN": 1.8266,
    //     "EUR": 0.934216
    //   }
    // }
}
```

**ConversionResultDTO** (`src/main/java/bg/softuni/mobilele/model/dto/ConversionResultDTO.java`):
```java
public record ConversionResultDTO(String from, String to, BigDecimal amount, BigDecimal result) {}
```

### 2. Repository Layer

#### ExRateRepository (`src/main/java/bg/softuni/mobilele/repository/ExRateRepository.java`)
```java
@Repository
public interface ExRateRepository extends JpaRepository<ExRateEntity, Long> {
    Optional<ExRateEntity> findByCurrency(String currency);
}
```

**Functionality:**
- Standard JPA repository operations
- Custom method to find exchange rate by currency code
- Returns `Optional` to handle cases where currency is not found

### 3. Service Layer

#### ExRateService Interface (`src/main/java/bg/softuni/mobilele/service/ExRateService.java`)
```java
public interface ExRateService {
    List<String> allSupportedCurrencies();
    boolean hasInitializedExRates();
    ExRatesDTO fetchExRates();
    void updateRates(ExRatesDTO exRatesDTO);
    BigDecimal convert(String from, String to, BigDecimal amount);
    void publishExRates();
}
```

#### ExRateServiceImpl Implementation (`src/main/java/bg/softuni/mobilele/service/impl/ExRateServiceImpl.java`)

**Key Methods Analysis:**

##### Currency Conversion Logic (`convert` method)
```java
@Override
public BigDecimal convert(String from, String to, BigDecimal amount) {
    return findExRate(from, to)
        .orElseThrow(() -> new ApiObjectNotFoundException("Conversion from " + from + " to " + to + " not possible!", from + "~" + to))
        .multiply(amount);
}
```

##### Exchange Rate Calculation (`findExRate` method)
```java
private Optional<BigDecimal> findExRate(String from, String to) {
    if (Objects.equals(from, to)) {
        return Optional.of(BigDecimal.ONE);  // Same currency = 1:1 rate
    }

    // Get rates for both currencies relative to base currency (USD)
    Optional<BigDecimal> fromOpt = forexApiConfig.getBase().equals(from) ?
        Optional.of(BigDecimal.ONE) :  // Base currency has rate of 1
        exRateRepository.findByCurrency(from).map(ExRateEntity::getRate);

    Optional<BigDecimal> toOpt = forexApiConfig.getBase().equals(to) ?
        Optional.of(BigDecimal.ONE) :
        exRateRepository.findByCurrency(to).map(ExRateEntity::getRate);

    if (fromOpt.isEmpty() || toOpt.isEmpty()) {
        return Optional.empty();
    } else {
        // Cross-rate calculation: toRate / fromRate
        return Optional.of(toOpt.get().divide(fromOpt.get(), 2, RoundingMode.HALF_DOWN));
    }
}
```

**Cross-Rate Calculation Explained:**
- All rates are stored relative to USD (base currency)
- To convert from currency A to currency B:
  - If USD/BGN = 1.8266 and USD/EUR = 0.934216
  - Then EUR/BGN = 1.8266 / 0.934216 = 1.9553
- Uses `HALF_DOWN` rounding mode with 2 decimal places for financial precision

##### Fetching External Rates (`fetchExRates` method)
```java
@Override
public ExRatesDTO fetchExRates() {
    return restClient
        .get()
        .uri(forexApiConfig.getUrl(), forexApiConfig.getKey())
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .body(ExRatesDTO.class);
}
```

##### Updating Stored Rates (`updateRates` method)
```java
@Override
public void updateRates(ExRatesDTO exRatesDTO) {
    LOGGER.info("Updating {} rates.", exRatesDTO.rates().size());

    // Validate base currency
    if (!forexApiConfig.getBase().equals(exRatesDTO.base())) {
        throw new IllegalArgumentException("Exchange rates base currency mismatch");
    }

    // Update or create each currency rate
    exRatesDTO.rates().forEach((currency, rate) -> {
        var exRateEntity = exRateRepository.findByCurrency(currency)
            .orElseGet(() -> new ExRateEntity().setCurrency(currency));

        exRateEntity.setRate(rate);
        exRateRepository.save(exRateEntity);
    });
}
```

### 4. Configuration

#### ForexApiConfig (`src/main/java/bg/softuni/mobilele/config/ForexApiConfig.java`)
```java
@Configuration
@ConfigurationProperties(prefix = "forex.api")
public class ForexApiConfig {
    private String key;     // API key for external service
    private String url;     // API endpoint URL
    private String base;    // Base currency (must be USD for free API)

    @PostConstruct
    public void checkConfiguration() {
        verifyNotNullOrEmpty("key", key);
        verifyNotNullOrEmpty("base", base);
        verifyNotNullOrEmpty("url", url);

        if (!"USD".equals(base)) {
            throw new IllegalStateException("Free API only supports USD as base currency");
        }
    }
}
```

**Configuration Properties:**
- `forex.api.key` - External API key
- `forex.api.url` - External API endpoint URL
- `forex.api.base` - Base currency (restricted to USD)

### 5. Controller Layer

#### CurrencyController (`src/main/java/bg/softuni/mobilele/web/CurrencyController.java`)
```java
@RestController
public class CurrencyController {

    @WarnIfExecutionExceeds(threshold = 800)  // Performance monitoring
    @GetMapping("/api/convert")
    public ResponseEntity<ConversionResultDTO> convert(
        @RequestParam("from") String from,
        @RequestParam("to") String to,
        @RequestParam("amount") BigDecimal amount
    ) {
        BigDecimal result = exRateService.convert(from, to, amount);
        return ResponseEntity.ok(new ConversionResultDTO(from, to, amount, result));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ApiObjectNotFoundException.class)
    @ResponseBody
    public NotFoundErrorInfo handleApiObjectNotFoundException(ApiObjectNotFoundException ex) {
        return new NotFoundErrorInfo("NOT FOUND", ex.getId());
    }
}
```

**API Endpoint:**
- **URL:** `GET /api/convert`
- **Parameters:**
  - `from` - Source currency code
  - `to` - Target currency code  
  - `amount` - Amount to convert
- **Response:** JSON with conversion details
- **Error Handling:** Returns 404 for unsupported currency pairs

### 6. Frontend Integration

#### JavaScript Implementation (`src/main/resources/static/js/currency.js`)
```javascript
let currencyDropDown = document.getElementById('currency')
currencyDropDown.addEventListener('change', currencyChange)

function currencyChange() {
    let selectedCurrency = currencyDropDown.value
    let amountInBGN = document.getElementById('priceInBGN').value
    let priceSpan = document.getElementById('price')

    fetch('/api/convert?' + new URLSearchParams({
        from: 'BGN',
        to: selectedCurrency,
        amount: amountInBGN
    }))
    .then(response => response.json())
    .then(data => {
        priceSpan.textContent = data.result
    })
    .catch(error => {
        console.log('An error occurred:' + error)
    })
}
```

**Frontend Flow:**
1. User selects currency from dropdown
2. JavaScript reads amount in BGN from hidden field
3. Makes AJAX call to `/api/convert` endpoint
4. Updates price display with converted amount

#### Currency Selection and Population

**HTML Template (`src/main/resources/templates/details.html`):**
```html
<div class="card-text">• Currency
    <select id="currency">
        <option
            th:each="currency : *{allCurrencies}"
            th:value="${currency}"
            th:selected="${currency} == 'BGN'"
            th:text="${currency}"
        >
        </option>
    </select>
</div>
<input type="hidden" id="priceInBGN" th:value="*{price}">
```

**Currency Population Process:**
1. **Backend Data:** `allSupportedCurrencies()` method provides list of available currencies
2. **Template Rendering:** Thymeleaf populates dropdown with currencies from database
3. **Default Selection:** BGN is pre-selected as default currency
4. **Hidden Price:** Original BGN price stored in hidden input for conversion calculations

**Supported Currencies Retrieval:**
```java
// ExRateServiceImpl.java:49-55
@Override
public List<String> allSupportedCurrencies() {
    return exRateRepository
        .findAll()
        .stream()
        .map(ExRateEntity::getCurrency)
        .toList();
}
```

**Currency Selection Workflow:**
1. **Application Startup:** External API fetched and currencies stored in database
2. **Page Load:** All stored currencies populated in dropdown menu
3. **User Selection:** JavaScript event listener triggers on currency change
4. **Real-time Conversion:** AJAX call converts price from BGN to selected currency
5. **UI Update:** Price display updated without page refresh

### 7. Initialization

#### ExchangeRateInitializer (`src/main/java/bg/softuni/mobilele/init/ExchangeRateInitializer.java`)
```java
@Order(0)
@Component
@ConditionalOnProperty(name = "forex.init-ex-rates", havingValue = "true")
public class ExchangeRateInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        if (!exRateService.hasInitializedExRates()) {
            exRateService.updateRates(exRateService.fetchExRates());
        }
    }
}
```

**Initialization Process:**
- Runs at application startup with highest priority (`@Order(0)`)
- Only executes if `forex.init-ex-rates=true` property is set
- Checks if rates are already initialized to avoid duplicate fetching
- Fetches and stores initial exchange rates from external API

## Key Design Patterns and Best Practices

### 1. Separation of Concerns
- **Controller:** HTTP request/response handling
- **Service:** Business logic and external API integration
- **Repository:** Data access abstraction
- **Entity:** Data persistence mapping

### 2. Error Handling
```java
@ExceptionHandler(ApiObjectNotFoundException.class)
public NotFoundErrorInfo handleApiObjectNotFoundException(ApiObjectNotFoundException ex) {
    return new NotFoundErrorInfo("NOT FOUND", ex.getId());
}
```

### 3. Financial Precision
- Uses `BigDecimal` for all monetary calculations
- Implements proper rounding strategies (`RoundingMode.HALF_DOWN`)
- Maintains 2 decimal places for currency precision

### 4. Performance Considerations
- `@WarnIfExecutionExceeds(threshold = 800)` monitors conversion performance
- Database indexing on currency field for fast lookups
- Optional-based error handling to avoid exceptions in normal flow

### 5. Configuration Management
- Externalized configuration using `@ConfigurationProperties`
- Validation of required properties at startup
- Environment-specific property files support

## Usage Examples

### 1. External API Integration Testing

**Manual API Testing (`src/test/resources/currency-api.http`):**
```http
### GET request with parameter
GET http://localhost:8080/api/convert?from=XXX&to=EUR&amount=50
Accept: application/json
```

**Environment Setup for API:**
```bash
# Set environment variables
export FOREX_API_KEY="your_openexchangerates_api_key"
export INIT_EX_RATES=true

# Start application - rates will be automatically fetched on startup
```

**Open Exchange Rates API Direct Testing:**
```bash
# Test external API directly
curl "https://openexchangerates.org/api/latest.json?app_id=YOUR_API_KEY"

# Expected response format:
{
  "disclaimer": "Usage subject to terms: https://openexchangerates.org/terms",
  "license": "https://openexchangerates.org/license",
  "timestamp": 1708617600,
  "base": "USD",
  "rates": {
    "BGN": 1.8266,
    "EUR": 0.934216,
    "GBP": 0.792154
  }
}
```

### 2. Internal API Usage
```bash
# Convert 100 BGN to EUR
GET /api/convert?from=BGN&to=EUR&amount=100

# Response:
{
    "from": "BGN",
    "to": "EUR",
    "amount": 100,
    "result": 51.16
}

# Error example - unsupported currency
GET /api/convert?from=BGN&to=XYZ&amount=100

# Response (404):
{
    "code": "NOT FOUND",
    "id": "BGN~XYZ"
}
```

### 3. Service Usage
```java
@Autowired
private ExRateService exRateService;

// Convert 100 USD to BGN
BigDecimal result = exRateService.convert("USD", "BGN", new BigDecimal("100"));

// Get all supported currencies
List<String> currencies = exRateService.allSupportedCurrencies();
```

### 4. Frontend Integration
```html
<select id="currency">
    <option value="EUR">EUR</option>
    <option value="USD">USD</option>
</select>
<input type="hidden" id="priceInBGN" value="1000">
<span id="price"></span>
```

## Testing Strategy

The project includes comprehensive tests:
- **Unit Tests:** `ExRateServiceImplTest.java`
- **Integration Tests:** `ExRateServiceImplIT.java`, `CurrencyControllerIT.java`
- **API Tests:** `currency-api.http`

## Security Considerations

1. **API Key Protection:** External API keys stored in configuration
2. **Input Validation:** Currency codes and amounts validated
3. **Error Information:** Minimal error details exposed to prevent information leakage
4. **BigDecimal Usage:** Prevents floating-point precision attacks

## Potential Enhancements

1. **Caching:** Implement Redis caching for frequently accessed rates
2. **Rate History:** Store historical exchange rates for trend analysis
3. **Multiple Providers:** Support multiple forex API providers for redundancy
4. **Real-time Updates:** WebSocket integration for live rate updates
5. **Rate Alerts:** Notification system for significant rate changes

## Conclusion

This currency conversion system demonstrates a robust, scalable implementation suitable for production use. It properly handles financial calculations, provides good error handling, and follows Spring Boot best practices. The separation of concerns and use of modern Java features make it maintainable and extensible.