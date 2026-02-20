package jlrs.carsharing.validation.user;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jlrs.carsharing.validation.user.impl.NameValidator;

@Constraint(validatedBy = NameValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Name {
    String message() default "Invalid name length";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
