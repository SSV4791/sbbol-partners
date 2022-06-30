package ru.sberbank.pprb.sbbol.partners.validation.common;

import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import java.util.Map;

import java.util.List;

import static ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidation.setError;


public final class BaseEmailValidation {
    private static final int EMAIL_MAX_LENGTH_VALIDATION = 320;
    private static final String EMAIL_SYMBOL = "@";
    private static final String DOCUMENT = "email";
    private static final String DEFAULT_MESSAGE_EMAIL_LENGTH = "email.email_length";
    private static final String DEFAULT_MESSAGE_EMAIL_VALID = "email.email_valid";

    private BaseEmailValidation() {
        throw new AssertionError();
    }

    public static void commonValidationChildEmail(Map<String, List<String>> errors, String entityEmail) {
        if (StringUtils.isNotEmpty(entityEmail)) {
            if (entityEmail.length() > EMAIL_MAX_LENGTH_VALIDATION) {
                setError(errors, DOCUMENT, MessagesTranslator.toLocale(DEFAULT_MESSAGE_EMAIL_LENGTH, "320"));
            } else if (!entityEmail.contains(EMAIL_SYMBOL)) {
                setError(errors, DOCUMENT, MessagesTranslator.toLocale(DEFAULT_MESSAGE_EMAIL_VALID));
            } else {
                var index = entityEmail.indexOf(EMAIL_SYMBOL);
                var lastIndex = entityEmail.lastIndexOf(EMAIL_SYMBOL);
                if (lastIndex != index) {
                    setError(errors, DOCUMENT, MessagesTranslator.toLocale(DEFAULT_MESSAGE_EMAIL_VALID));
                }
                var emailDomain = entityEmail.substring(index + 1);
                var localPart = entityEmail.substring(0, index);
                if (localPart.length() > 64 || emailDomain.length() > 255) {
                    setError(errors, DOCUMENT, MessagesTranslator.toLocale(DEFAULT_MESSAGE_EMAIL_VALID));
                }
            }
        }
    }
}
