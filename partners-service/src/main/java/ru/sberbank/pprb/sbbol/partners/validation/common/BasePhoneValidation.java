package ru.sberbank.pprb.sbbol.partners.validation.common;

import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import java.util.Map;

import java.util.List;

import static ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidation.setError;

public final class BasePhoneValidation {
    private static final int PHONE_MAX_LENGTH_VALIDATION = 12;
    private static final String SEARCH_SYMBOL = "+";
    private static final String DOCUMENT = "phone";
    private static final String DEFAULT_MESSAGE_PHONE_LENGTH = "phone.phone_length";
    private static final String DEFAULT_MESSAGE_PHONE_VALID = "phone.phone_valid";

    private BasePhoneValidation() {
        throw new AssertionError();
    }

    public static void commonValidationChildPhone(Map<String, List<String>> errors, String entityPhone) {
        if (StringUtils.isNotEmpty(entityPhone)) {
            if (entityPhone.length() > PHONE_MAX_LENGTH_VALIDATION) {
                setError(errors, DOCUMENT, MessagesTranslator.toLocale(DEFAULT_MESSAGE_PHONE_LENGTH, "12"));
            } else {
                String phone = entityPhone;
                var index = entityPhone.indexOf(SEARCH_SYMBOL);
                var lastIndex = entityPhone.lastIndexOf(SEARCH_SYMBOL);
                if (lastIndex != index) {
                    setError(errors, DOCUMENT, MessagesTranslator.toLocale(DEFAULT_MESSAGE_PHONE_VALID));
                } else if (index == 0) {
                    phone = entityPhone.substring(1);
                }
                char[] phoneNumberValidation = new char[phone.length()];
                phone.getChars(0, phone.length(), phoneNumberValidation, 0);
                for (char number : phoneNumberValidation) {
                    if (number < '0' || number > '9') {
                        setError(errors, DOCUMENT, MessagesTranslator.toLocale(DEFAULT_MESSAGE_PHONE_VALID));
                        break;
                    }
                }
            }
        }
    }
}
