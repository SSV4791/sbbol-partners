package ru.sberbank.pprb.sbbol.partners.replication.handler;

import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityStatus;

public interface ReplicationQueueHandler {

    void handle(ReplicationEntityStatus entityStatus, int retry);
}
