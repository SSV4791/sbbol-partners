package ru.sberbank.pprb.sbbol.partners.replication.agent.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.replication.agent.AbstractReplicationAgent;
import ru.sberbank.pprb.sbbol.partners.replication.entity.ReplicationEntity;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType;
import ru.sberbank.pprb.sbbol.partners.replication.repository.ReplicationRepository;

import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.DELETING_COUNTERPARTY;

@Loggable
public class DeletingCounterpartyReplicationAgent extends AbstractReplicationAgent {

    public DeletingCounterpartyReplicationAgent(
        LegacySbbolAdapter sbbolAdapter,
        ReplicationRepository replicationRepository,
        ObjectMapper objectMapper
    ) {
        super(sbbolAdapter, replicationRepository, objectMapper);
    }

    @Override
    public ReplicationEntityType getReplicationEntityType() {
        return DELETING_COUNTERPARTY;
    }

    @Override
    protected void replicateToSbbol(ReplicationEntity entity) {
        var accountId = entity.getEntityData();
        sbbolAdapter.delete(entity.getDigitalId(), accountId);
    }
}
