package ru.sberbank.pprb.sbbol.partners.exception;

public class FraudDeniedException extends RuntimeException {

    public FraudDeniedException(String message) {
        super(message);
    }
}
