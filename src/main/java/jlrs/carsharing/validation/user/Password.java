package jlrs.carsharing.validation.user;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jlrs.carsharing.validation.user.impl.PasswordValidator;

@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
    String message() default "Invalid password length";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

