package ru.sberbank.pprb.sbbol.partners.replication.agent.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.legacy.model.Counterparty;
import ru.sberbank.pprb.sbbol.partners.replication.agent.AbstractReplicationAgent;
import ru.sberbank.pprb.sbbol.partners.replication.entity.ReplicationEntity;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType;
import ru.sberbank.pprb.sbbol.partners.replication.repository.ReplicationRepository;

import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.UPDATING_COUNTERPARTY;

@Loggable
public class UpdatingCounterpartyReplicationAgent extends AbstractReplicationAgent {

    public UpdatingCounterpartyReplicationAgent(
        LegacySbbolAdapter sbbolAdapter,
        ReplicationRepository replicationRepository,
        ObjectMapper objectMapper
    ) {
        super(sbbolAdapter, replicationRepository, objectMapper);
    }

    @Override
    public ReplicationEntityType getReplicationEntityType() {
        return UPDATING_COUNTERPARTY;
    }

    @Override
    protected void replicateToSbbol(ReplicationEntity entity) throws JsonProcessingException {
        var counterparty = objectMapper.readValue(entity.getEntityData(), Counterparty.class);
        sbbolAdapter.update(entity.getDigitalId(), counterparty);
    }
}
