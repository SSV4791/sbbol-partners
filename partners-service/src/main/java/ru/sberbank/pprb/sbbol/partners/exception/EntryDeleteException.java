package ru.sberbank.pprb.sbbol.partners.exception;

import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;

import java.util.UUID;

public class EntryDeleteException extends RuntimeException {

    public EntryDeleteException(String entity, UUID signUuid, Throwable cause) {
        super(MessagesTranslator.toLocale("error.message.entry.delete", entity, signUuid), cause);
    }
}
