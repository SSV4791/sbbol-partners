package ru.sberbank.pprb.sbbol.partners.service.replication.impl;

import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.service.replication.ReplicationService;
import ru.sberbank.pprb.sbbol.partners.service.replication.ReplicationServiceRegistry;
import ru.sberbank.pprb.sbbol.partners.service.replication.ReplicationServiceType;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReplicationServiceRegistryImpl implements ReplicationServiceRegistry {

    private final Map<ReplicationServiceType, ReplicationService> registry;

    public ReplicationServiceRegistryImpl(List<ReplicationService> services) {
        this.registry = CollectionUtils.isEmpty(services)
            ? Collections.emptyMap()
            : services.stream().collect(Collectors.toMap(ReplicationService::getServiceType, it -> it));
    }

    @Override
    public Optional<ReplicationService> findService(ReplicationServiceType replicationServiceType) {
        return Optional.ofNullable(registry.get(replicationServiceType));
    }
}
