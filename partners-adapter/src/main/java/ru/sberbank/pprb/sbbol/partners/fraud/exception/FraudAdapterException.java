package ru.sberbank.pprb.sbbol.partners.fraud.exception;

import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;

public class FraudAdapterException extends RuntimeException {

    private static final String ERROR_MESSAGE = "fraud.adapter.exception";

    public FraudAdapterException(String message, Throwable cause) {
        super(MessagesTranslator.toLocale(ERROR_MESSAGE, message), cause);
    }
}
