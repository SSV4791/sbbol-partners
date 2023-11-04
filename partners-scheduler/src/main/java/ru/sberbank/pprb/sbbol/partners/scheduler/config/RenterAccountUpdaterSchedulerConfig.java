package ru.sberbank.pprb.sbbol.partners.scheduler.config;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.sberbank.pprb.sbbol.partners.job.RenterAccountUpdaterJob;
import ru.sberbank.pprb.sbbol.partners.scheduler.RenterAccountUpdaterScheduler;
import ru.sberbank.pprb.sbbol.partners.service.partner.RenterAccountUpdaterService;

import javax.sql.DataSource;

@ConditionalOnProperty(prefix = "scheduler.account-updater", name = "enable")
@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "10m")
public class RenterAccountUpdaterSchedulerConfig {

    @ConditionalOnMissingBean
    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(
            JdbcTemplateLockProvider.Configuration.builder()
                .withJdbcTemplate(new JdbcTemplate(dataSource))
                .usingDbTime()
                .build()
        );
    }

    @Bean
    public RenterAccountUpdaterScheduler accountUpdaterScheduler(
        RenterAccountUpdaterProperties renterAccountUpdaterProperties,
        RenterAccountUpdaterJob renterAccountUpdaterJob
    ) {
        return new RenterAccountUpdaterScheduler(renterAccountUpdaterProperties, renterAccountUpdaterJob);
    }

    @Bean
    public RenterAccountUpdaterJob renterAccountUpdaterJob(RenterAccountUpdaterService renterAccountUpdaterService) {
        return new RenterAccountUpdaterJob(renterAccountUpdaterService);
    }
}
