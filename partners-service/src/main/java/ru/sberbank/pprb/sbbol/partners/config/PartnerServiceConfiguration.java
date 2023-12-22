package ru.sberbank.pprb.sbbol.partners.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import ru.sberbank.pprb.sbbol.partners.fraud.FraudAdapter;
import ru.sberbank.pprb.sbbol.partners.fraud.config.FraudProperties;
import ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.mapper.counterparty.CounterpartyMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.fraud.DeletedPartnerFraudMetaDataMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.fraud.SignedAccountFraudMetaDataMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountSingMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AddressMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.BudgetMaskMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.ContactMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.DocumentMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.DocumentTypeMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.EmailMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.IdsHistoryMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PhoneMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.renter.RenterMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.renter.RenterPartnerMapper;
import ru.sberbank.pprb.sbbol.partners.replication.config.ReplicationProperties;
import ru.sberbank.pprb.sbbol.partners.replication.mapper.ReplicationEntityMapperRegistry;
import ru.sberbank.pprb.sbbol.partners.replication.repository.ReplicationRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountSignRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AddressRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ContactRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentDictionaryRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.EmailRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.GuidsHistoryRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PhoneRepository;
import ru.sberbank.pprb.sbbol.partners.repository.renter.FlatRenterRepository;
import ru.sberbank.pprb.sbbol.partners.repository.renter.RenterRepository;
import ru.sberbank.pprb.sbbol.partners.service.fraud.FraudService;
import ru.sberbank.pprb.sbbol.partners.service.fraud.FraudServiceManager;
import ru.sberbank.pprb.sbbol.partners.service.fraud.impl.DeletedPartnerFraudServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.fraud.impl.FraudServiceManagerImpl;
import ru.sberbank.pprb.sbbol.partners.service.fraud.impl.SignedAccountFraudServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.ids.history.IdsHistoryService;
import ru.sberbank.pprb.sbbol.partners.service.ids.history.impl.IdsHistoryServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.migration.RenterMigrationService;
import ru.sberbank.pprb.sbbol.partners.service.migration.RenterMigrationServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.partner.AccountService;
import ru.sberbank.pprb.sbbol.partners.service.partner.AccountServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.partner.AccountSignService;
import ru.sberbank.pprb.sbbol.partners.service.partner.AccountSignServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.partner.AddressService;
import ru.sberbank.pprb.sbbol.partners.service.partner.BudgetMaskService;
import ru.sberbank.pprb.sbbol.partners.service.partner.BudgetMaskServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.partner.ContactAddressServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.partner.ContactDocumentServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.partner.ContactEmailServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.partner.ContactPhoneServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.partner.ContactService;
import ru.sberbank.pprb.sbbol.partners.service.partner.ContactServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.partner.DocumentService;
import ru.sberbank.pprb.sbbol.partners.service.partner.DocumentTypeService;
import ru.sberbank.pprb.sbbol.partners.service.partner.DocumentTypeServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.partner.EmailService;
import ru.sberbank.pprb.sbbol.partners.service.partner.FraudMonitoringService;
import ru.sberbank.pprb.sbbol.partners.service.partner.FraudMonitoringServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerAddressServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerDocumentServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerEmailServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerPhoneServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerService;
import ru.sberbank.pprb.sbbol.partners.service.partner.PhoneService;
import ru.sberbank.pprb.sbbol.partners.service.partner.RenterAccountUpdaterService;
import ru.sberbank.pprb.sbbol.partners.service.partner.RenterAccountUpdaterServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.renter.PartnerServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.renter.RenterService;
import ru.sberbank.pprb.sbbol.partners.service.renter.RenterServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.renter.ValidationService;
import ru.sberbank.pprb.sbbol.partners.service.replication.ReplicationService;
import ru.sberbank.pprb.sbbol.partners.service.replication.impl.ReplicationServiceImpl;
import ru.sberbank.pprb.sbbol.partners.storage.BudgetMaskCacheableStorage;

import java.util.List;

@Configuration
@EnableAspectJAutoProxy
public class PartnerServiceConfiguration {

    @Bean
    FraudService deletedPartnerFraudService(
        FraudProperties fraudProperties,
        FraudAdapter fraudAdapter,
        DeletedPartnerFraudMetaDataMapper fraudMapper
    ) {
        return new DeletedPartnerFraudServiceImpl(fraudProperties, fraudAdapter, fraudMapper);
    }

    @Bean
    FraudService signedAccountFraudService(
        FraudProperties fraudProperties,
        FraudAdapter fraudAdapter,
        SignedAccountFraudMetaDataMapper fraudMapper,
        PartnerRepository partnerRepository
    ) {
        return new SignedAccountFraudServiceImpl(
            fraudProperties,
            fraudAdapter,
            fraudMapper,
            partnerRepository);
    }

    @Bean
    FraudServiceManager fraudServiceManager(List<FraudService> services) {
        return new FraudServiceManagerImpl(services);
    }

    @Bean
    FraudMonitoringService fraudMonitoringService(
        AccountRepository accountRepository,
        PartnerRepository partnerRepository,
        FraudServiceManager fraudServiceManager
    ) {
        return new FraudMonitoringServiceImpl(accountRepository, partnerRepository, fraudServiceManager);
    }

    @Bean
    AccountService accountService(
        AccountRepository accountRepository,
        AccountMapper accountMapper,
        @Lazy AccountSignService accountSignService,
        PartnerService partnerService,
        ReplicationService replicationService,
        IdsHistoryService idsHistoryService
    ) {
        return new AccountServiceImpl(
            accountRepository,
            accountMapper,
            accountSignService,
            partnerService,
            replicationService,
            idsHistoryService
        );
    }

    @Bean
    AccountSignService accountSignService(
        AccountSignRepository accountSignRepository,
        AccountSingMapper accountSingMapper,
        AccountService accountService,
        ReplicationService replicationService
    ) {
        return new AccountSignServiceImpl(
            accountSignRepository,
            accountSingMapper,
            accountService,
            replicationService
        );
    }

    @Bean
    BudgetMaskService budgetMaskService(
        BudgetMaskCacheableStorage budgetMaskCacheableStorage,
        BudgetMaskMapper budgetMaskMapper
    ) {
        return new BudgetMaskServiceImpl(budgetMaskCacheableStorage, budgetMaskMapper);
    }

    @Bean
    AddressService contactAddressService(
        ContactRepository contactRepository,
        AddressRepository addressRepository,
        AddressMapper addressMapper
    ) {
        return new ContactAddressServiceImpl(contactRepository, addressRepository, addressMapper);
    }

    @Bean
    DocumentService contactDocumentService(
        ContactRepository contactRepository,
        DocumentRepository documentRepository,
        DocumentDictionaryRepository documentDictionaryRepository,
        DocumentMapper documentMapper
    ) {
        return new ContactDocumentServiceImpl(
            contactRepository,
            documentRepository,
            documentDictionaryRepository,
            documentMapper
        );
    }

    @Bean
    ContactService contactService(
        ContactRepository contactRepository,
        ContactMapper contactMapper,
        PartnerService partnerService,
        AddressService contactAddressService,
        DocumentService contactDocumentService
    ) {
        return new ContactServiceImpl(
            contactRepository,
            contactMapper,
            partnerService,
            contactAddressService,
            contactDocumentService
        );
    }

    @Bean
    DocumentTypeService documentTypeService(
        DocumentDictionaryRepository dictionaryRepository,
        DocumentTypeMapper documentTypeMapper
    ) {
        return new DocumentTypeServiceImpl(dictionaryRepository, documentTypeMapper);
    }

    @Bean
    AddressService partnerAddressService(
        PartnerService partnerService,
        AddressRepository addressRepository,
        AddressMapper addressMapper
    ) {
        return new PartnerAddressServiceImpl(partnerService, addressRepository, addressMapper);
    }

    @Bean
    DocumentService partnerDocumentService(
        PartnerService partnerService,
        DocumentRepository documentRepository,
        DocumentDictionaryRepository documentDictionaryRepository,
        DocumentMapper documentMapper
    ) {
        return new PartnerDocumentServiceImpl(
            partnerService,
            documentRepository,
            documentDictionaryRepository,
            documentMapper
        );
    }

    @Bean
    PhoneService partnerPhoneService(
        PartnerService partnerService,
        PhoneRepository phoneRepository,
        PhoneMapper phoneMapper
    ) {
        return new PartnerPhoneServiceImpl(partnerService, phoneRepository, phoneMapper);
    }

    @Bean
    PhoneService contactPhoneService(
        ContactRepository contactRepository,
        PhoneRepository phoneRepository,
        PhoneMapper phoneMapper
    ) {
        return new ContactPhoneServiceImpl(contactRepository, phoneRepository, phoneMapper);
    }

    @Bean
    EmailService partnerEmailService(
        PartnerService partnerService,
        EmailRepository emailRepository,
        EmailMapper emailMapper
    ) {
        return new PartnerEmailServiceImpl(partnerService, emailRepository, emailMapper);
    }

    @Bean
    EmailService contactEmailService(
        ContactRepository contactRepository,
        EmailRepository emailRepository,
        EmailMapper emailMapper) {
        return new ContactEmailServiceImpl(contactRepository, emailRepository, emailMapper);
    }

    @SuppressWarnings("java:S107")
    @Bean
    PartnerService partnerService(
        PartnerRepository partnerRepository,
        PartnerMapper partnerMapper,
        @Lazy ReplicationService replicationService,
        @Lazy AddressService partnerAddressService,
        @Lazy DocumentService partnerDocumentService,
        @Lazy ContactService contactService,
        @Lazy AccountService accountService
    ) {
        return new ru.sberbank.pprb.sbbol.partners.service.partner.PartnerServiceImpl(
            partnerRepository,
            partnerMapper,
            replicationService,
            partnerAddressService,
            partnerDocumentService,
            contactService,
            accountService
        );
    }

    @SuppressWarnings("java:S5738")
    @Bean
    RenterService renterService(RenterRepository repository,
                                ValidationService validationService,
                                RenterMapper renterMapper) {
        return new RenterServiceImpl(repository, validationService, renterMapper);
    }

    @SuppressWarnings("java:S5738")
    @Bean
    @Primary
    RenterService renterService(
        PartnerRepository partnerRepository,
        AccountRepository accountRepository,
        AddressRepository addressRepository,
        DocumentRepository documentRepository,
        DocumentDictionaryRepository dictionaryRepository,
        FlatRenterRepository flatRenterRepository,
        ValidationService validationService,
        RenterPartnerMapper renterPartnerMapper
    ) {
        return new PartnerServiceImpl(
            partnerRepository,
            accountRepository,
            addressRepository,
            documentRepository,
            dictionaryRepository,
            flatRenterRepository,
            validationService,
            renterPartnerMapper
        );
    }

    @Bean
    RenterMigrationService renterMigrationService() {
        return new RenterMigrationServiceImpl();
    }

    @Bean
    ReplicationService replicationService(
        PartnerRepository partnerRepository,
        AccountRepository accountRepository,
        AccountSignRepository accountSignRepository,
        AccountSingMapper accountSingMapper,
        CounterpartyMapper counterpartyMapper,
        ReplicationProperties replicationProperties,
        LegacySbbolAdapter legacySbbolAdapter,
        ReplicationEntityMapperRegistry mapperRegistry,
        ReplicationRepository replicationRepository
    ) {
        return new ReplicationServiceImpl(
            partnerRepository,
            accountRepository,
            accountSignRepository,
            accountSingMapper,
            counterpartyMapper,
            replicationProperties,
            legacySbbolAdapter,
            mapperRegistry,
            replicationRepository
        );
    }

    @Bean
    IdsHistoryService idsHistoryService(GuidsHistoryRepository idsHistoryRepository, IdsHistoryMapper idsHistoryMapper) {
        return new IdsHistoryServiceImpl(idsHistoryRepository, idsHistoryMapper);
    }

    @Bean
    RenterAccountUpdaterService renterAccountUpdaterService(
        PartnerRepository partnerRepository,
        AccountRepository accountRepository
    ) {
        return new RenterAccountUpdaterServiceImpl(
            partnerRepository,
            accountRepository
        );
    }
}
