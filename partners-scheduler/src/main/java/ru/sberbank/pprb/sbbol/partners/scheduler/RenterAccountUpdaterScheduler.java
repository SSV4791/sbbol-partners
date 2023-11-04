package ru.sberbank.pprb.sbbol.partners.scheduler;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.job.RenterAccountUpdaterJob;
import ru.sberbank.pprb.sbbol.partners.scheduler.config.RenterAccountUpdaterProperties;

@Loggable
public class RenterAccountUpdaterScheduler {

    private final RenterAccountUpdaterProperties accountUpdaterProperties;

    private final RenterAccountUpdaterJob accountUpdaterJob;

    public RenterAccountUpdaterScheduler(
        RenterAccountUpdaterProperties renterAccountUpdaterProperties,
        RenterAccountUpdaterJob renterAccountUpdaterJob
    ) {
        this.accountUpdaterProperties = renterAccountUpdaterProperties;
        this.accountUpdaterJob = renterAccountUpdaterJob;
    }

    @SchedulerLock(name = "accountUpdater")
    @Scheduled(cron = "${scheduler.account-updater.cron}")
    void run() {
        if (Boolean.TRUE.equals(accountUpdaterProperties.isEnable())) {
            accountUpdaterJob.run(
                accountUpdaterProperties.getSleepTime(),
                accountUpdaterProperties.getBatchSize()
            );
        }
    }
}
