package ru.sberbank.pprb.sbbol.partners.scheduler.config;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.sberbank.pprb.sbbol.partners.replication.job.ReplicationJob;
import ru.sberbank.pprb.sbbol.partners.scheduler.ReplicationScheduler;

import javax.sql.DataSource;

@ConditionalOnProperty(prefix = "replication.sbbol", name = "enable")
@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "10m")
public class ReplicationSchedulerConfig {

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
    public ReplicationScheduler replicationScheduler(ReplicationJob replicationJob) {
        return new ReplicationScheduler(replicationJob);
    }
}
