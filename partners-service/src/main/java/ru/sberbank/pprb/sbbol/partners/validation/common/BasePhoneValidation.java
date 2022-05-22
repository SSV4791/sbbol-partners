package ru.sberbank.pprb.sbbol.partners.validation.common;

import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;

import java.util.List;

public final class BasePhoneValidation {
    private static final int PHONE_MAX_LENGTH_VALIDATION = 12;
    private static final String SEARCH_SYMBOL = "+";
    private static final String DEFAULT_MESSAGE_FIELD_FORBIDDEN_FORMAT = "default.fields.forbidden.format";
    private static final String DEFAULT_MESSAGE_FIELDS_IS_LENGTH = "default.fields.length";

    private BasePhoneValidation() {
        throw new AssertionError();
    }

    public static void commonValidationChildPhone(List<String> errors, String entityPhone) {
        if (StringUtils.isNotEmpty(entityPhone)) {
            if (entityPhone.length() > PHONE_MAX_LENGTH_VALIDATION) {
                errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_LENGTH, "phone", "1", "12"));
            } else {
                String phone = entityPhone;
                var index = entityPhone.indexOf(SEARCH_SYMBOL);
                var lastIndex = entityPhone.lastIndexOf(SEARCH_SYMBOL);
                if (lastIndex != index) {
                    errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_FORBIDDEN_FORMAT, "phone", "+79995552211"));
                } else if (index == 0) {
                    phone = entityPhone.substring(1);
                }
                char[] phoneNumberValidation = new char[phone.length()];
                phone.getChars(0, phone.length(), phoneNumberValidation, 0);
                for (char number : phoneNumberValidation) {
                    if (number < '0' || number > '9') {
                        errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_FORBIDDEN_FORMAT, "phone", "номер содержит запрещенный символы"));
                        break;
                    }
                }
            }
        }
    }
}
