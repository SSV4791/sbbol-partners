package ru.sberbank.pprb.sbbol.partners.exception;

/**
 * Ошибка, указывающая на отсутствие записи в БД
 */
public class EntryNotFoundException extends RuntimeException {

    public EntryNotFoundException(String entity, String digitalId) {
        super(String.format("Искомая сущность %s c digitalId: %s не найдена", entity, digitalId));
    }

    public EntryNotFoundException(String entity, String digitalId, String id) {
        super(String.format("Искомая сущность %s c с id: %s, digitalId: %s не найдена", entity, id, digitalId));
    }
}
