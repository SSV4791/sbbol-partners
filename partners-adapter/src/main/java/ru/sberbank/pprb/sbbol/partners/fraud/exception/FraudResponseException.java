package ru.sberbank.pprb.sbbol.partners.fraud.exception;

import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;

public class FraudResponseException extends RuntimeException {

    private static final String ERROR_MESSAGE = "fraud.response.exception";

    public FraudResponseException(String message) {
        super(MessagesTranslator.toLocale(ERROR_MESSAGE, message));
    }
}
