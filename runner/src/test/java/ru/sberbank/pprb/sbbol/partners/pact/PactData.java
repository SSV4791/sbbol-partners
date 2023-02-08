package ru.sberbank.pprb.sbbol.partners.pact;

import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.Address;
import ru.sberbank.pprb.sbbol.partners.model.AddressCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.AddressType;
import ru.sberbank.pprb.sbbol.partners.model.Bank;
import ru.sberbank.pprb.sbbol.partners.model.BankAccount;
import ru.sberbank.pprb.sbbol.partners.model.BankAccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.BankCreate;
import ru.sberbank.pprb.sbbol.partners.model.CertifierType;
import ru.sberbank.pprb.sbbol.partners.model.Citizenship;
import ru.sberbank.pprb.sbbol.partners.model.Contact;
import ru.sberbank.pprb.sbbol.partners.model.ContactCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.Descriptions;
import ru.sberbank.pprb.sbbol.partners.model.Document;
import ru.sberbank.pprb.sbbol.partners.model.DocumentCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.DocumentType;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModelResponse;
import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.model.PhoneCreate;
import ru.sberbank.pprb.sbbol.partners.model.SignType;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class PactData {
    public static final String DIGITAL_ID = "777";
    public static final String UUID_ID = "00000000-1111-2222-3333-444444444444";
    public static final String DESCRIPTION = "SOME DESCRIPTION";
    public static final Integer ERROR_CODE = 100;
    public static final String MESSAGE = "SOME MESSAGE";
    public static final String ERROR_TYPE_CRITICAL_ERROR = Error.TypeEnum.CRITICAL.toString();
    public static final String FIELD = "SOME FIELD";
    public static final String LEGAL_FORM_LEGAL_ENTITY = LegalForm.LEGAL_ENTITY.toString();
    public static final String ORG_NAME = "ООО РОМАШКА";
    public static final String FIRST_NAME = "ИВАН";
    public static final String SECOND_NAME = "ИВАНОВ";
    public static final String MIDDLE_NAME = "ИВАНОВИЧ";
    public static final String INN_LEGAL_ENTITY = "3134043971";
    public static final String KPP = "182801502";
    public static final String OGRN = "9107279058669";
    public static final String OKPO = "43433815";
    public static final String PHONE = "0079000001111";
    public static final String EMAIL = "email@sberbank.ru";
    public static final String COMMENT = "SOME COMMENT";
    public static final String CITIZENSHIP = "UNKNOWN";
    public static final Long VERSION = 1L;
    public static final Boolean GKU = false;
    public static final Boolean BUDGET = false;
    public static final String ACCOUNT = "40702810100260004426";
    public static final String BANK_ACCOUNT = "30101810400000000225";
    public static final String BIC = "044525225";
    public static final String BANK_NAME = "ПАО СБЕРБАНК";
    public static final Boolean MEDIARY = true;
    public static final Boolean PRIORITY_ACCOUNT = true;
    public static final String SIGN_STATE = SignType.SIGNED.toString();
    public static final String POSITION = "SOME POSITION";
    public static final String ADDRESS_TYPE_LEGAL_ADDRESS = AddressType.LEGAL_ADDRESS.toString();
    public static final String ZIP_CODE = "777777";
    public static final String REGION = "SOME REGION";
    public static final String REGION_CODE = "0332";
    public static final String CITY = "SOME CITY";
    public static final String LOCATION = "SOME LOCATION";
    public static final String STREET = "SOME STREET";
    public static final String BUILDING = "SOME BUILDING";
    public static final String BUILDING_BLOCK = "SOME BUILDING BLOCK";
    public static final String FLAT = "SOME FLAT";
    public static final String DOCUMENT_SERIES = "SOME SERIES";
    public static final String DOCUMENT_NUMBER = "SOME NUMBER";
    public static final Date DATE = Date.valueOf("2022-12-31");
    public static final String DIVISION_ISSUE = "SOME DIVISION ISSUE";
    public static final String DIVISION_CODE = "SOME DIVISION CODE";
    public static final String CERTIFIER_NAME = "SOME CERTIFIER NAME";
    public static final String POSITION_CERTIFIER = "SOME POSITION CERTIFIER";
    public static final String CERTIFIER_TYPE = CertifierType.NOTARY.toString();
    public static final String DOCUMENT_TYPE = "SOME DOCUMENT TYPE";
    public static final Boolean DELETED = false;

    public Partner partner() {
        return new Partner()
            .id(UUID_ID)
            .digitalId(DIGITAL_ID)
            .version(VERSION)
            .legalForm(LegalForm.fromValue(LEGAL_FORM_LEGAL_ENTITY))
            .orgName(ORG_NAME)
            .firstName(FIRST_NAME)
            .secondName(SECOND_NAME)
            .middleName(MIDDLE_NAME)
            .inn(INN_LEGAL_ENTITY)
            .kpp(KPP)
            .okpo(OKPO)
            .ogrn(OGRN)
            .phones(Set.of(phone()))
            .emails(Set.of(email()))
            .comment(COMMENT)
            .gku(GKU)
            .citizenship(Citizenship.fromValue(PactData.CITIZENSHIP));
    }

    public Phone phone() {
        return new Phone()
            .id(UUID_ID)
            .digitalId(DIGITAL_ID)
            .unifiedId(UUID_ID)
            .phone(PHONE)
            .version(VERSION);
    }

    public PhoneCreate phoneCreate() {
        return new PhoneCreate()
            .digitalId(DIGITAL_ID)
            .unifiedId(UUID_ID)
            .phone(PHONE);
    }

    public Email email() {
        return new Email()
            .id(UUID_ID)
            .digitalId(DIGITAL_ID)
            .unifiedId(UUID_ID)
            .email(EMAIL)
            .version(VERSION);
    }

    public PartnerCreate partnerCreate() {
        return new PartnerCreate()
            .digitalId(DIGITAL_ID)
            .legalForm(LegalForm.fromValue(LEGAL_FORM_LEGAL_ENTITY))
            .orgName(ORG_NAME)
            .firstName(FIRST_NAME)
            .secondName(SECOND_NAME)
            .middleName(MIDDLE_NAME)
            .inn(INN_LEGAL_ENTITY)
            .kpp(KPP)
            .okpo(OKPO)
            .ogrn(OGRN)
            .phones(Set.of(PHONE))
            .emails(Set.of(EMAIL))
            .comment(COMMENT)
            .citizenship(Citizenship.fromValue(PactData.CITIZENSHIP));
    }

    public PartnerCreateFullModel partnerCreateFullModel() {
        return new PartnerCreateFullModel()
            .digitalId(DIGITAL_ID)
            .legalForm(LegalForm.fromValue(LEGAL_FORM_LEGAL_ENTITY))
            .orgName(ORG_NAME)
            .firstName(FIRST_NAME)
            .secondName(SECOND_NAME)
            .middleName(MIDDLE_NAME)
            .inn(INN_LEGAL_ENTITY)
            .kpp(KPP)
            .okpo(OKPO)
            .ogrn(OGRN)
            .comment(COMMENT)
            .citizenship(Citizenship.fromValue(PactData.CITIZENSHIP))
            .accounts(Set.of(accountCreateFullModel()))
            .phones(Set.of(PHONE))
            .emails(Set.of(EMAIL))
            .contacts(Set.of(contactCreateFullModel()))
            .address(Set.of(addressCreateFullModel()))
            .documents(Set.of(documentCreateFullModel()));
    }

    public PartnerCreateFullModelResponse partnerCreateFullModelResponse() {
        return new PartnerCreateFullModelResponse()
            .id(UUID_ID)
            .digitalId(DIGITAL_ID)
            .legalForm(LegalForm.fromValue(LEGAL_FORM_LEGAL_ENTITY))
            .version(VERSION)
            .orgName(ORG_NAME)
            .firstName(FIRST_NAME)
            .secondName(SECOND_NAME)
            .middleName(MIDDLE_NAME)
            .inn(INN_LEGAL_ENTITY)
            .ogrn(OGRN)
            .kpp(KPP)
            .okpo(OKPO)
            .accounts(List.of(account()))
            .documents(List.of(document()))
            .address(List.of(address()))
            .contacts(List.of(contact()))
            .phones(List.of(phone()))
            .emails(List.of(email()))
            .comment(COMMENT)
            .gku(GKU)
            .budget(BUDGET)
            .citizenship(Citizenship.fromValue(CITIZENSHIP));
    }

    public Document document() {
        return new Document()
            .id(UUID_ID)
            .digitalId(DIGITAL_ID)
            .unifiedId(UUID_ID)
            .version(VERSION)
            .documentType(documentType())
            .series(DOCUMENT_SERIES)
            .number(DOCUMENT_NUMBER)
            .dateIssue(LocalDate.parse(DATE.toString()))
            .divisionIssue(DIVISION_ISSUE)
            .divisionCode(DIVISION_CODE)
            .certifierName(CERTIFIER_NAME)
            .positionCertifier(POSITION_CERTIFIER)
            .certifierType(CertifierType.fromValue(CERTIFIER_TYPE));
    }

    public DocumentType documentType() {
        return new DocumentType()
            .id(UUID_ID)
            .documentType(DOCUMENT_TYPE)
            .description(DESCRIPTION)
            .deleted(DELETED)
            .legalForms(List.of(LegalForm.LEGAL_ENTITY));
    }

    public DocumentCreateFullModel documentCreateFullModel() {
        return new DocumentCreateFullModel()
            .documentTypeId(UUID_ID)
            .series(DOCUMENT_SERIES)
            .number(DOCUMENT_NUMBER)
            .dateIssue(LocalDate.parse(DATE.toString()))
            .divisionIssue(DIVISION_ISSUE)
            .divisionCode(DIVISION_CODE)
            .certifierName(CERTIFIER_NAME)
            .positionCertifier(POSITION_CERTIFIER)
            .certifierType(CertifierType.fromValue(CERTIFIER_TYPE));
    }

    public Address address() {
        return new Address()
            .id(UUID_ID)
            .version(VERSION)
            .type(AddressType.fromValue(ADDRESS_TYPE_LEGAL_ADDRESS))
            .digitalId(DIGITAL_ID)
            .unifiedId(UUID_ID)
            .zipCode(ZIP_CODE)
            .region(REGION)
            .regionCode(REGION_CODE)
            .city(CITY)
            .location(LOCATION)
            .street(STREET)
            .building(BUILDING)
            .buildingBlock(BUILDING_BLOCK)
            .flat(FLAT);
    }

    public AddressCreateFullModel addressCreateFullModel() {
        return new AddressCreateFullModel()
            .type(AddressType.fromValue(ADDRESS_TYPE_LEGAL_ADDRESS))
            .zipCode(ZIP_CODE)
            .region(REGION)
            .regionCode(REGION_CODE)
            .city(CITY)
            .location(LOCATION)
            .street(STREET)
            .building(BUILDING)
            .buildingBlock(BUILDING_BLOCK)
            .flat(FLAT);
    }

    public Contact contact() {
        return new Contact()
            .id(UUID_ID)
            .digitalId(DIGITAL_ID)
            .partnerId(UUID_ID)
            .orgName(ORG_NAME)
            .version(VERSION)
            .firstName(FIRST_NAME)
            .secondName(SECOND_NAME)
            .middleName(MIDDLE_NAME)
            .position(POSITION)
            .phones(Set.of(phone()))
            .emails(Set.of(email()))
            .legalForm(LegalForm.fromValue(LEGAL_FORM_LEGAL_ENTITY));
    }

    public ContactCreateFullModel contactCreateFullModel() {
        return new ContactCreateFullModel()
            .orgName(ORG_NAME)
            .firstName(FIRST_NAME)
            .secondName(SECOND_NAME)
            .middleName(MIDDLE_NAME)
            .position(POSITION)
            .phones(Set.of(PHONE))
            .emails(Set.of(EMAIL))
            .legalForm(LegalForm.fromValue(LEGAL_FORM_LEGAL_ENTITY));
    }

    public AccountCreateFullModel accountCreateFullModel() {
        return new AccountCreateFullModel()
            .account(ACCOUNT)
            .comment(COMMENT)
            .bank(bankCreate());
    }

    public Account account() {
        return new Account()
            .id(UUID_ID)
            .partnerId(UUID_ID)
            .digitalId(DIGITAL_ID)
            .version(VERSION)
            .budget(BUDGET)
            .account(ACCOUNT)
            .priorityAccount(PRIORITY_ACCOUNT)
            .bank(bank())
            .state(SignType.fromValue(SIGN_STATE))
            .comment(COMMENT);
    }

    public Bank bank() {
        return new Bank()
            .id(UUID_ID)
            .accountId(UUID_ID)
            .version(VERSION)
            .bic(BIC)
            .name(BANK_NAME)
            .mediary(MEDIARY)
            .bankAccount(bankAccount());
    }

    public BankCreate bankCreate() {
        return new BankCreate()
            .bic(BIC)
            .name(BANK_NAME)
            .mediary(MEDIARY)
            .bankAccount(bankAccountCreate());
    }

    public BankAccount bankAccount() {
        return new BankAccount()
            .id(UUID_ID)
            .bankId(UUID_ID)
            .version(VERSION)
            .bankAccount(BANK_ACCOUNT);
    }

    public BankAccountCreate bankAccountCreate() {
        return new BankAccountCreate()
            .bankAccount(BANK_ACCOUNT);
    }

    public Error error() {
        return new Error()
            .code(ERROR_CODE)
            .message(MESSAGE)
            .type(Error.TypeEnum.fromValue(ERROR_TYPE_CRITICAL_ERROR))
            .descriptions(List.of(descriptions()));
    }

    public Descriptions descriptions() {
        return new Descriptions()
            .field(FIELD)
            .message(List.of(MESSAGE));
    }
}
