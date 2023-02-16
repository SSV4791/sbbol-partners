package ru.sberbank.pprb.sbbol.partners.replication.handler;

import ru.sberbank.pprb.sbbol.partners.replication.entity.ReplicationEntity;

public interface ReplicationEntityHandler {

    void handle(ReplicationEntity entity);
}
