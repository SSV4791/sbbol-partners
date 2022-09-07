package ru.sberbank.pprb.sbbol.partners.validation.common;

import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.partners.model.PatternValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.regex.Pattern;

public class PatternValidator extends BaseValidator
    implements ConstraintValidator<PatternValidation, String> {

    private String message;
    private Pattern pattern;

    @Override
    public void initialize(PatternValidation constraintAnnotation) {
        message = constraintAnnotation.message();
        pattern = Pattern.compile(constraintAnnotation.regexp());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(value)) {
            return true;
        }
        if (!pattern.matcher(value).matches()) {
            if ("{javax.validation.constraints.Pattern.message}".equals(message)) {
                buildMessage(context, message);
                return false;
            }
            var invalidCharsArray = pattern.split(value);
            var invalidChars = new StringBuilder();
            Arrays.stream(invalidCharsArray).forEach(invalidChars::append);
            buildMessage(context, message + " " + invalidChars);
            return false;
        }
        return true;
    }
}
