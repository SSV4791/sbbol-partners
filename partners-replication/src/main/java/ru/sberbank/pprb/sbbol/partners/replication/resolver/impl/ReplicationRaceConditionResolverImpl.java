package ru.sberbank.pprb.sbbol.partners.replication.resolver.impl;

import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.replication.config.ReplicationProperties;
import ru.sberbank.pprb.sbbol.partners.replication.entity.ReplicationEntity;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityStatus;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType;
import ru.sberbank.pprb.sbbol.partners.replication.repository.ReplicationRepository;
import ru.sberbank.pprb.sbbol.partners.replication.repository.model.ReplicationFilter;
import ru.sberbank.pprb.sbbol.partners.replication.resolver.ReplicationRaceConditionResolver;

import java.util.UUID;

@Loggable
public class ReplicationRaceConditionResolverImpl implements ReplicationRaceConditionResolver {

    private final ReplicationProperties properties;

    private final ReplicationRepository repository;

    public ReplicationRaceConditionResolverImpl(
        ReplicationProperties properties,
        ReplicationRepository repository
    ) {
        this.properties = properties;
        this.repository = repository;
    }

    @Override
    public void resolve(ReplicationEntityType entityType, UUID entityId, String digitalId) {
        var maxRetry = properties.getRetry();
        handleQueue(0, maxRetry, ReplicationEntityStatus.INIT, entityType, entityId, digitalId);
        handleQueue(1, maxRetry, ReplicationEntityStatus.ERROR, entityType, entityId, digitalId);
    }

    @Override
    public void resolve(ReplicationEntity entity) {
        var maxRetry = properties.getRetry();
        var beginningPartition = entity.getRetry();
        handleQueue(beginningPartition, maxRetry, ReplicationEntityStatus.ERROR, entity.getEntityType(), entity.getEntityId(), entity.getDigitalId());
    }

    private void handleQueue(
        int beginningPartition,
        int stoppingPartition,
        ReplicationEntityStatus queueName,
        ReplicationEntityType entityType,
        UUID entityId,
        String digitalId
        ) {
        for (int retry = beginningPartition; retry < stoppingPartition; retry++) {
            var filter = new ReplicationFilter()
                .digitalId(digitalId)
                .entityId(entityId)
                .entityType(entityType)
                .entityStatus(queueName)
                .partition(retry);
            var entities = repository.findByFilter(filter);
            entities.forEach(this::moveEntityToCanceledQueue);
        }
    }

    private void moveEntityToCanceledQueue(ReplicationEntity entity) {
        entity.setEntityStatus(ReplicationEntityStatus.CANCEL);
        repository.save(entity);
    }
}
