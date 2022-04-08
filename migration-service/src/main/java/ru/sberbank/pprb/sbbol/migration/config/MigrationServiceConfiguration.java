package ru.sberbank.pprb.sbbol.migration.config;

import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImplExporter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sberbank.pprb.sbbol.migration.correspondents.mapper.MigrationPartnerMapper;
import ru.sberbank.pprb.sbbol.migration.correspondents.mapper.MigrationPartnerMapperImpl;
import ru.sberbank.pprb.sbbol.migration.correspondents.repository.MigrationPartnerRepository;
import ru.sberbank.pprb.sbbol.migration.correspondents.service.CorrespondentMigrationService;
import ru.sberbank.pprb.sbbol.migration.correspondents.service.CorrespondentMigrationServiceImpl;
import ru.sberbank.pprb.sbbol.migration.gku.mapper.MigrationGkuMapper;
import ru.sberbank.pprb.sbbol.migration.gku.mapper.MigrationGkuMapperImpl;
import ru.sberbank.pprb.sbbol.migration.gku.repository.MigrationGkuRepository;
import ru.sberbank.pprb.sbbol.migration.gku.service.GkuMigrationService;
import ru.sberbank.pprb.sbbol.migration.gku.service.GkuMigrationServiceImpl;

@Configuration
public class MigrationServiceConfiguration {

    @Bean
    MigrationPartnerMapper migrationPartnerMapper() {
        return new MigrationPartnerMapperImpl();
    }

    @Bean
    MigrationGkuMapper migrationGkuMapper() {
        return new MigrationGkuMapperImpl();
    }

    @Bean
    CorrespondentMigrationService correspondentMigrationService(
        MigrationPartnerRepository migrationPartnerRepository
    ) {
        return new CorrespondentMigrationServiceImpl(
            migrationPartnerMapper(),
            migrationPartnerRepository
        );
    }

    @Bean
    GkuMigrationService gkuMigrationService(MigrationGkuRepository migrationGkuRepository) {
        return new GkuMigrationServiceImpl(migrationGkuMapper(), migrationGkuRepository);
    }

    @Bean
    AutoJsonRpcServiceImplExporter autoJsonRpcServiceImplExporter() {
        AutoJsonRpcServiceImplExporter exporter = new AutoJsonRpcServiceImplExporter();
        exporter.setContentType("application/json-rpc; charset=utf-8");
        return exporter;
    }
}
