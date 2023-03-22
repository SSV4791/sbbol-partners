package ru.sberbank.pprb.sbbol.partners.exception;

import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;

public class MultipleEntryFoundException extends RuntimeException {

    public static final String MESSAGE_ERROR = "error.message.entity.multiple_found";

    public MultipleEntryFoundException(String entity, String digitalId) {
        super(MessagesTranslator.toLocale(MESSAGE_ERROR, entity, digitalId));
    }
}
