package ru.sberbank.pprb.sbbol.partners.replication.agent.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartySignData;
import ru.sberbank.pprb.sbbol.partners.replication.agent.AbstractReplicationAgent;
import ru.sberbank.pprb.sbbol.partners.replication.entity.ReplicationEntity;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType;
import ru.sberbank.pprb.sbbol.partners.replication.repository.ReplicationRepository;

import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.CREATING_SIGN;

@Loggable
public class CreatingSignReplicationAgent extends AbstractReplicationAgent {

    public CreatingSignReplicationAgent(
        LegacySbbolAdapter sbbolAdapter,
        ReplicationRepository replicationRepository,
        ObjectMapper objectMapper
    ) {
        super(sbbolAdapter, replicationRepository, objectMapper);
    }

    @Override
    public ReplicationEntityType getReplicationEntityType() {
        return CREATING_SIGN;
    }

    @Override
    protected void replicateToSbbol(ReplicationEntity entity) throws JsonProcessingException {
        var сounterpartySignData = objectMapper.readValue(entity.getEntityData(), CounterpartySignData.class);
        sbbolAdapter.saveSign(entity.getDigitalUserId(), сounterpartySignData);
    }
}
