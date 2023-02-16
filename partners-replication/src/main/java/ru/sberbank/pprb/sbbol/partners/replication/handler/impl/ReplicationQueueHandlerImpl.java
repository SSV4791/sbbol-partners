package ru.sberbank.pprb.sbbol.partners.replication.handler.impl;

import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.replication.config.ReplicationProperties;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityStatus;
import ru.sberbank.pprb.sbbol.partners.replication.handler.ReplicationEntityHandler;
import ru.sberbank.pprb.sbbol.partners.replication.handler.ReplicationQueueHandler;
import ru.sberbank.pprb.sbbol.partners.replication.repository.ReplicationRepository;
import ru.sberbank.pprb.sbbol.partners.replication.repository.model.ReplicationFilter;

@Loggable
public class ReplicationQueueHandlerImpl implements ReplicationQueueHandler {

    private final ReplicationProperties properties;

    private final ReplicationRepository repository;

    private final ReplicationEntityHandler entityHandler;

    public ReplicationQueueHandlerImpl(
        ReplicationProperties properties,
        ReplicationRepository repository,
        ReplicationEntityHandler entityHandler
    ) {
        this.properties = properties;
        this.repository = repository;
        this.entityHandler = entityHandler;
    }

    @Override
    public void handle(ReplicationEntityStatus entityStatus, int retry) {
        var batchSize = properties.getBatchSize();
        var filter = new ReplicationFilter()
            .entityStatus(entityStatus)
            .partition(retry)
            .pagination(
                new Pagination()
                    .offset(0)
                    .count(batchSize)
            );
        while (true) {
            var entities = repository.findByFilter(filter);
            if (CollectionUtils.isEmpty(entities)) {
                break;
            }
            for (var entity : entities) {
                entityHandler.handle(entity);
            }
        }
    }
}
