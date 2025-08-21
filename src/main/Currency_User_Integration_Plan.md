# Currency User Integration Plan 💱

## Overview
Smart currency integration с user base currency като default value за всички операции.

## Architecture Decisions

### 1. User Base Currency ⭐⭐⭐⭐
- **Registration**: User избира base currency (smart default USD/EUR)
- **Settings**: Може да променя base currency в dashboard/settings
- **Database**: UserEntity.baseCurrencyId foreign key към CurrencyEntity

### 2. Smart Default Currency Flow 🎯

#### Registration
```java
User user = new User();
user.setBaseCurrencyId(2); // EUR избрана при registration
```

#### Create Expense - Auto Default
```json
POST /api/expenses
{
  "amount": 100.50,
  "description": "Grocery",
  "category": "FOOD"
  // currencyId automatically = user.baseCurrencyId (EUR)
}
```

#### Override когато е необходимо
```json
POST /api/expenses
{
  "amount": 100.50,
  "description": "Trip expense", 
  "category": "TRAVEL",
  "currencyId": 1  // Override to USD за този expense
}
```

#### Change Base Currency
```json
PUT /api/user/settings
{
  "baseCurrencyId": 3  // Change to BGN
}
```

### 3. Implementation Logic 🔧

#### Controller Logic
```java
@PostMapping("/expenses")
public ResponseEntity<ExpenseResponseDto> createExpense(@RequestBody CreateExpenseDto dto) {
    // Ако currencyId не е зададен → използвай user.baseCurrencyId
    Long currencyId = dto.getCurrencyId() != null ? 
        dto.getCurrencyId() : 
        getCurrentUser().getBaseCurrencyId();
    
    // Създаване на expense с determined currency
}
```

#### Budget Logic
```java
@PostMapping("/budgets")
public ResponseEntity<BudgetResponseDto> createBudget(@RequestBody CreateBudgetDto dto) {
    // Същата логика - default към user.baseCurrencyId
    Long currencyId = dto.getCurrencyId() != null ? 
        dto.getCurrencyId() : 
        getCurrentUser().getBaseCurrencyId();
}
```

### 4. Display Conversion Strategy 📊

#### Data Integrity Approach
- **Database запазва original валути** - data integrity ✅
- **Display показва в base currency** - user convenience ✅
- **Използваме ExRateService** - existing conversion logic ✅

#### Example Flow
```
DB Storage: Expense = 100 EUR (original)
User Base Currency: BGN
Display: 195.50 BGN (converted при показване)
```

#### Budget Comparison Logic
- **Всичко конвертирано към base currency** за calculations
- **Flexible approach** - expenses в EUR vs budget в BGN работи
- **Real-time conversion** използвайки ExRateService

### 5. Database Changes Needed 📝

#### UserEntity Updates
```java
@Entity
public class UserEntity {
    // existing fields...
    
    @ManyToOne
    @JoinColumn(name = "base_currency_id")
    private CurrencyEntity baseCurrency;
    
    // getters/setters
}
```

#### DTO Updates
```java
// CreateExpenseDto
private Long currencyId; // Optional - defaults to user base

// CreateBudgetDto  
private Long currencyId; // Optional - defaults to user base

// UserSettingsDto
private Long baseCurrencyId; // For updating user base currency
```

### 6. API Endpoints 🌐

#### Mobile-First Dropdown Approach ⭐⭐⭐⭐
**Решение: Simple currency dropdown - НЕ settings page!**

#### New/Updated Endpoints
```
PUT /api/user/currency          // Simple currency update (dropdown)
GET /api/user/profile           // Include base currency info
POST /api/expenses              // Optional currencyId field
POST /api/budgets               // Optional currencyId field
GET /api/currencies             // List available currencies for dropdown
```

#### Currency Dropdown Implementation
```java
// Simple endpoint за dropdown
@PutMapping("/api/user/currency")
@PreAuthorize("hasRole('USER')")  
public ResponseEntity<CurrencyUpdateResponseDto> updateCurrency(@RequestBody @Valid CurrencyUpdateDto dto) {
    String newCurrency = userService.updateUserCurrency(dto.getCurrencyId());
    return ResponseEntity.ok(new CurrencyUpdateResponseDto("Currency updated to " + newCurrency, newCurrency));
}

// GET currencies за dropdown options
@GetMapping("/api/currencies")
public ResponseEntity<List<CurrencyDto>> getAllCurrencies() {
    return ResponseEntity.ok(currencyService.getAllCurrencies());
}
```

### 7. UX Benefits 🎯

#### Mobile-First User Experience 📱
- **Dropdown simplicity** - header currency switch (no settings page!)
- **Instant updates** - all amounts refresh immediately
- **App Store ready** - clean, intuitive mobile UX
- **Minimal friction** - auto-defaults to preferred currency
- **Flexibility** - can override когато е необходимо  
- **Consistency** - all amounts displayed in preferred currency
- **Real-world usability** - matches Airbnb/Booking.com pattern

#### Business Logic
- **Data integrity** - original currencies preserved
- **Audit trail** - знаем истинската валута на transaction
- **Performance** - conversion само при display
- **Scalability** - supports multi-currency users

### 8. Implementation Steps 📋

1. **Database Migration** - add baseCurrencyId to users table
2. **UserEntity Update** - add baseCurrency relationship  
3. **DTO Updates** - add optional currencyId fields + CurrencyUpdateDto
4. **Service Layer** - implement smart default logic
5. **Controller Updates** - handle optional currency fields  
6. **Currency Dropdown API** - simple PUT /api/user/currency endpoint
7. **Display Conversion** - implement conversion при response
8. **GET /api/currencies** - endpoint за dropdown options
9. **Testing** - unit tests за currency logic

#### Mobile-Specific Implementation
- **Header dropdown component** - currency selection UI
- **Real-time refresh** - update all amounts след currency change  
- **Offline support** - cache currency rates за mobile
- **Performance optimization** - minimize API calls

### 9. Interview Highlights 💼

#### Technical Skills Demonstrated
- **Database Design** - proper foreign key relationships
- **API Design** - smart defaults, optional parameters
- **Business Logic** - real-world currency handling
- **User Experience** - thoughtful UX decisions  
- **Data Integrity** - original data preservation
- **Performance** - efficient conversion strategy

#### Architecture Benefits
- **Separation of Concerns** - storage vs display logic
- **Extensibility** - easy to add new currencies
- **Maintainability** - clean, understandable code
- **Production Ready** - handles real-world scenarios

---

## Next Session Tasks
- [ ] Implement UserEntity.baseCurrencyId field
- [ ] Update CreateExpenseDto/CreateBudgetDto
- [ ] Add smart default currency logic
- [ ] Implement display conversion
- [ ] Add user settings API endpoint
- [ ] Testing и validation

*This approach demonstrates enterprise-level thinking about user experience и data integrity.*