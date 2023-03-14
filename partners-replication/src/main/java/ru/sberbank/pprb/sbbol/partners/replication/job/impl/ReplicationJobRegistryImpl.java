package ru.sberbank.pprb.sbbol.partners.replication.job.impl;

import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.replication.job.ReplicationJob;
import ru.sberbank.pprb.sbbol.partners.replication.job.ReplicationJobRegistry;
import ru.sberbank.pprb.sbbol.partners.replication.job.ReplicationJobType;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReplicationJobRegistryImpl implements ReplicationJobRegistry {

    private final Map<ReplicationJobType, ReplicationJob> registry;

    public ReplicationJobRegistryImpl(List<ReplicationJob> jobs) {
        this.registry = CollectionUtils.isEmpty(jobs)
            ? Collections.emptyMap()
            : jobs.stream().collect(Collectors.toMap(ReplicationJob::getJobType, it -> it));
    }

    @Override
    public Optional<ReplicationJob> findJob(ReplicationJobType jobType) {
        return Optional.ofNullable(registry.get(jobType));
    }
}
