package ru.sberbank.pprb.sbbol.partners.replication.agent.impl;

import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.replication.agent.ReplicationAgent;
import ru.sberbank.pprb.sbbol.partners.replication.agent.ReplicationAgentRegistry;
import ru.sberbank.pprb.sbbol.partners.replication.agent.ReplicationEntityDefiner;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Loggable
public class ReplicationAgentRegistryImpl implements ReplicationAgentRegistry {

    private final Map<ReplicationEntityType, ReplicationAgent> registry;

    public ReplicationAgentRegistryImpl(List<ReplicationAgent> agents) {
        this.registry = CollectionUtils.isEmpty(agents)
            ? Collections.emptyMap()
            : agents.stream().collect(Collectors.toMap(ReplicationEntityDefiner::getReplicationEntityType, it -> it));
    }

    @Override
    public Optional<ReplicationAgent> findAgent(ReplicationEntityType entityType) {
        return Optional.ofNullable(registry.get(entityType));
    }
}
