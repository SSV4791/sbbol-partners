package ru.sberbank.pprb.sbbol.partners.replication.handler.impl;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.replication.handler.ReplicationQueueCleaner;
import ru.sberbank.pprb.sbbol.partners.replication.repository.ReplicationRepository;

import java.time.OffsetDateTime;

@Loggable
public class ReplicationQueueCleanerImpl implements ReplicationQueueCleaner {

    private final ReplicationRepository replicationRepository;

    public ReplicationQueueCleanerImpl(ReplicationRepository replicationRepository) {
        this.replicationRepository = replicationRepository;
    }

    @Transactional
    @Override
    public void clear(OffsetDateTime startExpiredDate) {
        replicationRepository.deleteExpiredMessages(startExpiredDate);
    }
}
