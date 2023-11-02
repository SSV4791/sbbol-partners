package ru.sberbank.pprb.sbbol.partners.scheduler;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.replication.config.ReplicationProperties;
import ru.sberbank.pprb.sbbol.partners.replication.exception.NotFoundReplicationJobException;
import ru.sberbank.pprb.sbbol.partners.replication.job.ReplicationJobRegistry;
import ru.sberbank.pprb.sbbol.partners.replication.job.ReplicationJobType;

import static ru.sberbank.pprb.sbbol.partners.replication.job.ReplicationJobType.REPLICATION;

@Loggable
public class ReplicationScheduler {

    private final ReplicationProperties replicationProperties;

    private final ReplicationJobRegistry jobRegistry;

    public ReplicationScheduler(
        ReplicationProperties replicationProperties,
        ReplicationJobRegistry jobRegistry
    ) {
        this.replicationProperties = replicationProperties;
        this.jobRegistry = jobRegistry;
    }

    @SchedulerLock(name = "sbbolReplication")
    @Scheduled(cron = "${replication.sbbol.cron}")
    void replicate() {
        if (Boolean.TRUE.equals(replicationProperties.isEnable())) {
            run(REPLICATION);
        }
    }

    void run(ReplicationJobType jobType) {
        var job = jobRegistry
            .findJob(jobType)
            .orElseThrow(() -> new NotFoundReplicationJobException(jobType));
        job.run();
    }
}
