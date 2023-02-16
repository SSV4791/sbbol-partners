package ru.sberbank.pprb.sbbol.partners.replication.job.impl;

import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.replication.config.ReplicationProperties;
import ru.sberbank.pprb.sbbol.partners.replication.handler.ReplicationQueueHandler;
import ru.sberbank.pprb.sbbol.partners.replication.job.ReplicationJob;

import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityStatus.ERROR;
import static ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityStatus.INIT;

@Loggable
public class ReplicationJobImpl implements ReplicationJob {

    private final ReplicationProperties properties;
    private final ReplicationQueueHandler queueHandler;

    public ReplicationJobImpl(ReplicationProperties properties, ReplicationQueueHandler queueHandler) {
        this.properties = properties;
        this.queueHandler = queueHandler;
    }

    @Override
    public void execute() {
        handleErrorQueues();
        handleMainQueue();
    }

    private void handleErrorQueues() {
        var partitionNumber =  properties.getRetry();
        while (partitionNumber > 0) {
            queueHandler.handle(ERROR, partitionNumber);
            partitionNumber--;
        }
    }

    private void handleMainQueue() {
        queueHandler.handle(INIT, 0);
    }
}
