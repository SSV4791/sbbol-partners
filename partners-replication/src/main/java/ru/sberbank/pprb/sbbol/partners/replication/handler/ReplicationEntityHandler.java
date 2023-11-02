package ru.sberbank.pprb.sbbol.partners.replication.handler;

import ru.sberbank.pprb.sbbol.partners.replication.entity.ReplicationEntity;

import java.util.UUID;

public interface ReplicationEntityHandler {

    void handle(ReplicationEntity entity, UUID sessionId);
}
