package ru.sberbank.pprb.sbbol.partners.replication.agent;

import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType;

import java.util.Optional;

public interface ReplicationAgentRegistry {

    Optional<ReplicationAgent> findAgent(ReplicationEntityType entityType);
}
