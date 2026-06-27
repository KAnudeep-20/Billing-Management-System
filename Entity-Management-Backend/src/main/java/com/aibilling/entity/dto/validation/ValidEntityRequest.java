package com.aibilling.entity.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class-level constraint validation annotation for Entity request DTOs.
 */
@Documented
@Constraint(validatedBy = EntityRequestValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEntityRequest {

    String message() default "Invalid entity request details";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
