package ru.sberbank.pprb.sbbol.partners.exception;

import java.util.UUID;

/**
 * Ошибка, указывающая на отсутствие записи в БД
 */
public class EntryNotFoundException extends RuntimeException {

    public EntryNotFoundException(String entity, String digitalId) {
        super(String.format("Искомая сущность %s c digitalId: %s не найдена", entity, digitalId));
    }

    public EntryNotFoundException(String entity, UUID id) {
        super(String.format("Искомая сущность %s c id: %s не найдена", entity, id));
    }

    public EntryNotFoundException(String entity, String digitalId, String id) {
        super(String.format("Искомая сущность %s c с id: %s, digitalId: %s не найдена", entity, id, digitalId));
    }
}
