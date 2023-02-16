package ru.sberbank.pprb.sbbol.partners.replication.mapper.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartySignData;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType;

import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.CREATING_SIGN;

@Loggable
public class CreatingSignReplicationMapper extends AbstractReplicationEntityMapper<CounterpartySignData> {

    public CreatingSignReplicationMapper(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public ReplicationEntityType getReplicationEntityType() {
        return CREATING_SIGN;
    }

    @Override
    protected String serialize(CounterpartySignData counterpartySignData) throws JsonProcessingException {
        return objectMapper.writeValueAsString(counterpartySignData);
    }
}
