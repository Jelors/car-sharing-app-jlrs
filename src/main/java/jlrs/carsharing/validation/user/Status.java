package jlrs.carsharing.validation.user;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jlrs.carsharing.validation.user.impl.StatusValidator;

@Constraint(validatedBy = StatusValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Status {
    Class<? extends Enum<?>> enumClass();

    String message() default "Invalid user role status!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
