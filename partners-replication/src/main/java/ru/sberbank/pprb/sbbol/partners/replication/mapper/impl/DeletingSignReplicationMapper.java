package ru.sberbank.pprb.sbbol.partners.replication.mapper.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType;

import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType.DELETING_SIGN;

@Loggable
public class DeletingSignReplicationMapper extends AbstractReplicationEntityMapper<String> {

    public DeletingSignReplicationMapper(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public ReplicationEntityType getReplicationEntityType() {
        return DELETING_SIGN;
    }

    @Override
    protected String serialize(String accountId) {
        return accountId;
    }
}
