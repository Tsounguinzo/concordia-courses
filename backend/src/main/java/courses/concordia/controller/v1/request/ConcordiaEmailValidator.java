package courses.concordia.controller.v1.request;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ConcordiaEmailValidator implements ConstraintValidator<ConcordiaEmail, String> {
    @Override
    public void initialize(ConcordiaEmail constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.endsWith("concordia.ca");
    }
}
