package ru.sberbank.pprb.sbbol.partners.replication.mapper.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.MDC;
import ru.sberbank.pprb.sbbol.partners.replication.entity.ReplicationEntity;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType;
import ru.sberbank.pprb.sbbol.partners.replication.exception.JsonProcessingReplicationEntityException;
import ru.sberbank.pprb.sbbol.partners.replication.mapper.ReplicationEntityMapper;

import java.util.UUID;

import static java.util.Objects.isNull;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityStatus.INIT;

public abstract class AbstractReplicationEntityMapper<T> implements ReplicationEntityMapper<T> {

    protected final ObjectMapper objectMapper;

    protected AbstractReplicationEntityMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ReplicationEntity map(
        String digitalId,
        String digitalUserId,
        UUID entityId,
        ReplicationEntityType entityType,
        T entity
    ) {
        try {
            return new ReplicationEntity()
                .digitalId(digitalId)
                .digitalUserId(digitalUserId)
                .entityId(entityId)
                .entityType(entityType)
                .entityStatus(INIT)
                .retry(0)
                .entityData(serialize(entity))
                .requestId(getRequestId());
        } catch (JsonProcessingException e) {
            throw new JsonProcessingReplicationEntityException(e);
        }
    }

    protected abstract String serialize(T entity) throws JsonProcessingException;

    private String getRequestId() {
        var requestId = MDC.get("requestUid");
        return isNull(requestId) ? UUID.randomUUID().toString() : requestId;
    }
}
