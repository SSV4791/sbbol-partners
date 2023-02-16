package ru.sberbank.pprb.sbbol.partners.scheduler;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.replication.job.ReplicationJob;

@Loggable
public class ReplicationScheduler {

    private final ReplicationJob job;

    public ReplicationScheduler(ReplicationJob job) {
        this.job = job;
    }

    @SchedulerLock(name = "sbbolReplication")
    @Scheduled(cron = "${replication.sbbol.cron}")
    void replicate() {
        job.execute();
    }
}
