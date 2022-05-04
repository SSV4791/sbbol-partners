package ru.sberbank.pprb.sbbol.partners.exception;

public class EntrySaveException extends RuntimeException {

    public EntrySaveException(String entity, Throwable cause) {
        super(String.format("Ошибка при сохранении сущности %s", entity), cause);
    }
}
