package ru.sberbank.pprb.sbbol.partners.config;

import com.sbt.pprb.integration.hibernate.adapter.HibernateAdapter;
import com.sbt.pprb.integration.hibernate.adapter.HibernatePluginRegistrySpi;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;

/**
 * Добавление зачистки плагинов Hibernate перед инициализацией контекста. Необходим для возможности запуска тестов,
 * помеченных аннотацией {@code @DirtiesContext}, так как между запусками копятся контексты в плагине
 * {@link com.sbt.pprb.integration.hibernate.standin.plugin.StandinPluginImpl}
 */
class HibernatePluginCleanerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {
        ((HibernatePluginRegistrySpi) HibernateAdapter.getInstance().getPluginRegistry()).clean();
    }
}
