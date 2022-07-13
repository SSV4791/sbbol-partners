package ru.sberbank.pprb.sbbol.partners.validation;

import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhoneAttributeValidation implements ConstraintValidator<ru.sberbank.pprb.sbbol.partners.model.PhoneValidation, String> {

    private static final String SEARCH_SYMBOL = "+";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.hasText(value)) {
            String phone = value;
            var index = value.indexOf(SEARCH_SYMBOL);
            var lastIndex = value.lastIndexOf(SEARCH_SYMBOL);
            if (lastIndex != index) {
                return false;
            } else if (index == 0) {
                phone = value.substring(1);
            }
            char[] phoneNumberValidation = new char[phone.length()];
            phone.getChars(0, phone.length(), phoneNumberValidation, 0);
            for (char number : phoneNumberValidation) {
                if (number < '0' || number > '9') {
                    return false;
                }
            }
        }
        return true;
    }
}
