package ru.sberbank.pprb.sbbol.partners.replication.mapper.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType;

import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.DELETING_COUNTERPARTY;

@Loggable
public class DeletingCounterpartyReplicationMapper extends AbstractReplicationEntityMapper<String> {

    public DeletingCounterpartyReplicationMapper(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public ReplicationEntityType getReplicationEntityType() {
        return DELETING_COUNTERPARTY;
    }

    @Override
    protected String serialize(String accountId) {
        return accountId;
    }
}
