package ru.sberbank.pprb.sbbol.partners.replication.exception;

import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType;

public class NotFoundReplicationAgentException extends RuntimeException {

    public NotFoundReplicationAgentException(ReplicationEntityType entityType) {
        super("Отсутствует агент репликации для типа реплицируемой сущности : " + entityType);
    }
}
