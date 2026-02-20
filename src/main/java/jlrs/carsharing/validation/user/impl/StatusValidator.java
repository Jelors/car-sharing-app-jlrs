package jlrs.carsharing.validation.user.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import jlrs.carsharing.validation.user.Status;

public class StatusValidator implements ConstraintValidator<Status, Object> {
    private Set<String> values;

    @Override
    public void initialize(Status constraintAnnotation) {
        values = Arrays.stream(constraintAnnotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(Object value,
                           ConstraintValidatorContext constraintValidatorContext
    ) {
        if (value == null) {
            return false;
        }
        String stringValue = (value instanceof Enum) ? ((Enum<?>) value).name() : value.toString();
        return values.contains(stringValue);
    }

}
