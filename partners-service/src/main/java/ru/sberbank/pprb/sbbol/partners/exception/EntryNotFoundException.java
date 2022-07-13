package ru.sberbank.pprb.sbbol.partners.exception;

import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;

import java.util.List;
import java.util.UUID;

/**
 * Ошибка, указывающая на отсутствие записи в БД
 */
public class EntryNotFoundException extends RuntimeException {

    public static final String MESSAGE_ERROR = "error.message.entity.id.digital_id.not_found";

    public EntryNotFoundException(String entity, String digitalId) {
        super(MessagesTranslator.toLocale("error.message.entity.digital_id.not_found", entity, digitalId));
    }

    public EntryNotFoundException(String entity, UUID id) {
        super(MessagesTranslator.toLocale("error.message.entity.id.not_found", entity, id));
    }

    public EntryNotFoundException(String entity, List<UUID> id) {
        super(MessagesTranslator.toLocale("error.message.entity.id.not_found", entity, id));
    }

    public EntryNotFoundException(String entity, String digitalId, String id) {
        super(MessagesTranslator.toLocale(MESSAGE_ERROR, entity, id, digitalId));
    }
}
