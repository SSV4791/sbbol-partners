package ru.sberbank.pprb.sbbol.partners.replication.job.impl;

import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.replication.config.ReplicationProperties;
import ru.sberbank.pprb.sbbol.partners.replication.handler.ReplicationQueueCleaner;
import ru.sberbank.pprb.sbbol.partners.replication.job.ReplicationJob;
import ru.sberbank.pprb.sbbol.partners.replication.job.ReplicationJobType;

import java.time.OffsetDateTime;

@Loggable
public class ReplicationCleanerJobImpl implements ReplicationJob {

    private final ReplicationProperties properties;

    private final ReplicationQueueCleaner replicationQueueCleaner;

    public ReplicationCleanerJobImpl(
        ReplicationProperties properties,
        ReplicationQueueCleaner replicationQueueCleaner
    ) {
        this.properties = properties;
        this.replicationQueueCleaner = replicationQueueCleaner;
    }

    @Override
    public ReplicationJobType getJobType() {
        return ReplicationJobType.REPLICATION_CLEANER;
    }

    @Override
    public void run() {
        clearReplicationQueue();
    }

    private void clearReplicationQueue() {
        int expiredPeriod = properties.getCleaner().getExpiredPeriod();
        OffsetDateTime startExpiredDate = OffsetDateTime.now().minusDays(expiredPeriod);
        replicationQueueCleaner.clear(startExpiredDate);
    }
}
