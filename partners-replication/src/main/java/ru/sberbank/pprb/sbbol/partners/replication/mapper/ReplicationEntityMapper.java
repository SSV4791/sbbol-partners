package ru.sberbank.pprb.sbbol.partners.replication.mapper;

import ru.sberbank.pprb.sbbol.partners.replication.entity.ReplicationEntity;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType;

import java.util.UUID;

public interface ReplicationEntityMapper<T> extends ReplicationEntityDefiner {

    ReplicationEntity map(
        String digitalId,
        UUID entityId,
        ReplicationEntityType entityType,
        T entity
    );
}
