package ru.sberbank.pprb.sbbol.partners.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.sberbank.pprb.sbbol.partners.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountSingMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountSingMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AddressMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AddressMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.BudgetMaskMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.BudgetMaskMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.ContactEmailMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.ContactEmailMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.ContactMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.ContactMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.ContactPhoneMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.ContactPhoneMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.DocumentMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.DocumentMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.DocumentTypeMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.DocumentTypeMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerEmailMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerEmailMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerPhoneMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerPhoneMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.renter.RenterMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.renter.RenterPartnerMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.renter.RenterPartnerMapperImpl;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AddressRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.BudgetMaskDictionaryRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ContactRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentDictionaryRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.MergeHistoryRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;
import ru.sberbank.pprb.sbbol.partners.repository.renter.FlatRenterRepository;
import ru.sberbank.pprb.sbbol.partners.repository.renter.RenterRepository;
import ru.sberbank.pprb.sbbol.partners.service.partner.AccountService;
import ru.sberbank.pprb.sbbol.partners.service.partner.AccountServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.partner.AccountSignService;
import ru.sberbank.pprb.sbbol.partners.service.partner.AccountSignServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.partner.AddressService;
import ru.sberbank.pprb.sbbol.partners.service.partner.BudgetMaskService;
import ru.sberbank.pprb.sbbol.partners.service.partner.BudgetMaskServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.partner.ContactAddressServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.partner.ContactDocumentServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.partner.ContactService;
import ru.sberbank.pprb.sbbol.partners.service.partner.ContactServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.partner.DocumentService;
import ru.sberbank.pprb.sbbol.partners.service.partner.DocumentTypeService;
import ru.sberbank.pprb.sbbol.partners.service.partner.DocumentTypeServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerAddressServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerDocumentServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerService;
import ru.sberbank.pprb.sbbol.partners.service.renter.PartnerServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.renter.RenterService;
import ru.sberbank.pprb.sbbol.partners.service.renter.RenterServiceImpl;
import ru.sberbank.pprb.sbbol.partners.service.renter.ValidationService;

@Configuration
public class PartnerServiceConfiguration {

    @Bean
    AccountMapper accountMapper() {
        return new AccountMapperImpl();
    }

    @Bean
    AccountSingMapper accountSingMapper() {
        return new AccountSingMapperImpl();
    }

    @Bean
    AddressMapper addressMapper() {
        return new AddressMapperImpl();
    }

    @Bean
    ContactEmailMapper contactEmailMapper() {
        return new ContactEmailMapperImpl();
    }

    @Bean
    ContactPhoneMapper contactPhoneMapper() {
        return new ContactPhoneMapperImpl();
    }

    @Bean
    ContactMapper contactMapper() {
        return new ContactMapperImpl(contactEmailMapper(), contactPhoneMapper());
    }

    @Bean
    DocumentMapper documentMapper() {
        return new DocumentMapperImpl(documentTypeMapper());
    }

    @Bean
    DocumentTypeMapper documentTypeMapper() {
        return new DocumentTypeMapperImpl();
    }

    @Bean
    PartnerEmailMapper partnerEmailMapper() {
        return new PartnerEmailMapperImpl();
    }

    @Bean
    PartnerPhoneMapper partnerPhoneMapper() {
        return new PartnerPhoneMapperImpl();
    }

    @Bean
    PartnerMapper partnerMapper() {
        return new PartnerMapperImpl(partnerEmailMapper(), partnerPhoneMapper());
    }

    @Bean
    RenterPartnerMapper renterPartnerMapper() {
        return new RenterPartnerMapperImpl();
    }

    @Bean
    BudgetMaskMapper budgetMaskMapper() {
        return new BudgetMaskMapperImpl();
    }

    @Bean
    AccountService accountService(
        PartnerRepository partnerRepository,
        AccountRepository accountRepository,
        LegacySbbolAdapter legacySbbolAdapter,
        BudgetMaskService budgetMaskService
    ) {
        return new AccountServiceImpl(partnerRepository, accountRepository, legacySbbolAdapter, budgetMaskService, accountMapper());
    }

    @Bean
    AccountSignService accountSignService(AccountRepository accountRepository) {
        return new AccountSignServiceImpl(accountRepository, accountSingMapper());
    }

    @Bean
    BudgetMaskService budgetMaskService(BudgetMaskDictionaryRepository budgetMaskDictionaryRepository) {
        return new BudgetMaskServiceImpl(budgetMaskDictionaryRepository, budgetMaskMapper());
    }

    @Bean
    AddressService contactAddressService(
        ContactRepository contactRepository,
        AddressRepository addressRepository
    ) {
        return new ContactAddressServiceImpl(contactRepository, addressRepository, addressMapper());
    }

    @Bean
    DocumentService contactDocumentService(ContactRepository contactRepository, DocumentRepository documentRepository) {
        return new ContactDocumentServiceImpl(contactRepository, documentRepository, documentMapper());
    }

    @Bean
    ContactService contactService(
        PartnerRepository partnerRepository,
        ContactRepository contactRepository
    ) {
        return new ContactServiceImpl(partnerRepository, contactRepository, contactMapper());
    }

    @Bean
    DocumentTypeService documentTypeService(DocumentDictionaryRepository dictionaryRepository) {
        return new DocumentTypeServiceImpl(dictionaryRepository, documentTypeMapper());
    }

    @Bean
    AddressService partnerAddressService(
        PartnerRepository partnerRepository,
        AddressRepository addressRepository
    ) {
        return new PartnerAddressServiceImpl(partnerRepository, addressRepository, addressMapper());
    }

    @Bean
    DocumentService partnerDocumentService(PartnerRepository partnerRepository, DocumentRepository documentRepository) {
        return new PartnerDocumentServiceImpl(partnerRepository, documentRepository, documentMapper());
    }

    @Bean
    PartnerService partnerService(
        PartnerRepository partnerRepository,
        MergeHistoryRepository mergeHistoryRepository,
        LegacySbbolAdapter legacySbbolAdapter
    ) {
        return new ru.sberbank.pprb.sbbol.partners.service.partner.PartnerServiceImpl(partnerRepository, mergeHistoryRepository, legacySbbolAdapter, partnerMapper());
    }

    @Bean
    RenterService renterService(RenterRepository repository,
                                ValidationService validationService,
                                RenterMapper renterMapper) {
        return new RenterServiceImpl(repository, validationService, renterMapper);
    }

    @Bean
    @Primary
    RenterService renterService(
        PartnerRepository partnerRepository,
        AccountRepository accountRepository,
        AddressRepository addressRepository,
        DocumentRepository documentRepository,
        DocumentDictionaryRepository dictionaryRepository,
        FlatRenterRepository flatRenterRepository,
        ValidationService validationService
    ) {
        return new PartnerServiceImpl(
            partnerRepository,
            accountRepository,
            addressRepository,
            documentRepository,
            dictionaryRepository,
            flatRenterRepository,
            validationService,
            renterPartnerMapper()
        );
    }
}
