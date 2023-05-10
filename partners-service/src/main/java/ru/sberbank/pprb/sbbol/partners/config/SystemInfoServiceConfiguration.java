package ru.sberbank.pprb.sbbol.partners.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sberbank.pprb.sbbol.partners.service.systeminfo.SystemInfoService;
import ru.sberbank.pprb.sbbol.partners.service.systeminfo.impl.SystemInfoServiceImpl;
import ru.sbrf.journal.standin.StandinResourceHelper;

@Configuration
public class SystemInfoServiceConfiguration {

    @Bean
    SystemInfoService systemInfoService(StandinResourceHelper<String> standinResourceHelper) {
        return new SystemInfoServiceImpl(standinResourceHelper);
    }
}
