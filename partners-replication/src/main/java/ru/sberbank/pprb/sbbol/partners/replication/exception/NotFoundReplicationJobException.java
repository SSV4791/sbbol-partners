package ru.sberbank.pprb.sbbol.partners.replication.exception;

import ru.sberbank.pprb.sbbol.partners.replication.job.ReplicationJobType;

public class NotFoundReplicationJobException extends RuntimeException {

    public NotFoundReplicationJobException(ReplicationJobType jobType) {
        super("В реестре планировщика задач на выполнение отсутствует задача типа: " + jobType);
    }
}
