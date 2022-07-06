package ru.sberbank.pprb.sbbol.partners.exception;

import java.util.List;
import java.util.Map;

public class ModelValidationException extends RuntimeException {

    private static final String EXCEPTION = "PPRB:PARTNER:MODEL_VALIDATION_EXCEPTION";

    private final String text;

    private final Map<String, List<String>> errors;


    public ModelValidationException(Map<String, List<String>> errors) {
        super("Ошибка прохождения валидации");
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
