package ru.sberbank.pprb.sbbol.partners.replication.handler;

import java.time.OffsetDateTime;

public interface ReplicationQueueCleaner {
    void clear(OffsetDateTime startExpiredDate);
}
