package ru.sberbank.pprb.sbbol.partners.validation.common;

import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;

import java.util.List;


public final class BaseEmailValidation {
    private static final int EMAIL_MAX_LENGTH_VALIDATION = 320;
    private static final String EMAIL_SYMBOL = "@";
    private static final String DEFAULT_MESSAGE_FIELD_FORBIDDEN_FORMAT = "default.fields.forbidden.format";
    private static final String DEFAULT_MESSAGE_FIELDS_IS_LENGTH = "default.fields.length";

    private BaseEmailValidation() {
        throw new AssertionError();
    }

    public static void commonValidationChildEmail(List<String> errors, String entityEmail) {
        if (StringUtils.isNotEmpty(entityEmail)) {
            if (entityEmail.length() > EMAIL_MAX_LENGTH_VALIDATION) {
                errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_LENGTH, "email", "1", "320"));
            } else if (!entityEmail.contains(EMAIL_SYMBOL)) {
                errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_FORBIDDEN_FORMAT, "email", "email@host.domain"));
            } else {
                var index = entityEmail.indexOf(EMAIL_SYMBOL);
                var lastIndex = entityEmail.lastIndexOf(EMAIL_SYMBOL);
                if (lastIndex != index) {
                    errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_FORBIDDEN_FORMAT, "email", "email@host.domain"));
                }
                var emailDomain = entityEmail.substring(index + 1);
                var localPart = entityEmail.substring(0, index);
                if (localPart.length() > 64 || emailDomain.length() > 255) {
                    errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_FORBIDDEN_FORMAT, "email", "{64}@{255}"));
                }
            }
        }
    }
}
