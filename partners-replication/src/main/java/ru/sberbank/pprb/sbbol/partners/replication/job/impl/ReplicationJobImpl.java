package ru.sberbank.pprb.sbbol.partners.replication.job.impl;

import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.replication.config.ReplicationProperties;
import ru.sberbank.pprb.sbbol.partners.replication.handler.ReplicationQueueHandler;
import ru.sberbank.pprb.sbbol.partners.replication.job.ReplicationJob;
import ru.sberbank.pprb.sbbol.partners.replication.job.ReplicationJobType;

import static ru.sberbank.pprb.sbbol.partners.replication.job.ReplicationJobType.REPLICATION;

@Loggable
public class ReplicationJobImpl implements ReplicationJob {

    private final ReplicationProperties properties;
    private final ReplicationQueueHandler queueHandler;

    public ReplicationJobImpl(ReplicationProperties properties, ReplicationQueueHandler queueHandler) {
        this.properties = properties;
        this.queueHandler = queueHandler;
    }

    @Override
    public ReplicationJobType getJobType() {
        return REPLICATION;
    }

    @Override
    public void run() {
        queueHandler.handle(properties.getRetry());
    }
}
