# Spring SpEL (Spring Expression Language) Implementation в Mobilele проекта

## Общ преглед

В този проект е имплементирана кастомна Spring Security архитектура използваща Spring Expression Language (SpEL) за контрол на достъпа до ресурсите. Имплементацията позволява проверка дали текущо аутентикираният потребител е собственик на дадена оферта преди да може да я изтрие.

## Архитектура на имплементацията

### 1. Конфигурационни класове

#### MethodSecurityConfig.java
```java
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration
```

**Местоположение:** `src/main/java/bg/softuni/mobilele/config/MethodSecurityConfig.java`

**Отговорности:**
- Активира глобалната метод секюрити с `@EnableGlobalMethodSecurity(prePostEnabled = true)`
- Наследява `GlobalMethodSecurityConfiguration` за да предостави кастомна конфигурация
- Презаписва `createExpressionHandler()` метода за да регистрира кастомния expression handler
- Инжектира `OfferService` чрез `@Autowired` за да го предостави на expression handler-а

**Ключови методи:**
- `createExpressionHandler()` - създава и връща нов `MobileleMethodSecurityExpressionHandler`

#### MobileleMethodSecurityExpressionHandler.java
```java
public class MobileleMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler
```

**Местоположение:** `src/main/java/bg/softuni/mobilele/config/MobileleMethodSecurityExpressionHandler.java`

**Отговорности:**
- Наследява `DefaultMethodSecurityExpressionHandler` за да разшири функционалността
- Създава кастомни security expression root обекти
- Инжектира зависимости в expression root обектите
- Настройва различни компоненти като `PermissionEvaluator`, `TrustResolver`, `RoleHierarchy`

**Ключови методи:**
- `createSecurityExpressionRoot(Authentication, MethodInvocation)` - създава нов `OwnerSecurityExpressionClass` с необходимите зависимости

### 2. Кастомен Expression Root клас

#### OwnerSecurityExpressionClass.java
```java
public class OwnerSecurityExpressionClass extends SecurityExpressionRoot implements MethodSecurityExpressionOperations
```

**Местоположение:** `src/main/java/bg/softuni/mobilele/config/OwnerSecurityExpressionClass.java`

**Отговорности:**
- Наследява `SecurityExpressionRoot` за достъп до основните security операции
- Имплементира `MethodSecurityExpressionOperations` за метод-специфична функционалност
- Дефинира кастомни SpEL методи, достъпни в `@PreAuthorize` анотациите
- Управлява filter и return обекти за SpEL израженията

**Кастомни SpEL методи:**

##### `isOwner(Long id)`
- **Параметри:** `id` - ID на офертата за проверка
- **Връща:** `boolean` - true ако текущият потребител е собственик на офертата
- **Логика:**
  1. Извлича username на текущия потребител чрез `currentUserName()`
  2. Валидира че username и id не са null
  3. Извиква `offerService.isOwner(id, username)` за да провери собствеността
  4. Логва всички стъпки за debug целите

##### `currentUserName()`
- **Връща:** `String` - username на текущо аутентикирания потребител или null
- **Логика:**
  1. Извлича `Authentication` обекта от Spring Security контекста
  2. Проверява дали principal е от тип `UserDetails`
  3. Връща username-а ако е валиден, иначе null

**Имплементирани методи от MethodSecurityExpressionOperations:**
- `setFilterObject(Object)` / `getFilterObject()` - за филтриране на колекции
- `setReturnObject(Object)` / `getReturnObject()` - за валидиране на връщани стойности
- `getThis()` - връща текущия обект

### 3. Бизнес логика

#### OfferService.isOwner(Long, String)
**Местоположение:** `src/main/java/bg/softuni/mobilele/service/OfferService.java:87-97`

```java
public boolean isOwner(Long offerId, String username) {
    OfferEntity offer = offerRepository.findById(offerId)
        .orElseThrow(() -> new ObjectNotFoundException("Offer with id " + offerId + " not found"));
    
    UserEntity user = userRepository.findByUsername(username)
        .orElseThrow(() -> new ObjectNotFoundException("User with username" + username + "not found"));
    
    return offer.getSeller().getId() == user.getId();
}
```

**Отговорности:**
- Намира офертата по подадения ID
- Намира потребителя по подадения username
- Сравнява ID-тата на продавача на офертата и текущия потребител
- Хвърля `ObjectNotFoundException` ако офертата или потребителят не са намерени

## 4. Използване в контролерите

#### OffersController.java
**Местоположение:** `src/main/java/bg/softuni/mobilele/web/OffersController.java:81-86`

```java
@PreAuthorize("isOwner(#p0)")
@PostMapping("/delete/{id}")
public String deleteOffer(@PathVariable("id") Long id) {
    offerService.deleteOffer(id);
    return "redirect:/offers/all";
}
```

**SpEL израз обяснение:**
- `@PreAuthorize("isOwner(#p0)")` - извиква кастомния `isOwner` метод
- `#p0` - референцира първия параметър на метода (`@PathVariable("id") Long id`)
- Методът се изпълнява само ако `isOwner(id)` върне `true`
- Ако проверката не премине, Spring Security хвърля `AccessDeniedException`

## Поток на изпълнение

### Когато потребител опита да изтрие оферта:

1. **HTTP заявка:** POST към `/offers/delete/{id}`

2. **Spring Security интерцепция:** Spring Security интерцептва заявката преди да достигне до контролера

3. **SpEL парсиране:** `@PreAuthorize("isOwner(#p0)")` се парсира и:
   - `#p0` се заменя с първия параметър (ID на офертата)
   - `isOwner()` се разпознава като кастомен метод

4. **Expression Handler активиране:** `MobileleMethodSecurityExpressionHandler.createSecurityExpressionRoot()` се извиква

5. **Expression Root създаване:** Нов `OwnerSecurityExpressionClass` се създава с:
   - Текущия `Authentication` обект
   - Инжектиран `OfferService`
   - Конфигурирани security компоненти

6. **SpEL метод изпълнение:** `OwnerSecurityExpressionClass.isOwner(Long id)` се извиква:
   - Извлича username на текущия потребител
   - Извиква `OfferService.isOwner(id, username)`

7. **Бизнес логика проверка:** `OfferService.isOwner()`:
   - Намира офертата в базата данни
   - Намира потребителя в базата данни
   - Сравнява ID-тата на собствениците

8. **Авторизация решение:**
   - **TRUE:** Методът в контролера се изпълнява нормално
   - **FALSE:** `AccessDeniedException` се хвърля, заявката се отхвърля

9. **Логинг:** Всички стъпки се логват за debugging чрез `System.out.println()`

## Предимства на тази имплементация

### 1. **Декларативна секюрити**
- Секюрити правилата са ясно видими в анотациите
- Не е нужен допълнителен код в контролерите за проверки

### 2. **Гъвкавост**
- Лесно добавяне на нови кастомни SpEL методи
- Възможност за сложни authorization логики

### 3. **Разделение на отговорностите**
- Authorization логиката е отделена от бизнес логиката
- Кластърна организация на секюрити конфигурацията

### 4. **Extensibility**
- Наследяване на стандартните Spring Security класове
- Възможност за добавяне на допълнителни security проверки

## Потенциални подобрения

### 1. **Logging**
- Замяна на `System.out.println()` с proper logging framework (SLF4J + Logback)
- Добавяне на различни log levels (DEBUG, INFO, ERROR)

### 2. **Exception Handling**
- По-специфични exception типове за различните грешки
- Globalno exception handling за security грешки

### 3. **Performance**
- Кеширане на често използвани проверки
- Batch операции за множество ownership проверки

### 4. **Security**
- Валидиране на входните параметри
- Rate limiting за security операции

## Зависимости

### Maven/Gradle dependencies:
```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-config</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-web</artifactId>
</dependency>
```

### Използвани Spring Security класове:
- `DefaultMethodSecurityExpressionHandler`
- `SecurityExpressionRoot`
- `MethodSecurityExpressionOperations`
- `GlobalMethodSecurityConfiguration`
- `@EnableGlobalMethodSecurity`
- `@PreAuthorize`

## Заключение

Тази SpEL имплементация предоставя елегантно и мощно решение за method-level security в Spring Boot приложението. Тя позволява декларативен подход за authorization проверки, като същевременно запазва гъвкавостта за сложни бизнес правила. Архитектурата е добре структурирана, следва Spring Security best practices и може лесно да се разширява за допълнителни нужди.