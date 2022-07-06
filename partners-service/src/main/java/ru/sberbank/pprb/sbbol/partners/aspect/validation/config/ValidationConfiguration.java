package ru.sberbank.pprb.sbbol.partners.aspect.validation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.aspect.validation.mapper.ValidationMapper;
import ru.sberbank.pprb.sbbol.partners.aspect.validation.mapper.ValidationMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerMapper;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.AccountPriority;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfo;
import ru.sberbank.pprb.sbbol.partners.model.Address;
import ru.sberbank.pprb.sbbol.partners.model.AddressCreate;
import ru.sberbank.pprb.sbbol.partners.model.AddressesFilter;
import ru.sberbank.pprb.sbbol.partners.model.Contact;
import ru.sberbank.pprb.sbbol.partners.model.ContactCreate;
import ru.sberbank.pprb.sbbol.partners.model.ContactsFilter;
import ru.sberbank.pprb.sbbol.partners.model.DocumentChange;
import ru.sberbank.pprb.sbbol.partners.model.DocumentCreate;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeChange;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsFilter;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.EmailCreate;
import ru.sberbank.pprb.sbbol.partners.model.EmailsFilter;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;
import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.model.PhoneCreate;
import ru.sberbank.pprb.sbbol.partners.model.PhonesFilter;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AddressRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ContactRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentDictionaryRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.EmailRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PhoneRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.common.AccountSignViewRepository;
import ru.sberbank.pprb.sbbol.partners.validation.AccountChangePriorityValidationImpl;
import ru.sberbank.pprb.sbbol.partners.validation.AccountCreateValidatorImpl;
import ru.sberbank.pprb.sbbol.partners.validation.AccountSignValidatorImpl;
import ru.sberbank.pprb.sbbol.partners.validation.AccountUpdateValidatorImpl;
import ru.sberbank.pprb.sbbol.partners.validation.AccountsFilterValidationImpl;
import ru.sberbank.pprb.sbbol.partners.validation.AccountsSignFilterValidationImpl;
import ru.sberbank.pprb.sbbol.partners.validation.AddressCreateValidationImpl;
import ru.sberbank.pprb.sbbol.partners.validation.AddressUpdateValidationImpl;
import ru.sberbank.pprb.sbbol.partners.validation.AddressesFilterValidationImpl;
import ru.sberbank.pprb.sbbol.partners.validation.ContactCreateValidationImpl;
import ru.sberbank.pprb.sbbol.partners.validation.ContactUpdateValidationImpl;
import ru.sberbank.pprb.sbbol.partners.validation.ContactsFilterValidationImpl;
import ru.sberbank.pprb.sbbol.partners.validation.DocumentCreateValidationImpl;
import ru.sberbank.pprb.sbbol.partners.validation.DocumentTypeUpdateValidationImpl;
import ru.sberbank.pprb.sbbol.partners.validation.DocumentUpdateValidationImpl;
import ru.sberbank.pprb.sbbol.partners.validation.DocumentsFilterValidationImpl;
import ru.sberbank.pprb.sbbol.partners.validation.EmailCreateValidationImpl;
import ru.sberbank.pprb.sbbol.partners.validation.EmailUpdateValidationImpl;
import ru.sberbank.pprb.sbbol.partners.validation.EmailsFilterValidationImpl;
import ru.sberbank.pprb.sbbol.partners.validation.PaginationValidationImpl;
import ru.sberbank.pprb.sbbol.partners.validation.PartnerCreateValidatorImpl;
import ru.sberbank.pprb.sbbol.partners.validation.PartnerUpdateValidatorImpl;
import ru.sberbank.pprb.sbbol.partners.validation.PartnersFilterValidationImpl;
import ru.sberbank.pprb.sbbol.partners.validation.PhoneCreateValidationImpl;
import ru.sberbank.pprb.sbbol.partners.validation.PhoneUpdateValidationImpl;
import ru.sberbank.pprb.sbbol.partners.validation.PhonesFilterValidationImpl;

@Configuration
public class ValidationConfiguration {

    @Bean
    Validator<AccountChange> accountUpdateValidator(
        AccountRepository accountRepository,
        AccountMapper accountMapper
    ) {
        return new AccountUpdateValidatorImpl(accountRepository, accountMapper);
    }

    @Bean
    Validator<AccountCreate> accountCreateValidator(
        PartnerRepository partnerRepository
    ) {
        return new AccountCreateValidatorImpl(partnerRepository);
    }

    @Bean
    Validator<AccountPriority> accountPriorityValidator(
        AccountRepository accountRepository
    ) {
        return new AccountChangePriorityValidationImpl(accountRepository);
    }

    @Bean
    Validator<AccountsFilter> accountsFilterValidator(
        Validator<Pagination> paginationValidator
    ) {
        return new AccountsFilterValidationImpl(paginationValidator);
    }

    @Bean
    Validator<AccountsSignInfo> accountsSignInfoValidator(
        AccountSignViewRepository accountRepository
    ) {
        return new AccountSignValidatorImpl(accountRepository);
    }

    @Bean
    Validator<AccountsSignFilter> accountsSignFilterValidator(
        Validator<Pagination> paginationValidator
    ) {
        return new AccountsSignFilterValidationImpl(paginationValidator);
    }

    @Bean
    Validator<Address> addressUpdateValidator(
        AddressRepository addressRepository
    ) {
        return new AddressUpdateValidationImpl(addressRepository);
    }

    @Bean
    Validator<AddressesFilter> addressesFilterValidator(
        Validator<Pagination> paginationValidator
    ) {
        return new AddressesFilterValidationImpl(paginationValidator);
    }

    @Bean
    Validator<AddressCreate> addressCreateValidator(
    ) {
        return new AddressCreateValidationImpl();
    }

    @Bean
    Validator<ContactCreate> contactCreateValidator(
        PartnerRepository partnerRepository
    ) {
        return new ContactCreateValidationImpl(partnerRepository);
    }

    @Bean
    Validator<Contact> contactUpdateValidator(
        ContactRepository contactRepository,
        Validator<Email> emailUpdateValidator,
        Validator<Phone> phoneUpdateValidator
    ) {
        return new ContactUpdateValidationImpl(contactRepository, emailUpdateValidator, phoneUpdateValidator);
    }

    @Bean
    Validator<ContactsFilter> contactsFilterValidator(
        Validator<Pagination> paginationValidator
    ) {
        return new ContactsFilterValidationImpl(paginationValidator);
    }

    @Bean
    Validator<DocumentChange> documentUpdateValidator(
        DocumentRepository documentRepository,
        DocumentDictionaryRepository documentDictionaryRepository
    ) {
        return new DocumentUpdateValidationImpl(documentRepository, documentDictionaryRepository);
    }

    @Bean
    Validator<DocumentCreate> documentCreateValidator(
        DocumentDictionaryRepository documentDictionaryRepository
    ) {
        return new DocumentCreateValidationImpl(documentDictionaryRepository);
    }

    @Bean
    Validator<DocumentsFilter> documentsFilterValidator(
        Validator<Pagination> paginationValidator
    ) {
        return new DocumentsFilterValidationImpl(paginationValidator);
    }

    @Bean
    Validator<Email> emailUpdateValidator(
        EmailRepository emailRepository
    ) {
        return new EmailUpdateValidationImpl(emailRepository);
    }

    @Bean
    Validator<EmailCreate> emailCreateValidator(
    ) {
        return new EmailCreateValidationImpl();
    }

    @Bean
    Validator<EmailsFilter> emailsFilterValidator(
        Validator<Pagination> paginationValidator
    ) {
        return new EmailsFilterValidationImpl(paginationValidator);
    }

    @Bean
    Validator<Phone> phoneUpdateValidator(
        PhoneRepository phoneRepository
    ) {
        return new PhoneUpdateValidationImpl(phoneRepository);
    }

    @Bean
    Validator<PhoneCreate> phoneCreateValidator(
    ) {
        return new PhoneCreateValidationImpl();
    }

    @Bean
    Validator<PhonesFilter> phonesFilterValidator(
        Validator<Pagination> paginationValidator
    ) {
        return new PhonesFilterValidationImpl(paginationValidator);
    }

    @Bean
    Validator<Partner> partnerUpdateValidator(
        PartnerRepository partnerRepository,
        PartnerMapper partnerMapper,
        Validator<Email> emailUpdateValidator,
        Validator<Phone> phoneUpdateValidator
    ) {
        return new PartnerUpdateValidatorImpl(partnerRepository, partnerMapper, emailUpdateValidator, phoneUpdateValidator);
    }

    @Bean
    Validator<PartnerCreate> partnerCreateValidator(
    ) {
        return new PartnerCreateValidatorImpl();
    }

    @Bean
    Validator<PartnersFilter> partnersFilterValidator(
        Validator<Pagination> paginationValidator
    ) {
        return new PartnersFilterValidationImpl(paginationValidator);
    }

    @Bean
    Validator<Pagination> paginationValidator(
    ) {
        return new PaginationValidationImpl();
    }

    @Bean
    Validator<DocumentTypeChange> documentTypeUpdate(
    ) {
        return new DocumentTypeUpdateValidationImpl();
    }

    @Bean
    ValidationMapper validationMapper() {
        return new ValidationMapperImpl();
    }
}
