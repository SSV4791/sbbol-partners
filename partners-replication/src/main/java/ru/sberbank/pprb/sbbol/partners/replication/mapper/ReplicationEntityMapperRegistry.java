package ru.sberbank.pprb.sbbol.partners.replication.mapper;

import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ReplicationEntityMapperRegistry {

    Optional<ReplicationEntityMapper> findMapper(ReplicationEntityType entityType);

    Set<ReplicationEntityType> getReplicationEntityTypeSet();

    List<ReplicationEntityMapper> getReplicationEntityMapperList();
}
