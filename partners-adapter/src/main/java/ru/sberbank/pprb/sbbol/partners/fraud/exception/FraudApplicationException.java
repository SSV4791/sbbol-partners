package ru.sberbank.pprb.sbbol.partners.fraud.exception;

import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;

public class FraudApplicationException extends RuntimeException {

    private static final String ERROR_MESSAGE = "fraud.application.exception";

    public FraudApplicationException(String message, Throwable cause) {
        super(MessagesTranslator.toLocale(ERROR_MESSAGE, message), cause);
    }
}
