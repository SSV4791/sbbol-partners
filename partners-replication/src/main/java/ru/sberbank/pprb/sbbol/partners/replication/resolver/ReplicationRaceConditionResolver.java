package ru.sberbank.pprb.sbbol.partners.replication.resolver;

import ru.sberbank.pprb.sbbol.partners.replication.entity.ReplicationEntity;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType;

import java.util.UUID;

public interface ReplicationRaceConditionResolver {

    void resolve(ReplicationEntityType entityType, UUID entityId, String digitalId);

    void resolve(ReplicationEntity entity);
}
