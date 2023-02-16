package ru.sberbank.pprb.sbbol.partners.replication.agent;

import ru.sberbank.pprb.sbbol.partners.replication.entity.ReplicationEntity;

public interface ReplicationAgent extends ReplicationEntityDefiner {

    void replicate(ReplicationEntity entity);
}
