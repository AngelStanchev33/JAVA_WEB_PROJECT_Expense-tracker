package com.example.expense_tracker.model.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // Кога се изпълнява
@Target(ElementType.TYPE)  // Къде може да се слага (на класа)
@Constraint(validatedBy = FieldMatchValidator.class)

public @interface FieldMatch {

    String first();

    String second();

    String message() default "Passwords do not match";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
