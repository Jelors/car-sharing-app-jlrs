package jlrs.carsharing.validation.user.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jlrs.carsharing.validation.user.Password;

public class PasswordValidator implements ConstraintValidator<Password, String> {
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 24;

    @Override
    public boolean isValid(String password,
                           ConstraintValidatorContext constraintValidatorContext) {
        return password != null && (password.length() > MIN_PASSWORD_LENGTH
                && password.length() <= MAX_PASSWORD_LENGTH);
    }
}