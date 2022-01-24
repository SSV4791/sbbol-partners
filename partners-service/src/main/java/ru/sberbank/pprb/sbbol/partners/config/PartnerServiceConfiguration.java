package ru.sberbank.pprb.sbbol.partners.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sberbank.pprb.sbbol.partners.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapperImpl;
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
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AddressRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.BudgetMaskDictionaryRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ContactRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentDictionaryRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.MergeHistoryRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;
import ru.sberbank.pprb.sbbol.partners.service.partner.AccountService;
import ru.sberbank.pprb.sbbol.partners.service.partner.AccountServiceImpl;
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
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerServiceImpl;

@Configuration
public class PartnerServiceConfiguration {

    @Bean
    AccountMapper accountMapper() {
        return new AccountMapperImpl();
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
        return new PartnerServiceImpl(partnerRepository, mergeHistoryRepository, legacySbbolAdapter, partnerMapper());
    }
}
