package ru.sberbank.pprb.sbbol.migration.correspondents.config;

import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImplExporter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sberbank.pprb.sbbol.migration.correspondents.mapper.MigrationPartnerMapper;
import ru.sberbank.pprb.sbbol.migration.correspondents.mapper.MigrationPartnerMapperImpl;
import ru.sberbank.pprb.sbbol.migration.correspondents.mapper.MigrationReplicationHistoryMapper;
import ru.sberbank.pprb.sbbol.migration.correspondents.mapper.MigrationReplicationHistoryMapperImpl;

@Configuration
public class CorrespondentMigrationServiceConfiguration {

    @Bean
    MigrationPartnerMapper migrationPartnerMapper() {
        return new MigrationPartnerMapperImpl();
    }

    @Bean
    MigrationReplicationHistoryMapper migrationReplicationHistoryMapper() {
        return new MigrationReplicationHistoryMapperImpl();
    }

    @Bean
    AutoJsonRpcServiceImplExporter autoJsonRpcServiceImplExporter() {
        AutoJsonRpcServiceImplExporter exporter = new AutoJsonRpcServiceImplExporter();
        exporter.setContentType("application/json-rpc; charset=utf-8");
        return exporter;
    }
}
