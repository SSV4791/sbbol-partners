package ru.sberbank.pprb.sbbol.partners.replication.mapper.impl;

import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType;
import ru.sberbank.pprb.sbbol.partners.replication.mapper.ReplicationEntityDefiner;
import ru.sberbank.pprb.sbbol.partners.replication.mapper.ReplicationEntityMapper;
import ru.sberbank.pprb.sbbol.partners.replication.mapper.ReplicationEntityMapperRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Loggable
public class ReplicationEntityMapperRegistryImpl implements ReplicationEntityMapperRegistry {

    private final Map<ReplicationEntityType, ReplicationEntityMapper> registry;

    public ReplicationEntityMapperRegistryImpl(List<ReplicationEntityMapper> mappers) {
        this.registry = CollectionUtils.isEmpty(mappers)
            ? Collections.emptyMap()
            : mappers.stream().collect(Collectors.toMap(ReplicationEntityDefiner::getReplicationEntityType, it -> it));
    }

    @Override
    public Optional<ReplicationEntityMapper> findMapper(ReplicationEntityType entityType) {
        return Optional.ofNullable(registry.get(entityType));
    }

    @Override
    public Set<ReplicationEntityType> getReplicationEntityTypeSet() {
        return registry.keySet();
    }

    @Override
    public List<ReplicationEntityMapper> getReplicationEntityMapperList() {
        return new ArrayList<>(registry.values());
    }
}
