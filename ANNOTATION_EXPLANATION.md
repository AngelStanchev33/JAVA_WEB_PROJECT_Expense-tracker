# Junior Interview Notes

## Bean Validation - Custom Annotations

### groups() и payload() - Задължителни параметри

```java
Class<?>[] groups() default {};
Class<? extends Payload>[] payload() default {};
```

**За интервю трябва да знам:**

Знам че `groups()` и `payload()` са задължителни параметри в Bean Validation. Groups се използват за условна валидация - например различни правила при create vs update операции. Не съм ги използвал в сложни сценарии, но разбирам концепцията.

### Обяснение:

1. **groups()** - Позволява групиране на валидации
   ```java
   // Различни групи за различни операции
   public interface CreateGroup {}
   public interface UpdateGroup {}
   
   @NotNull(groups = CreateGroup.class)
   @Null(groups = UpdateGroup.class)
   private Long id;
   ```

2. **payload()** - Носи допълнителна информация за валидацията
   ```java
   // Може да съдържа severity level, error codes и т.н.
   @Email(payload = {Severity.Error.class})
   private String email;
   ```

**Практически:** В 90% от случаите се оставят празни с `default {}`, но Bean Validation спецификацията ги изисква за всички анотации.

### Custom Validation - Основно

1. **Създаване на анотация:**
   ```java
   @interface FieldMatch {
       String first();
       String second();
       String message() default "Fields don't match";
       Class<?>[] groups() default {};
       Class<? extends Payload>[] payload() default {};
   }
   ```

2. **Validator клас:**
   ```java
   public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
       public void initialize(FieldMatch annotation) { ... }
       public boolean isValid(Object value, ConstraintValidatorContext context) { ... }
   }
   ```

3. **Използване:**
   ```java
   @FieldMatch(first = "password", second = "confirmPassword")
   public class RegisterDto { ... }
   ```