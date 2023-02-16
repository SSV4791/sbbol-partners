package ru.sberbank.pprb.sbbol.partners.replication.exception;

import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType;

public class NotFoundReplicationEntityMapperException extends RuntimeException {

    public NotFoundReplicationEntityMapperException(ReplicationEntityType entityType) {
        super("Отсутствует маппер для реплицируемой сущности типа: " + entityType);
    }
}
