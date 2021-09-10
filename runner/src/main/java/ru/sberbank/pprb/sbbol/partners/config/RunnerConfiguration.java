package ru.sberbank.pprb.sbbol.partners.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sberbank.pprb.sbbol.partners.config.endpoint.DataSpaceCoreIndicator;

@Configuration
public class RunnerConfiguration {

    @Bean
    public DataSpaceCoreIndicator pingEndpoint(@Value("${core.url}") String dataSpaceUrl) {
        return new DataSpaceCoreIndicator(dataSpaceUrl);
    }
}
