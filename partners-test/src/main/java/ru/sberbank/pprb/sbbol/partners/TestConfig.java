package ru.sberbank.pprb.sbbol.partners;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import sbp.com.sbt.dataspace.core.local.runner.DataSpaceCoreLocalRunner;
import sbp.sbt.sdk.DataspaceCorePacketClient;
import sbp.sbt.sdk.search.DataspaceCoreSearchClient;

@Configuration
@ComponentScan({"ru.sberbank.pprb.sbbol.partners"})
public class TestConfig {

    @Bean
    public DataspaceCoreSearchClient dataspaceCoreSearchClient() {
        String port = DataSpaceCoreLocalRunner.getRunnerProperties().getPort();
        return new DataspaceCoreSearchClient("http://localhost:" + port);
    }

    @Bean
    public DataspaceCorePacketClient dataspaceCorePacketClient() {
        String port = DataSpaceCoreLocalRunner.getRunnerProperties().getPort();
        return new DataspaceCorePacketClient("http://localhost:" + port);
    }
}
