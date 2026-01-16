package jlrs.carsharing.validation.user.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jlrs.carsharing.validation.user.Name;

public class NameValidator implements ConstraintValidator<Name, String> {
    private static final int MIN_NAME_LENGTH = 3;

    @Override
    public boolean isValid(String name,
                           ConstraintValidatorContext constraintValidatorContext) {
        return name != null && name.length() >= MIN_NAME_LENGTH;
    }
}
