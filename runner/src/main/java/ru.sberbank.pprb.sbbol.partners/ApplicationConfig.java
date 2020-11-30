package ru.sberbank.pprb.sbbol.partners;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sbp.sbt.sdk.DataspaceCorePacketClient;
import sbp.sbt.sdk.search.DataspaceCoreSearchClient;

@Configuration
public class ApplicationConfig {

    @Bean
    public DataspaceCoreSearchClient dataspaceCoreSearchClient(@Value("${core.url}") String coreUrl) {
        return new DataspaceCoreSearchClient(coreUrl);
    }

    @Bean
    public DataspaceCorePacketClient dataspaceCorePacketClient(@Value("${core.url}") String coreUrl) {
        return new DataspaceCorePacketClient(coreUrl);
    }
}
