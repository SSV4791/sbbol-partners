package ru.sberbank.pprb.sbbol.partners.config;

import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.query.criteria.LiteralHandlingMode;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(value = {
    "ru.sberbank.pprb.sbbol.migration.gku.repository",
    "ru.sberbank.pprb.sbbol.partners.repository",
    "ru.sberbank.pprb.sbbol.partners.replication.repository",
})
public class DataSourceConfiguration {

    private static final String[] PACKAGES_TO_SCAN = {
        "ru.sberbank.pprb.sbbol.migration.gku.entity",
        "ru.sberbank.pprb.sbbol.partners.entity",
        "ru.sberbank.pprb.sbbol.partners.replication.entity"
    };

    // MAIN DATASOURCE CONFIGS

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties mainDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.configuration")
    public HikariDataSource mainDataSource() {
        return mainDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    // STANDIN DATASOURCE CONFIGS

    @Bean
    @ConfigurationProperties("standin.datasource")
    public DataSourceProperties standInDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("standin.datasource.configuration")
    public HikariDataSource standInDataSource() {
        return standInDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    // COMMON BEANS

    @Bean
    @ConfigurationProperties("spring.jpa.properties")
    public Properties hibernateJpaProperties() {
        return new Properties();
    }

    @Bean
    public LocalSessionFactoryBean entityManagerFactory() {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(mainDataSource());
        sessionFactoryBean.setPackagesToScan(PACKAGES_TO_SCAN);
        sessionFactoryBean.setHibernateProperties(hibernateJpaProperties());
        sessionFactoryBean.getHibernateProperties().put(
            AvailableSettings.CRITERIA_LITERAL_HANDLING_MODE,
            LiteralHandlingMode.BIND.toString()
        );
        return sessionFactoryBean;
    }

    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setDataSource(mainDataSource());
        transactionManager.setSessionFactory(sessionFactory);
        return transactionManager;
    }

    /**
     * HealthCheck для датасорсов.
     * <p>
     * По умолчанию Spring включает в HC все объявленные датасорсы. В таком случае при недоступности второй БД
     * приложение целиком становится недоступно.
     * <p>
     * В данном случае HC переопределен. Определяется доступность только текущей БД, с которой работает приложение.
     *
     * @param mainDataSource     источник данных
     * @param sessionFactory     фабрика соединений
     * @param transactionManager менеджер транзакций
     * @return HealthCheck для проверки доступности БД
     */
    @Bean
    public DataSourceHealthIndicator dataSourceHealthIndicator(DataSource mainDataSource, SessionFactory sessionFactory,
                                                               PlatformTransactionManager transactionManager) {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setReadOnly(true);
        return new DataSourceHealthIndicator(mainDataSource) {
            @Override
            protected void doHealthCheck(Health.Builder builder) {
                template.executeWithoutResult(consumer -> {
                    boolean valid = sessionFactory.getCurrentSession().doReturningWork(c -> c.isValid(0));
                    builder.withDetail("validationQuery", "isValid()");
                    builder.status((valid) ? Status.UP : Status.DOWN);
                });
            }
        };
    }
}
