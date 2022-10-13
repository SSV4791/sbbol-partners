package ru.sberbank.pprb.sbbol.partners.exception;

/**
 * Ошибка, указывающая на не верный запрос
 */
public class BadRequestException extends RuntimeException {

    private static final String EXCEPTION = "PPRB:PARTNER:BAD_REQUEST_EXCEPTION";

    private final String code;

    public BadRequestException(Throwable ex) {
        super(ex.getLocalizedMessage(), ex);
        this.code = EXCEPTION;
    }

    public String getCode() {
        return code;
    }
}
