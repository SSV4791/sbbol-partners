package ru.sberbank.pprb.sbbol.partners.exception;

import java.util.List;

public class ModelValidationException extends RuntimeException {

    private static final String EXCEPTION = "PPRB:PARTNER:MODEL_VALIDATION_EXCEPTION";

    private final String code;

    private final List<String> errors;

    public ModelValidationException(List<String> errors) {
        super("Ошибка прохождения валидации");
        this.code = EXCEPTION;
        this.errors = errors;
    }

    public String getCode() {
        return code;
    }

    public List<String> getErrors() {
        return errors;
    }
}
