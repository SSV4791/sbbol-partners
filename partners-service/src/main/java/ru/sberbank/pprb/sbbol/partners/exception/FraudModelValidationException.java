package ru.sberbank.pprb.sbbol.partners.exception;

public class FraudModelValidationException extends RuntimeException {

    public FraudModelValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
