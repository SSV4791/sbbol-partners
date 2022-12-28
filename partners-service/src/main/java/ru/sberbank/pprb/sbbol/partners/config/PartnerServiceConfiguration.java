package ru.sberbank.pprb.sbbol.partners.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import ru.sberbank.pprb.sbbol.partners.aspect.legacy.LegacyAsynchReplicationAspect;
import ru.sberbank.pprb.sbbol.partners.aspect.legacy.LegacyCheckAspect;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.LoggerAspect;
import ru.sberbank.pprb.sbbol.partners.aspect.validator.FraudValidatorAspect;
import ru.sberbank.pprb.sbbol.partners.audit.AuditAdapter;
import ru.sberbank.pprb.sbbol.partners.config.props.ReplicationKafkaProducerProperties;
import ru.sberbank.pprb.sbbol.partners.fraud.FraudAdapter;
import ru.sberbank.pprb.sbbol.partners.fraud.config.FraudProperties;
import ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.mapper.counterparty.AsynchReplicationCounterpartyMapper;
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
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PhoneMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.renter.RenterMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.renter.RenterPartnerMapper;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountSignRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AddressRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.BudgetMaskDictionaryRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ContactRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentDictionaryRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.EmailRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.GkuInnDictionaryRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PhoneRepository;
import ru.sberbank.pprb.sbbol.partners.repository.renter.FlatRenterRepository;
import ru.sberbank.pprb.sbbol.partners.repository.renter.RenterRepository;
import ru.sberbank.pprb.sbbol.partners.service.fraud.FraudService;
import ru.sberbank.pprb.sbbol.partners.service.fraud.FraudServiceManager;
import ru.sberbank.pprb.sbbol.partners.service.fraud.impl.DeletedPartnerFraudServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.fraud.impl.FraudServiceManagerImpl;
import ru.sberbank.pprb.sbbol.partners.service.fraud.impl.SignedAccountFraudServiceImpl;
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
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerAddressServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerDocumentServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerEmailServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerPhoneServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerService;
import ru.sberbank.pprb.sbbol.partners.service.partner.PhoneService;
import ru.sberbank.pprb.sbbol.partners.service.renter.PartnerServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.renter.RenterService;
import ru.sberbank.pprb.sbbol.partners.service.renter.RenterServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.renter.ValidationService;
import ru.sberbank.pprb.sbbol.partners.service.replication.AsynchReplicationService;
import ru.sberbank.pprb.sbbol.partners.service.replication.AsynchReplicationServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.replication.ReplicationService;
import ru.sberbank.pprb.sbbol.partners.service.replication.ReplicationServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Validator;
import java.util.List;

@Configuration
@EnableAspectJAutoProxy
public class PartnerServiceConfiguration {

    @Bean
    FraudValidatorAspect fraudValidatorAspect (Validator validator) {
        return new FraudValidatorAspect(validator);
    }

    @Bean
    LoggerAspect loggedAspect() {
        return new LoggerAspect();
    }

    @Bean
    LegacyCheckAspect legacyCheckAspect(
        @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") HttpServletRequest servletRequest,
        LegacySbbolAdapter legacySbbolAdapter
    ) {
        return new LegacyCheckAspect(servletRequest, legacySbbolAdapter);
    }

    @Bean
    LegacyAsynchReplicationAspect legacyAsynchReplicationAspect(AsynchReplicationService asynchReplicationService) {
        return new LegacyAsynchReplicationAspect(asynchReplicationService);
    }

    @Bean
    ReplicationService replicationHistoryService(
        PartnerRepository partnerRepository,
        AccountRepository accountRepository,
        LegacySbbolAdapter legacySbbolAdapter,
        AccountMapper accountMapper,
        AccountSingMapper accountSingMapper,
        CounterpartyMapper counterpartyMapper
    ) {
        return new ReplicationServiceImpl(
            partnerRepository,
            accountRepository,
            legacySbbolAdapter,
            accountMapper,
            accountSingMapper,
            counterpartyMapper
        );
    }

    @Bean
    AsynchReplicationService asynchReplicationService(
        ReplicationKafkaProducerProperties kafkaProperties,
        @Autowired(required = false)  KafkaTemplate<String, String> kafkaTemplate,
        AsynchReplicationCounterpartyMapper asynchReplicationCounterpartyMapper,
        ObjectMapper objectMapper
    ) {
        return new AsynchReplicationServiceImpl(
            kafkaProperties,
            kafkaTemplate,
            asynchReplicationCounterpartyMapper,
            objectMapper);
    }

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
    AccountService accountService(
        AccountRepository accountRepository,
        AccountSignRepository accountSignRepository,
        ReplicationService replicationService,
        BudgetMaskService budgetMaskService,
        AuditAdapter auditAdapter,
        AccountMapper accountMapper
    ) {
        return new AccountServiceImpl(
            accountRepository,
            accountSignRepository,
            replicationService,
            budgetMaskService,
            auditAdapter,
            accountMapper);
    }

    @Bean
    AccountSignService accountSignService(
        AccountRepository accountRepository,
        AccountSignRepository accountSignRepository,
        ReplicationService replicationService,
        FraudServiceManager fraudServiceManager,
        AuditAdapter auditAdapter,
        AccountMapper accountMapper,
        AccountSingMapper accountSingMapper
    ) {
        return new AccountSignServiceImpl(
            accountRepository,
            accountSignRepository,
            replicationService,
            fraudServiceManager,
            auditAdapter,
            accountMapper,
            accountSingMapper
        );
    }

    @Bean
    BudgetMaskService budgetMaskService(
        BudgetMaskDictionaryRepository budgetMaskDictionaryRepository,
        BudgetMaskMapper budgetMaskMapper
    ) {
        return new BudgetMaskServiceImpl(budgetMaskDictionaryRepository, budgetMaskMapper);
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
        EmailRepository emailRepository,
        PhoneRepository phoneRepository,
        AddressRepository addressRepository,
        DocumentRepository documentRepository,
        ContactMapper contactMapper
    ) {
        return new ContactServiceImpl(
            contactRepository,
            emailRepository,
            phoneRepository,
            addressRepository,
            documentRepository,
            contactMapper)
            ;
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
        PartnerRepository partnerRepository,
        AddressRepository addressRepository,
        AddressMapper addressMapper
    ) {
        return new PartnerAddressServiceImpl(partnerRepository, addressRepository, addressMapper);
    }

    @Bean
    DocumentService partnerDocumentService(
        PartnerRepository partnerRepository,
        DocumentRepository documentRepository,
        DocumentDictionaryRepository documentDictionaryRepository,
        DocumentMapper documentMapper
    ) {
        return new PartnerDocumentServiceImpl(
            partnerRepository,
            documentRepository,
            documentDictionaryRepository,
            documentMapper
        );
    }

    @Bean
    PhoneService partnerPhoneService(
        PartnerRepository partnerRepository,
        PhoneRepository phoneRepository,
        PhoneMapper phoneMapper
    ) {
        return new PartnerPhoneServiceImpl(partnerRepository, phoneRepository, phoneMapper);
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
        PartnerRepository partnerRepository,
        EmailRepository emailRepository,
        EmailMapper emailMapper
    ) {
        return new PartnerEmailServiceImpl(partnerRepository, emailRepository, emailMapper);
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
        AccountRepository accountRepository,
        DocumentRepository documentRepository,
        ContactRepository contactRepository,
        AddressRepository addressRepository,
        PartnerRepository partnerRepository,
        GkuInnDictionaryRepository gkuInnDictionaryRepository,
        BudgetMaskService budgetMaskService,
        ReplicationService replicationService,
        FraudServiceManager fraudServiceManager,
        AccountMapper accountMapper,
        DocumentMapper documentMapper,
        AddressMapper addressMapper,
        ContactMapper contactMapper,
        PartnerMapper partnerMapper
    ) {
        return new ru.sberbank.pprb.sbbol.partners.service.partner.PartnerServiceImpl(
            accountRepository,
            documentRepository,
            contactRepository,
            addressRepository,
            partnerRepository,
            gkuInnDictionaryRepository,
            budgetMaskService,
            replicationService,
            fraudServiceManager,
            accountMapper,
            documentMapper,
            addressMapper,
            contactMapper,
            partnerMapper
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
}
