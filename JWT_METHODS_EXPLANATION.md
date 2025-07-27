# JWT Service Methods Explanation

## Основни концепции

**JWT Token** съдържа 3 части:
- **Header** - тип на token и алгоритъм за подписване
- **Payload (Claims)** - данните (email, expiration, custom data)
- **Signature** - подпис за проверка на валидността

## Методи в JwtServiceImpl

### 1. `extractAllClaims(String token)` 
```java
private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
}
```

**Какво прави:**
- Взима JWT token като String
- Парсира го и проверява подписа
- **Връща ВСИЧКИ данни** от token-а като Claims обект

**Claims обект съдържа:**
- `subject` - обикновено email на потребителя
- `issuedAt` - кога е създаден token-а
- `expiration` - кога изтича token-а
- `notBefore` - от кога е валиден
- Custom fields - всички допълнителни данни

### 2. `extractClaims(String token, Function<Claims, T> claimsResolver)`
```java
private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);  // Взима всички данни
    return claimsResolver.apply(claims);            // Връща само това което искаме
}
```

**Какво прави:**
- Използва `extractAllClaims()` за да вземе всички данни
- Прилага функцията `claimsResolver` за да извлече конкретната информация
- **Връща само едно конкретно поле** от Claims-а

**Generic `<T>`:** Означава че методът може да върне всякакъв тип данни - String, Date, Integer, etc.

## Примери за употреба

### Извличане на email (Subject):
```java
String email = extractClaims(token, Claims::getSubject);
// Същото като: claims.getSubject()
```

### Извличане на expiration date:
```java
Date expiration = extractClaims(token, Claims::getExpiration);
// Същото като: claims.getExpiration()
```

### Извличане на custom поле:
```java
String role = extractClaims(token, claims -> claims.get("role"));
// Същото като: claims.get("role")
```

### Извличане на issued date:
```java
Date issuedAt = extractClaims(token, Claims::getIssuedAt);
// Същото като: claims.getIssuedAt()
```

## Защо са разделени на 2 метода?

### 1. **Повторна употреба:** 
`extractAllClaims()` парсира token-а само веднъж, след това `extractClaims()` може да извлича различни полета без да парсира отново.

### 2. **Абстракция:** 
`extractClaims()` предоставя лесен начин да извлечеш конкретна информация без да знаеш как точно се парсира token-а.

### 3. **Type Safety:** 
Generic `<T>` гарантира че ще получиш правилния тип данни.

## Практически пример:

```java
// В extractEmail() метода:
public String extractEmail(String token) {
    return extractClaims(token, Claims::getSubject);
}

// В getExpirationDate() метода:  
private Date getExpirationDate(String token) {
    return extractClaims(token, Claims::getExpiration);
}

// В isTokenExpired() метода:
private boolean isTokenExpired(String token) {
    return getExpirationDate(token).before(new Date());
}
```

## Как работи `Function<Claims, T>`?

`Function<Claims, T>` е functional interface който приема Claims и връща тип T:

- `Claims::getSubject` → връща String
- `Claims::getExpiration` → връща Date  
- `claims -> claims.get("role")` → връща Object

Това е Lambda/Method Reference синтаксис в Java.

---

# JWT Token Validation Deep Dive

## Какво точно проверява `isTokenValid(String token)`?

### Скритите проверки зад `isTokenExpired()`:

Когато извикваме `isTokenExpired(token)`, това задейства цяла верига от проверки:

1. **`isTokenExpired(token)`** →
2. **`getExpirationDate(token)`** →  
3. **`extractClaims(token, Claims::getExpiration)`** →
4. **`extractAllClaims(token)`** →
5. **`Jwts.parser().verifyWith(getSigningKey()).parseSignedClaims(token)`**

### Детайлни проверки в `extractAllClaims()`:

```java
private Claims extractAllClaims(String token) {
    return Jwts
        .parser()                    // Създава JWT parser
        .verifyWith(getSigningKey()) // Проверява подписа със secret key
        .build()                     // Билдва parser-а
        .parseSignedClaims(token)    // ПАРСИРА И ВАЛИДИРА token-а
        .getPayload();               // Връща само данните (Claims)
}
```

### Какво се случва в `parseSignedClaims(token)`:

#### 1. **Format Validation:**
- Проверява дали token има 3 части: `header.payload.signature`
- Проверява дали всяка част е валиден Base64
- Проверява дали header и payload са валиден JSON

#### 2. **Signature Verification:**
- Взима header + payload от token-а
- Пресмята signature с нашия secret key
- Сравнява с signature-а в token-а
- Ако не съвпадат → `SignatureException`

#### 3. **Claims Validation:**
- Парсира payload-а като JSON
- Проверява mandatory полета (iat, exp, sub, etc.)
- Проверява дали типовете данни са правилни

#### 4. **Time-based Validation:**
- Проверява `notBefore` (nbf) - дали token-ът вече е активен
- JWT библиотеката може да хвърли `ExpiredJwtException` дори преди да стигнем до нашата проверка

## Възможни Exceptions:

### `MalformedJwtException`
```
Причина: Token format е грешен
Пример: "invalid.token" вместо "header.payload.signature"
```

### `SignatureException`  
```
Причина: Token-ът е подписан с различен secret key
Пример: Някой се опитва да подправи token-а
```

### `ExpiredJwtException`
```
Причина: Token-ът е изтекъл според JWT стандарта
Пример: exp field е по-малко от текущото време
```

### `IllegalArgumentException`
```
Причина: Token е null, празен или има невалидни символи
Пример: token = null или token = ""
```

### `UnsupportedJwtException`
```
Причина: Token типът не се поддържа
Пример: Unsecured JWT tokens (без signature)
```

## Защо се използва Generic Exception Handling?

```java
try {
    return !isTokenExpired(token);
} catch (Exception e) {  // Хваща ВСИЧКИ грешки
    return false;
}
```

### Предимства:
- **Простота:** Един catch block вместо 5-6
- **Сигурност:** Всяка непредвидена грешка се третира като невалиден token
- **Maintenance:** По-лесно за поддръжка

### Алтернатива (по-verbose):
```java
try {
    return !isTokenExpired(token);
} catch (MalformedJwtException e) {
    log.warn("Malformed JWT token: {}", e.getMessage());
    return false;
} catch (SignatureException e) {
    log.warn("Invalid JWT signature: {}", e.getMessage());
    return false;
} catch (ExpiredJwtException e) {
    log.warn("JWT token expired: {}", e.getMessage());
    return false;
} catch (IllegalArgumentException e) {
    log.warn("JWT token compact of handler are invalid: {}", e.getMessage());
    return false;
} catch (UnsupportedJwtException e) {
    log.warn("JWT token is unsupported: {}", e.getMessage());
    return false;
}
```

## Заключение

`isTokenValid()` изглежда прост, но всъщност прави **всичко необходимо за пълна JWT валидация:**
- Format проверка
- Signature проверка  
- Expiration проверка
- Type safety проверки

Ето защо един `try-catch` е достатъчен - JWT библиотеката прави тежката работа за нас!