package ru.sberbank.pprb.sbbol.partners.mapper.renter;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AddressEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankAccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentTypeEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEmailEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerPhoneEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.DocumentType;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.LegalType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.renter.model.Renter;
import ru.sberbank.pprb.sbbol.renter.model.RenterAddress;

import java.util.List;

import static ru.sberbank.pprb.sbbol.partners.entity.renter.DulType.FOREIGNPASSPORT;
import static ru.sberbank.pprb.sbbol.partners.entity.renter.DulType.PASSPORTOFRUSSIA;
import static ru.sberbank.pprb.sbbol.partners.entity.renter.DulType.PASSPORTOFRUSSIAWITHCHIP;
import static ru.sberbank.pprb.sbbol.partners.entity.renter.DulType.RFCITIZENDIPLOMATICPASSPORT;
import static ru.sberbank.pprb.sbbol.partners.entity.renter.DulType.SEAMANPASSPORT;
import static ru.sberbank.pprb.sbbol.partners.entity.renter.DulType.SERVICEMANIDENTITYCARDOFRUSSIA;
import static ru.sberbank.pprb.sbbol.partners.entity.renter.DulType.SERVICEPASSPORTOFRUSSIA;


@Deprecated
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    imports = {
        CollectionUtils.class
    }
)
public interface RenterPartnerMapper extends BaseMapper {

    @Mapping(target = "uuid", expression = "java(mapUuid(renter.getUuid()))")
    @Mapping(target = "type", constant = "RENTER")
    @Mapping(target = "orgName", source = "legalName")
    @Mapping(target = "secondName", source = "lastName")
    @Mapping(target = "phones", source = "phoneNumbers", qualifiedByName = "toPhones")
    @Mapping(target = "emails", source = "emails", qualifiedByName = "toEmails")
    @Mapping(target = "legalType", source = "type", qualifiedByName = "toLegalType")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "comment", ignore = true)
    @Mapping(target = "citizenship", ignore = true)
    PartnerEntity toPartner(Renter renter);

    @Named("toPhones")
    static List<PartnerPhoneEntity> toPhones(String phone) {
        if (phone == null) {
            return null;
        }
        var phoneEntity = new PartnerPhoneEntity();
        phoneEntity.setPhone(phone);
        return List.of(phoneEntity);
    }

    @Named("toEmails")
    static List<PartnerEmailEntity> toEmails(String email) {
        if (email == null) {
            return null;
        }
        var emailEntity = new PartnerEmailEntity();
        emailEntity.setEmail(email);
        return List.of(emailEntity);
    }

    @AfterMapping
    default void mapBidirectional(@MappingTarget PartnerEntity partner) {
        var phones = partner.getPhones();
        if (phones != null) {
            for (var phone : phones) {
                phone.setPartner(partner);
            }
        }
        var emails = partner.getEmails();
        if (emails != null) {
            for (var email : emails) {
                email.setPartner(partner);
            }
        }
    }

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "partnerUuid", expression = "java(mapUuid(renter.getUuid()))")
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "state", constant = "NOT_SIGNED")
    @Mapping(target = "banks", source = "renter", qualifiedByName = "toBanks")
    @Mapping(target = "signCollectionId", ignore = true)
    AccountEntity toAccount(Renter renter);

    @Named("toBanks")
    static List<BankEntity> toBanks(Renter renter) {
        if (renter == null) {
            return null;
        }
        var bank = new BankEntity();
        var bankAccountEntity = new BankAccountEntity();
        bankAccountEntity.setBank(bank);
        bankAccountEntity.setAccount(renter.getBankAccount());
        bank.setName(renter.getBankName());
        bank.setBankAccounts(
            List.of(bankAccountEntity)
        );
        bank.setBic(renter.getBankBic());
        return List.of(bank);
    }

    @AfterMapping
    default void mapBidirectional(@MappingTarget AccountEntity account) {
        var banks = account.getBanks();
        if (banks != null) {
            for (var bank : banks) {
                bank.setAccount(account);
                for (BankAccountEntity bankAccount : bank.getBankAccounts()) {
                    bankAccount.setBank(bank);
                }
            }
        }
    }

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "unifiedUuid", ignore = true)
    @Mapping(target = "digitalId", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "location", source = "locality")
    AddressEntity toAddress(RenterAddress address);

    @Mapping(target = "series", source = "dulSerie")
    @Mapping(target = "number", source = "dulNumber")
    @Mapping(target = "divisionIssue", source = "dulDivisionIssue")
    @Mapping(target = "dateIssue", source = "dulDateIssue")
    @Mapping(target = "divisionCode", source = "dulDivisionCode")
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "unifiedUuid", ignore = true)
    @Mapping(target = "typeUuid", ignore = true)
    @Mapping(target = "certifierName", ignore = true)
    @Mapping(target = "positionCertifier", ignore = true)
    @Mapping(target = "certifierType", ignore = true)
    DocumentEntity toDocument(Renter renter);

    @Named("toDocumentType")
    static DocumentType toDocumentType(Renter.DulTypeEnum type) {
        if (type == null) {
            return null;
        }
        return switch (type) {
            case SEAMANPASSPORT -> DocumentType.SEAMAN_PASSPORT;
            case FOREIGNPASSPORT -> DocumentType.FOREIGN_PASSPORT;
            case PASSPORTOFRUSSIA -> DocumentType.PASSPORT_OF_RUSSIA;
            case SERVICEPASSPORTOFRUSSIA -> DocumentType.SERVICE_PASSPORT_OF_RUSSIA;
            case PASSPORTOFRUSSIAWITHCHIP -> DocumentType.PASSPORT_OF_RUSSIA_WITH_CHIP;
            case RFCITIZENDIPLOMATICPASSPORT -> DocumentType.RF_CITIZEN_DIPLOMATIC_PASSPORT;
            case SERVICEMANIDENTITYCARDOFRUSSIA -> DocumentType.SERVICEMAN_IDENTITY_CARD_OF_RUSSIA;
        };
    }

    @Mapping(target = "uuid", expression = "java(partner.getUuid().toString())")
    @Mapping(target = "type", source = "legalType")
    @Mapping(target = "lastName", source = "secondName")
    @Mapping(target = "legalName", source = "orgName")
    @Mapping(target = "phoneNumbers", source = "phones", qualifiedByName = "toRenterPhone")
    @Mapping(target = "emails", source = "emails", qualifiedByName = "toRenterEmail")
    @Mapping(target = "dulType", ignore = true)
    @Mapping(target = "dulName", ignore = true)
    @Mapping(target = "dulSerie", ignore = true)
    @Mapping(target = "dulNumber", ignore = true)
    @Mapping(target = "dulDivisionIssue", ignore = true)
    @Mapping(target = "dulDateIssue", ignore = true)
    @Mapping(target = "dulDivisionCode", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "bankBic", ignore = true)
    @Mapping(target = "bankName", ignore = true)
    @Mapping(target = "bankAccount", ignore = true)
    @Mapping(target = "legalAddress", ignore = true)
    @Mapping(target = "physicalAddress", ignore = true)
    @Mapping(target = "checkResults", ignore = true)
    Renter toRenter(PartnerEntity partner);

    @Named("toRenterPhone")
    static String toRenterPhone(List<PartnerPhoneEntity> phones) {
        if (CollectionUtils.isEmpty(phones)) {
            return null;
        }
        return phones.get(0).getPhone();
    }

    @Named("toRenterEmail")
    static String toRenterEmail(List<PartnerEmailEntity> emails) {
        if (CollectionUtils.isEmpty(emails)) {
            return null;
        }
        return emails.get(0).getEmail();
    }

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "legalName", ignore = true)
    @Mapping(target = "inn", ignore = true)
    @Mapping(target = "kpp", ignore = true)
    @Mapping(target = "ogrn", ignore = true)
    @Mapping(target = "okpo", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "middleName", ignore = true)
    @Mapping(target = "dulType", ignore = true)
    @Mapping(target = "dulName", ignore = true)
    @Mapping(target = "dulSerie", ignore = true)
    @Mapping(target = "dulNumber", ignore = true)
    @Mapping(target = "dulDivisionIssue", ignore = true)
    @Mapping(target = "dulDateIssue", ignore = true)
    @Mapping(target = "dulDivisionCode", ignore = true)
    @Mapping(target = "phoneNumbers", ignore = true)
    @Mapping(target = "emails", ignore = true)
    @Mapping(target = "legalAddress", ignore = true)
    @Mapping(target = "physicalAddress", ignore = true)
    @Mapping(target = "checkResults", ignore = true)
    @Mapping(target = "bankBic", expression = "java(CollectionUtils.isEmpty(accountEntity.getBanks()) ? null : accountEntity.getBanks().get(0).getBic())")
    @Mapping(target = "bankName", expression = "java(CollectionUtils.isEmpty(accountEntity.getBanks()) ? null : accountEntity.getBanks().get(0).getName())")
    @Mapping(target = "bankAccount", expression = "java(CollectionUtils.isEmpty(accountEntity.getBanks()) ? null : CollectionUtils.isEmpty(accountEntity.getBanks().get(0).getBankAccounts()) ? null : accountEntity.getBanks().get(0).getBankAccounts().get(0).getAccount())")
    void addRenterAccount(AccountEntity accountEntity, @MappingTarget Renter renter);

    @Mapping(target = "locality", source = "location")
    RenterAddress toRenterAddress(AddressEntity addressEntity);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "dulType", ignore = true)
    @Mapping(target = "dulName", ignore = true)
    @Mapping(target = "legalName", ignore = true)
    @Mapping(target = "inn", ignore = true)
    @Mapping(target = "kpp", ignore = true)
    @Mapping(target = "ogrn", ignore = true)
    @Mapping(target = "okpo", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "middleName", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "bankBic", ignore = true)
    @Mapping(target = "bankName", ignore = true)
    @Mapping(target = "bankAccount", ignore = true)
    @Mapping(target = "phoneNumbers", ignore = true)
    @Mapping(target = "emails", ignore = true)
    @Mapping(target = "legalAddress", ignore = true)
    @Mapping(target = "physicalAddress", ignore = true)
    @Mapping(target = "checkResults", ignore = true)
    @Mapping(target = "dulSerie", source = "series")
    @Mapping(target = "dulNumber", source = "number")
    @Mapping(target = "dulDivisionIssue", source = "divisionIssue")
    @Mapping(target = "dulDateIssue", source = "dateIssue")
    @Mapping(target = "dulDivisionCode", source = "divisionCode")
    void addRenterDocument(DocumentEntity documentEntity, @MappingTarget Renter renter);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "digitalId", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "legalName", ignore = true)
    @Mapping(target = "inn", ignore = true)
    @Mapping(target = "kpp", ignore = true)
    @Mapping(target = "ogrn", ignore = true)
    @Mapping(target = "okpo", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "middleName", ignore = true)
    @Mapping(target = "dulSerie", ignore = true)
    @Mapping(target = "dulNumber", ignore = true)
    @Mapping(target = "dulDivisionIssue", ignore = true)
    @Mapping(target = "dulDateIssue", ignore = true)
    @Mapping(target = "dulDivisionCode", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "bankBic", ignore = true)
    @Mapping(target = "bankName", ignore = true)
    @Mapping(target = "bankAccount", ignore = true)
    @Mapping(target = "phoneNumbers", ignore = true)
    @Mapping(target = "emails", ignore = true)
    @Mapping(target = "legalAddress", ignore = true)
    @Mapping(target = "physicalAddress", ignore = true)
    @Mapping(target = "checkResults", ignore = true)
    @Mapping(target = "dulType", source = "systemName", qualifiedByName = "toDocumentType")
    @Mapping(target = "dulName", source = "systemName", qualifiedByName = "toDocumentName")
    void addRenterDocumentType(DocumentTypeEntity documentTypeEntity, @MappingTarget Renter renter);

    @Named("toDocumentType")
    static Renter.DulTypeEnum toDocumentType(String type) {
        if (type == null) {
            return null;
        }
        var documentType = DocumentType.valueOf(type);
        return switch (documentType) {
            case SEAMAN_PASSPORT -> Renter.DulTypeEnum.SEAMANPASSPORT;
            case FOREIGN_PASSPORT -> Renter.DulTypeEnum.FOREIGNPASSPORT;
            case PASSPORT_OF_RUSSIA -> Renter.DulTypeEnum.PASSPORTOFRUSSIA;
            case SERVICE_PASSPORT_OF_RUSSIA -> Renter.DulTypeEnum.SERVICEPASSPORTOFRUSSIA;
            case PASSPORT_OF_RUSSIA_WITH_CHIP -> Renter.DulTypeEnum.PASSPORTOFRUSSIAWITHCHIP;
            case RF_CITIZEN_DIPLOMATIC_PASSPORT -> Renter.DulTypeEnum.RFCITIZENDIPLOMATICPASSPORT;
            case SERVICEMAN_IDENTITY_CARD_OF_RUSSIA -> Renter.DulTypeEnum.SERVICEMANIDENTITYCARDOFRUSSIA;
        };
    }

    @Named("toDocumentName")
    static String toDocumentName(String type) {
        if (type == null) {
            return null;
        }
        var documentType = DocumentType.valueOf(type);
        return switch (documentType) {
            case SEAMAN_PASSPORT -> SEAMANPASSPORT.getDesc();
            case FOREIGN_PASSPORT -> FOREIGNPASSPORT.getDesc();
            case PASSPORT_OF_RUSSIA -> PASSPORTOFRUSSIA.getDesc();
            case SERVICE_PASSPORT_OF_RUSSIA -> SERVICEPASSPORTOFRUSSIA.getDesc();
            case PASSPORT_OF_RUSSIA_WITH_CHIP -> PASSPORTOFRUSSIAWITHCHIP.getDesc();
            case RF_CITIZEN_DIPLOMATIC_PASSPORT -> RFCITIZENDIPLOMATICPASSPORT.getDesc();
            case SERVICEMAN_IDENTITY_CARD_OF_RUSSIA -> SERVICEMANIDENTITYCARDOFRUSSIA.getDesc();
        };
    }

    @Mapping(target = "legalType", source = "type", qualifiedByName = "toLegalType")
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "comment", ignore = true)
    @Mapping(target = "citizenship", ignore = true)
    @Mapping(target = "orgName", source = "legalName")
    @Mapping(target = "secondName", source = "lastName")
    @Mapping(target = "phones", ignore = true)
    @Mapping(target = "emails", ignore = true)
    void updatePartner(Renter renter, @MappingTarget PartnerEntity partnerEntity);

    @Named("toLegalType")
    static LegalType toLegalType(Renter.TypeEnum type) {
        if (type == null) {
            return null;
        }
        return switch (type) {
            case LEGAL_ENTITY -> LegalType.LEGAL_ENTITY;
            case PHYSICAL_PERSON -> LegalType.PHYSICAL_PERSON;
            case ENTREPRENEUR -> LegalType.ENTREPRENEUR;
        };
    }

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "unifiedUuid", ignore = true)
    @Mapping(target = "digitalId", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "location", source = "locality")
    void updateAddress(RenterAddress renterAddress, @MappingTarget AddressEntity entity);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "unifiedUuid", ignore = true)
    @Mapping(target = "typeUuid", ignore = true)
    @Mapping(target = "certifierName", ignore = true)
    @Mapping(target = "positionCertifier", ignore = true)
    @Mapping(target = "certifierType", ignore = true)
    @Mapping(target = "series", source = "dulSerie")
    @Mapping(target = "number", source = "dulNumber")
    @Mapping(target = "divisionIssue", source = "dulDivisionIssue")
    @Mapping(target = "dateIssue", source = "dulDateIssue")
    @Mapping(target = "divisionCode", source = "dulDivisionCode")
    void updateDocument(Renter renter, @MappingTarget DocumentEntity entity);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "intermediary", ignore = true)
    @Mapping(target = "bankAccounts", ignore = true)
    @Mapping(target = "bic", source = "bankBic")
    @Mapping(target = "name", source = "bankName")
    void updateBank(Renter renter, @MappingTarget BankEntity bank);
}
