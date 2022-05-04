package ru.sberbank.pprb.sbbol.partners.exception;

import java.util.Collections;
import java.util.List;

/**
 * Ошибка сохранения счёта при условии что он подписан
 */
public class SignAccountException extends RuntimeException {

    private static final String EXCEPTION = "PPRB:PARTNER:SAVE_ACCOUNT_EXCEPTION";

    private final String code;

    private final List<String> errors;

    public SignAccountException(String error) {
        super("Ошибка сохранения счёта");
        this.code = EXCEPTION;
        this.errors = Collections.singletonList(error);
    }
    public SignAccountException(List<String> errors) {
        super("Ошибка сохранения счёта");
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
