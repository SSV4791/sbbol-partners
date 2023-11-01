package ru.sberbank.pprb.sbbol.partners.replication.agent;

import ru.sberbank.pprb.sbbol.partners.replication.entity.ReplicationEntity;

import java.util.UUID;

public interface ReplicationAgent extends ReplicationEntityDefiner {

    void replicate(ReplicationEntity entity, UUID sessionId);
}
