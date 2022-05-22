package ru.sberbank.pprb.sbbol.partners.exception;

import java.util.Collections;
import java.util.List;

public class MissingValueException extends RuntimeException {

    private static final String EXCEPTION = "PPRB:PARTNER:MISSING_VALUE_EXCEPTION";

    private final String code;

    private final List<String> errors;

    public MissingValueException(String error) {
        super("Ошибка переданного значения, значение не найдено в базе данных");
        this.code = EXCEPTION;
        this.errors = Collections.singletonList(error);
    }

    public MissingValueException(List<String> errors) {
        super("Ошибка переданного значения, значение не найдено в базе данных");
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
