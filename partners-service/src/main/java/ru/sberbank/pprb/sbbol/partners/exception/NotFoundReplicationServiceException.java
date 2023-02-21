package ru.sberbank.pprb.sbbol.partners.exception;

import ru.sberbank.pprb.sbbol.partners.service.replication.ReplicationServiceType;

public class NotFoundReplicationServiceException extends RuntimeException {

    public NotFoundReplicationServiceException(ReplicationServiceType serviceType) {
        super("Отсутствует сервис репликации типа : " + serviceType);
    }
}
