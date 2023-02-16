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

import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.CREATING_COUNTERPARTY;

@Loggable
public class CreatingCounterpartyReplicationAgent extends AbstractReplicationAgent {

    public CreatingCounterpartyReplicationAgent(
        LegacySbbolAdapter sbbolAdapter,
        ReplicationRepository replicationRepository,
        ObjectMapper objectMapper
    ) {
        super(sbbolAdapter, replicationRepository, objectMapper);
    }

    @Override
    public ReplicationEntityType getReplicationEntityType() {
        return CREATING_COUNTERPARTY;
    }

    @Override
    protected void replicateToSbbol(ReplicationEntity entity) throws JsonProcessingException {
        var counterparty = objectMapper.readValue(entity.getEntityData(), Counterparty.class);
        sbbolAdapter.create(entity.getDigitalId(), counterparty);
    }
}
