package ru.sberbank.pprb.sbbol.partners.replication.job;

import java.util.Optional;

public interface ReplicationJobRegistry {

    Optional<ReplicationJob> findJob(ReplicationJobType jobType);
}
