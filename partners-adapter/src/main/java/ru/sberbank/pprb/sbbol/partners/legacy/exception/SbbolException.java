package ru.sberbank.pprb.sbbol.partners.legacy.exception;

import org.springframework.http.HttpStatus;

/**
 * Ошибка, при работе с legacy СББОЛ
 */
public class SbbolException extends RuntimeException {

    private static final String EXCEPTION = "SBBOL:PARTNER:ADAPTER_EXCEPTION";

    private final String code;

    public SbbolException(HttpStatus code, String message) {
        super("Error execute http-request to SBBOL: StatusCode: " + code + " , Message: " + message);
        this.code = EXCEPTION;
    }

    public SbbolException(String message, Throwable cause) {
        super("Error sending request to SBBOL: " + message, cause);
        this.code = EXCEPTION;
    }

    public String getCode() {
        return code;
    }
}
