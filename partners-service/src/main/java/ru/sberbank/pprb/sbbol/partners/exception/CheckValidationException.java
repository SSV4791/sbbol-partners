package ru.sberbank.pprb.sbbol.partners.exception;

import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;

import java.util.List;
import java.util.Map;

public class CheckValidationException extends RuntimeException {

    private static final String EXCEPTION = "PPRB:PARTNER:CHECK_VALIDATION_EXCEPTION";

    private final String text;

    private final Map<String, List<String>> errors;

    public CheckValidationException(Map<String, List<String>> errors) {
        super(MessagesTranslator.toLocale("error.message.check.validation"));
        this.text = EXCEPTION;
        this.errors = errors;
    }

    public String getText() {
        return text;
    }

    public Map<String, List<String>> getErrors() {
        return errors;
    }
}
