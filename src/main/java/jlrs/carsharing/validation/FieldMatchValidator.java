package jlrs.carsharing.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String field;
    private String fieldMatch;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.fieldMatch = constraintAnnotation.fieldMatch();
    }

    @Override
    public boolean isValid(
            Object object,
            ConstraintValidatorContext constraintValidatorContext
    ) {
        Object fieldValue = new BeanWrapperImpl(object)
                .getPropertyValue(field);
        Object fieldMathValue = new BeanWrapperImpl(object)
                .getPropertyValue(fieldMatch);

        if (fieldValue != null) {
            return fieldValue.equals(fieldMathValue);
        } else {
            return fieldMathValue == null;
        }
    }
}
