package ru.sberbank.pprb.sbbol.partners.replication.handler.impl;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.replication.entity.ReplicationEntity;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityStatus;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType;
import ru.sberbank.pprb.sbbol.partners.replication.handler.ReplicationEntityHandler;
import ru.sberbank.pprb.sbbol.partners.replication.agent.ReplicationAgent;
import ru.sberbank.pprb.sbbol.partners.replication.agent.ReplicationAgentRegistry;
import ru.sberbank.pprb.sbbol.partners.replication.exception.NotFoundReplicationAgentException;
import ru.sberbank.pprb.sbbol.partners.replication.resolver.ReplicationRaceConditionResolver;

@Loggable
public class ReplicationEntityHandlerImpl implements ReplicationEntityHandler {

    private final ReplicationAgentRegistry agentRegistry;

    private final ReplicationRaceConditionResolver raceConditionResolver;

    public ReplicationEntityHandlerImpl(
        ReplicationAgentRegistry agentRegistry,
        ReplicationRaceConditionResolver raceConditionResolver
        ) {
        this.agentRegistry = agentRegistry;
        this.raceConditionResolver = raceConditionResolver;
    }

    @Transactional
    @Override
    public void handle(ReplicationEntity entity) {
        var agent = getAgent(entity.getEntityType());
        agent.replicate(entity);
        if (entity.getEntityStatus() == ReplicationEntityStatus.SUCCESS) {
            raceConditionResolver.resolve(entity);
        }
    }

    private ReplicationAgent getAgent(ReplicationEntityType entityType) {
        return agentRegistry.findAgent(entityType)
            .orElseThrow(() -> new NotFoundReplicationAgentException(entityType));
    }
}
