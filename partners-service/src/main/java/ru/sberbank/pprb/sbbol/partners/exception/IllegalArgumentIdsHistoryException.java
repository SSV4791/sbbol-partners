package ru.sberbank.pprb.sbbol.partners.exception;

public class IllegalArgumentIdsHistoryException extends IllegalArgumentException {

    private static final String IDS_HISTORY_EXCEPTION_MESSAGE_ALREADY_EXIST = "Не уникальный ExternalId";

    public IllegalArgumentIdsHistoryException() {
        super(IDS_HISTORY_EXCEPTION_MESSAGE_ALREADY_EXIST);
    }
}
