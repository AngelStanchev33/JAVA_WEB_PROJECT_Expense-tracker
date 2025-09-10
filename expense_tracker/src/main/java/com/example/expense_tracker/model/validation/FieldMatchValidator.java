package com.example.expense_tracker.model.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import java.util.Objects;

/**
 * Validator за проверка дали две полета в обект са равни.
 * Използва се за validation на password и confirmPassword полета.
 */
public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {

    // Имената на полетата, които ще се сравняват
    private String first;   // Първото поле (напр. "password")
    private String second;  // Второто поле (напр. "confirmPassword") 
    private String message; // Custom съобщението за грешка

    /**
     * Инициализира validator-а с данни от @FieldMatch анотацията
     */
    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        this.first = constraintAnnotation.first();   // Взема името на първото поле
        this.second = constraintAnnotation.second(); // Взема името на второто поле
        this.message = constraintAnnotation.message(); // Взема custom message-а
    }

    /**
     * Проверява дали двете полета са равни
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // Създава BeanWrapper за достъп до properties на обекта (RegisterRequestDto)
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(value);

        // Взема стойностите на двете полета чрез reflection
        Object firstPropertyValue = beanWrapper.getPropertyValue(first);   // password стойност
        Object secondPropertyValue = beanWrapper.getPropertyValue(second); // confirmPassword стойност

        // Сравнява стойностите за равенство (включително null values)
        boolean isValid = Objects.equals(firstPropertyValue, secondPropertyValue);

        // Ако полетата НЕ са равни, създава custom error message
        if (!isValid) {
            context
                    .buildConstraintViolationWithTemplate(message) // Използва custom message-а
                    .addPropertyNode(second)                        // Прикача грешката към второто поле
                    .addConstraintViolation()                       // Създава violation-а
                    .disableDefaultConstraintViolation();          // Изключва default message-а
        }

        return isValid; // Връща true ако полетата са равни, false ако не са
    }
}
