package ru.sberbank.pprb.sbbol.partners.exception;

import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;

public class EntrySaveException extends RuntimeException {

    public EntrySaveException(String entity, Throwable cause) {
        super(MessagesTranslator.toLocale("error.message.entry.save", entity), cause);
    }
}
