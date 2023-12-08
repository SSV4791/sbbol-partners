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
import ru.sberbank.pprb.sbbol.partners.entity.renter.FlatRenter;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.renter.model.Renter;
import ru.sberbank.pprb.sbbol.renter.model.RenterAddress;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static ru.sberbank.pprb.sbbol.partners.entity.renter.DulType.FOREIGNPASSPORT;
import static ru.sberbank.pprb.sbbol.partners.entity.renter.DulType.PASSPORTOFRUSSIA;
import static ru.sberbank.pprb.sbbol.partners.entity.renter.DulType.PASSPORTOFRUSSIAWITHCHIP;
import static ru.sberbank.pprb.sbbol.partners.entity.renter.DulType.RFCITIZENDIPLOMATICPASSPORT;
import static ru.sberbank.pprb.sbbol.partners.entity.renter.DulType.SEAMANPASSPORT;
import static ru.sberbank.pprb.sbbol.partners.entity.renter.DulType.SERVICEMANIDENTITYCARDOFRUSSIA;
import static ru.sberbank.pprb.sbbol.partners.entity.renter.DulType.SERVICEPASSPORTOFRUSSIA;


/**
 * @deprecated {@link ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerMapper}
 */
@Deprecated(forRemoval = true)
@Mapper(
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {
        BaseMapper.class,
    },
    imports = {
        CollectionUtils.class
    }
)
public interface RenterPartnerMapper {

    @Mapping(target = "uuid", source = "uuid", qualifiedByName = "mapUuid")
    @Mapping(target = "type", constant = "RENTER")
    @Mapping(target = "orgName", source = "legalName")
    @Mapping(target = "secondName", source = "lastName")
    @Mapping(target = "phones", expression = "java(toPhones(renter.getPhoneNumbers(), renter.getDigitalId()))")
    @Mapping(target = "emails", expression = "java(toEmails(renter.getEmails(), renter.getDigitalId()))")
    @Mapping(target = "legalType", source = "type", qualifiedByName = "toLegalType")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "comment", ignore = true)
    @Mapping(target = "citizenship", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "search", ignore = true)
    @Mapping(target = "gkuInnEntity", ignore = true)
    @Mapping(target = "migrationDate", ignore = true)
    PartnerEntity toPartner(Renter renter);

    @Named("toPhones")
    default List<PartnerPhoneEntity> toPhones(String phone, String digitalId) {
        if (phone == null) {
            return Collections.emptyList();
        }
        var phoneEntity = new PartnerPhoneEntity();
        phoneEntity.setPhone(phone);
        phoneEntity.setDigitalId(digitalId);
        return List.of(phoneEntity);
    }

    @Named("toEmails")
    default List<PartnerEmailEntity> toEmails(String email, String digitalId) {
        if (email == null) {
            return Collections.emptyList();
        }
        var emailEntity = new PartnerEmailEntity();
        emailEntity.setEmail(email);
        emailEntity.setDigitalId(digitalId);
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
    @Mapping(target = "partnerUuid", source = "uuid", qualifiedByName = "mapUuid")
    @Mapping(target = "comment", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "state", constant = "NOT_SIGNED")
    @Mapping(target = "bank", source = "renter", qualifiedByName = "toBank")
    @Mapping(target = "priorityAccount", ignore = true)
    @Mapping(target = "search", ignore = true)
    @Mapping(target = "partner", ignore = true)
    @Mapping(target = "partnerType", constant = "RENTER")
    AccountEntity toAccount(Renter renter);

    @Named("toBank")
    static BankEntity toBank(Renter renter) {
        if (renter == null) {
            return null;
        }
        var bank = new BankEntity();
        bank.setName(renter.getBankName());
        bank.setBic(renter.getBankBic());
        if (renter.getBankAccount() != null) {
            var bankAccountEntity = new BankAccountEntity();
            bankAccountEntity.setAccount(renter.getBankAccount());
            bankAccountEntity.setBank(bank);
            bank.setBankAccount(
                bankAccountEntity
            );
        }
        return bank;
    }

    @AfterMapping
    default void mapBidirectional(@MappingTarget AccountEntity account) {
        var bank = account.getBank();
        if (bank != null) {
            bank.setAccount(account);
            var bankAccount = bank.getBankAccount();
            if (bankAccount != null) {
                bankAccount.setBank(bank);
            }
        }
    }

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "unifiedUuid", source = "partnerUuid")
    @Mapping(target = "digitalId", source = "digitalId")
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "zipCode", source = "address.zipCode")
    @Mapping(target = "regionCode", source = "address.regionCode")
    @Mapping(target = "region", source = "address.region")
    @Mapping(target = "city", source = "address.city")
    @Mapping(target = "location", source = "address.locality")
    @Mapping(target = "street", source = "address.street")
    @Mapping(target = "building", source = "address.building")
    @Mapping(target = "buildingBlock", source = "address.buildingBlock")
    @Mapping(target = "flat", source = "address.flat")
    AddressEntity toAddress(RenterAddress address, UUID partnerUuid, String digitalId);

    @Mapping(target = "uuid", source = "partnerUuid")
    @Mapping(target = "digitalId", source = "renter.digitalId")
    @Mapping(target = "series", source = "renter.dulSerie")
    @Mapping(target = "number", source = "renter.dulNumber")
    @Mapping(target = "divisionIssue", source = "renter.dulDivisionIssue")
    @Mapping(target = "dateIssue", source = "renter.dulDateIssue")
    @Mapping(target = "divisionCode", source = "renter.dulDivisionCode")
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "unifiedUuid", source = "partnerUuid")
    @Mapping(target = "typeUuid", ignore = true)
    @Mapping(target = "certifierName", ignore = true)
    @Mapping(target = "positionCertifier", ignore = true)
    @Mapping(target = "certifierType", ignore = true)
    DocumentEntity toDocument(Renter renter, UUID partnerUuid);

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

    @Mapping(target = "uuid", source = "partner.uuid", qualifiedByName = "mapUuid")
    @Mapping(target = "digitalId", source = "partner.digitalId")
    @Mapping(target = "type", source = "partner.legalType")
    @Mapping(target = "lastName", source = "partner.secondName")
    @Mapping(target = "legalName", source = "partner.orgName")
    @Mapping(target = "phoneNumbers", expression = "java(toRenterPhone(partner.getPhones(), flatRenter))")
    @Mapping(target = "emails", expression = "java(toRenterEmail(partner.getEmails(), flatRenter))")
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
    Renter toRenter(PartnerEntity partner, FlatRenter flatRenter);

    default String toRenterPhone(List<PartnerPhoneEntity> phones, FlatRenter flatRenter) {
        if (CollectionUtils.isEmpty(phones)) {
            return null;
        }
        if (flatRenter == null) {
            return phones.get(0).getPhone();
        }
        UUID phoneUuid = flatRenter.getPhoneUuid();
        if (phoneUuid != null) {
            return phones.stream()
                .filter(phone -> Objects.equals(phone.getUuid(), phoneUuid))
                .map(PartnerPhoneEntity::getPhone)
                .findFirst().orElse(null);
        }
        return phones.get(0).getPhone();
    }

    default String toRenterEmail(List<PartnerEmailEntity> emails, FlatRenter flatRenter) {
        if (CollectionUtils.isEmpty(emails)) {
            return null;
        }
        if (flatRenter == null) {
            return emails.get(0).getEmail();
        }
        UUID emailUuid = flatRenter.getEmailUuid();
        if (emailUuid != null) {
            return emails.stream()
                .filter(email -> Objects.equals(email.getUuid(), emailUuid))
                .map(PartnerEmailEntity::getEmail)
                .findFirst().orElse(null);
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
    @Mapping(target = "bankBic", expression = "java(accountEntity.getBank() == null ? null : accountEntity.getBank().getBic())")
    @Mapping(target = "bankName", expression = "java(accountEntity.getBank() == null ? null : accountEntity.getBank().getName())")
    @Mapping(
        target = "bankAccount",
        expression = "java(accountEntity.getBank() == null ? null : accountEntity.getBank().getBankAccount() == null ? null : accountEntity.getBank().getBankAccount().getAccount())")
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

    @Mapping(target = "uuid", source = "uuid", qualifiedByName = "mapUuid")
    @Mapping(target = "legalType", source = "type", qualifiedByName = "toLegalType")
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "comment", ignore = true)
    @Mapping(target = "citizenship", ignore = true)
    @Mapping(target = "orgName", source = "legalName")
    @Mapping(target = "secondName", source = "lastName")
    @Mapping(target = "search", ignore = true)
    @Mapping(target = "phones", ignore = true)
    @Mapping(target = "emails", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "gkuInnEntity", ignore = true)
    @Mapping(target = "migrationDate", ignore = true)
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
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "unifiedUuid", ignore = true)
    @Mapping(target = "digitalId", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "location", source = "locality")
    void updateAddress(RenterAddress renterAddress, @MappingTarget AddressEntity entity);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
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
    @Mapping(target = "partnerUuid", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "priorityAccount", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "search", ignore = true)
    @Mapping(target = "partner", ignore = true)
    void updateAccount(Renter renter, @MappingTarget AccountEntity account);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "bankAccount", ignore = true)
    @Mapping(target = "bic", source = "bankBic")
    @Mapping(target = "name", source = "bankName")
    void updateBank(Renter renter, @MappingTarget BankEntity bank);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "bank", ignore = true)
    @Mapping(target = "account", source = "bankAccount")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    void updateBankAccount(Renter renter, @MappingTarget BankAccountEntity bankAccount);
}
