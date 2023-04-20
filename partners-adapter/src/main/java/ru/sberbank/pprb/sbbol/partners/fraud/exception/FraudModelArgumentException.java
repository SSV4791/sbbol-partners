package ru.sberbank.pprb.sbbol.partners.fraud.exception;

import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;

public class FraudModelArgumentException extends RuntimeException {

    private static final String ERROR_MESSAGE = "fraud.model_argument.exception";

    public FraudModelArgumentException(String message, Throwable cause) {
        super(MessagesTranslator.toLocale(ERROR_MESSAGE, message), cause);
    }
}
