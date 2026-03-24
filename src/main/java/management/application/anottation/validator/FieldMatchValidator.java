package management.application.anottation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import management.application.anottation.FieldMatch;
import org.springframework.beans.BeanWrapperImpl;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstFieldName;
    private String secondFieldName;
    private String message;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
        message = constraintAnnotation.message();

    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        Object firstValue = new BeanWrapperImpl(object).getPropertyValue(firstFieldName);
        Object secondValue = new BeanWrapperImpl(object).getPropertyValue(secondFieldName);

        boolean valid = (firstValue == null && secondValue == null)
                || (firstValue != null && firstValue.equals(secondValue));

        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(secondFieldName)
                    .addConstraintViolation();
        }

        return valid;

    }
}

