package ru.sberbank.pprb.sbbol.migration.config;

import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImplExporter;
import org.springframework.beans.factory.annotation.Value;
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
import ru.sberbank.pprb.sbbol.partners.repository.partner.AddressRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ContactRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class MigrationServiceConfiguration {

    @Bean
    CorrespondentMigrationService correspondentMigrationService(
        PartnerRepository partnerRepository,
        DocumentRepository documentRepository,
        ContactRepository contactRepository,
        AddressRepository addressRepository,
        AccountRepository accountRepository,
        AccountSignRepository accountSignRepository,
        MigrationPartnerMapper migrationPartnerMapper
    ) {
        return new CorrespondentMigrationServiceImpl(
            migrationPartnerMapper,
            partnerRepository,
            documentRepository,
            contactRepository,
            addressRepository,
            accountRepository,
            accountSignRepository
        );
    }

    @Bean
    public ExecutorService gkuExecutorService(
        @Value("${replication.sbbol.gku.executor.threads:1}") int threads
    ) {
        return Executors.newFixedThreadPool(threads);
    }

    @Bean
    GkuMigrationService gkuMigrationService(
        MigrationGkuRepository migrationGkuRepository,
        MigrationGkuMapper migrationGkuMapper,
        ExecutorService gkuExecutorService
    ) {
        return new GkuMigrationServiceImpl(migrationGkuMapper, migrationGkuRepository, gkuExecutorService);
    }

    @Bean
    AutoJsonRpcServiceImplExporter autoJsonRpcServiceImplExporter() {
        AutoJsonRpcServiceImplExporter exporter = new AutoJsonRpcServiceImplExporter();
        exporter.setContentType("application/json-rpc; charset=utf-8");
        return exporter;
    }
}
