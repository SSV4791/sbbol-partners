package ru.sberbank.pprb.sbbol.migration.config;

import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImplExporter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sberbank.pprb.sbbol.migration.correspondents.mapper.MigrationPartnerMapper;
import ru.sberbank.pprb.sbbol.migration.correspondents.service.CorrespondentMigrationService;
import ru.sberbank.pprb.sbbol.migration.correspondents.service.CorrespondentMigrationServiceImpl;
import ru.sberbank.pprb.sbbol.migration.gku.mapper.MigrationGkuMapper;
import ru.sberbank.pprb.sbbol.migration.gku.repository.MigrationGkuRepository;
import ru.sberbank.pprb.sbbol.migration.gku.service.GkuMigrationService;
import ru.sberbank.pprb.sbbol.migration.gku.service.GkuMigrationServiceImpl;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountSignRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;

@Configuration
public class MigrationServiceConfiguration {

    @Bean
    CorrespondentMigrationService correspondentMigrationService(
        PartnerRepository partnerRepository,
        AccountRepository accountRepository,
        AccountSignRepository accountSignRepository,
        MigrationPartnerMapper migrationPartnerMapper
    ) {
        return new CorrespondentMigrationServiceImpl(
            migrationPartnerMapper,
            partnerRepository,
            accountRepository,
            accountSignRepository
        );
    }

    @Bean
    GkuMigrationService gkuMigrationService(
        MigrationGkuRepository migrationGkuRepository,
        MigrationGkuMapper migrationGkuMapper) {
        return new GkuMigrationServiceImpl(migrationGkuMapper, migrationGkuRepository);
    }

    @Bean
    AutoJsonRpcServiceImplExporter autoJsonRpcServiceImplExporter() {
        AutoJsonRpcServiceImplExporter exporter = new AutoJsonRpcServiceImplExporter();
        exporter.setContentType("application/json-rpc; charset=utf-8");
        return exporter;
    }
}
