package ru.sberbank.pprb.sbbol.partners.replication.handler.impl;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.replication.entity.ReplicationEntity;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType;
import ru.sberbank.pprb.sbbol.partners.replication.handler.ReplicationEntityHandler;
import ru.sberbank.pprb.sbbol.partners.replication.agent.ReplicationAgent;
import ru.sberbank.pprb.sbbol.partners.replication.agent.ReplicationAgentRegistry;
import ru.sberbank.pprb.sbbol.partners.replication.exception.NotFoundReplicationAgentException;

import java.util.UUID;

@Loggable
public class ReplicationEntityHandlerImpl implements ReplicationEntityHandler {

    private final ReplicationAgentRegistry agentRegistry;

    public ReplicationEntityHandlerImpl(ReplicationAgentRegistry agentRegistry) {
        this.agentRegistry = agentRegistry;
    }

    @Transactional
    @Override
    public void handle(ReplicationEntity entity, UUID sessionId) {
        getAgent(entity.getEntityType())
            .replicate(entity, sessionId);
    }

    private ReplicationAgent getAgent(ReplicationEntityType entityType) {
        return agentRegistry.findAgent(entityType)
            .orElseThrow(() -> new NotFoundReplicationAgentException(entityType));
    }
}
