package ru.sberbank.pprb.sbbol.partners.replication.mapper.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.legacy.model.Counterparty;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType;

@Loggable
public class UpdatingCounterpartyReplicationMapper extends AbstractReplicationEntityMapper<Counterparty> {

    public UpdatingCounterpartyReplicationMapper(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public ReplicationEntityType getReplicationEntityType() {
        return ReplicationEntityType.UPDATING_COUNTERPARTY;
    }

    @Override
    protected String serialize(Counterparty counterparty) throws JsonProcessingException {
        return objectMapper.writeValueAsString(counterparty);
    }
}
