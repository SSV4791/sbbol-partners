package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AddressEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankAccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AddressType;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.LegalType;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.Address;
import ru.sberbank.pprb.sbbol.partners.model.Bank;
import ru.sberbank.pprb.sbbol.partners.model.BankAccount;
import ru.sberbank.pprb.sbbol.partners.model.Contact;
import ru.sberbank.pprb.sbbol.partners.model.Document;
import ru.sberbank.pprb.sbbol.partners.model.Partner;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PartnerMapper {

    default UUID mapUuid(final String id) {
        return id != null ? UUID.fromString(id) : null;
    }

    @Mapping(target = "uuid", expression = "java(partner.getId() != null ? partner.getId().toString() : null)")
    @Mapping(target = "type", source = "legalType", qualifiedByName = "toLegalType")
    Partner toPartner(PartnerEntity partner);

    @Named("toLegalType")
    static Partner.TypeEnum toLegalType(LegalType legalType) {
        return legalType != null ? Partner.TypeEnum.valueOf(legalType.name()) : null;
    }

    @Mapping(target = "uuid", expression = "java(account.getId() != null ? account.getId().toString() : null)")
    Account toAccount(AccountEntity account);

    @Mapping(target = "uuid", expression = "java(bank.getId() != null ? bank.getId().toString() : null)")
    @Mapping(target = "accounts", source = "bankAccounts")
    Bank toBank(BankEntity bank);

    @Mapping(target = "uuid", expression = "java(bankAccount.getId() != null ? bankAccount.getId().toString() : null)")
    BankAccount toBankAccount(BankAccountEntity bankAccount);

    @Mapping(target = "uuid", expression = "java(address.getId() != null ? address.getId().toString() : null)")
    @Mapping(target = "type", source = "type", qualifiedByName = "toAddressType")
    Address toAddress(AddressEntity address);

    @Mapping(target = "uuid", expression = "java(document.getId() != null ? document.getId().toString() : null)")
    Document toDocument(DocumentEntity document);

    @Mapping(target = "uuid", expression = "java(contact.getId() != null ? contact.getId().toString() : null)")
    Contact toContact(ContactEntity contact);

    @Named("toAddressType")
    static Address.TypeEnum toAddressType(AddressType addressType) {
        return addressType != null ? Address.TypeEnum.valueOf(addressType.name()) : null;
    }

    @Mapping(target = "id", expression = "java(mapUuid(partner.getUuid()))")
    @Mapping(target = "type", constant = "PARTNER")
    @Mapping(target = "legalType", source = "type", qualifiedByName = "toLegalType")
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "mergeHistory", ignore = true)
    PartnerEntity toPartner(Partner partner);

    @Named("toLegalType")
    static LegalType toLegalType(Partner.TypeEnum legalType) {
        return legalType != null ? LegalType.valueOf(legalType.getValue()) : null;
    }

    @Mapping(target = "id", expression = "java(mapUuid(account.getUuid()))")
    AccountEntity toAccount(Account account);

    @Mapping(target = "id", expression = "java(mapUuid(bank.getUuid()))")
    @Mapping(target = "bankAccounts", source = "accounts")
    @Mapping(target = "account", ignore = true)
    BankEntity toBank(Bank bank);

    @Mapping(target = "id", expression = "java(mapUuid(bankAccount.getUuid()))")
    @Mapping(target = "bank", ignore = true)
    BankAccountEntity toBankAccount(BankAccount bankAccount);

    @Mapping(target = "id", expression = "java(mapUuid(document.getUuid()))")
    @Mapping(target = "partner", ignore = true)
    DocumentEntity toDocument(Document document);

    @Mapping(target = "id", expression = "java(mapUuid(contact.getUuid()))")
    @Mapping(target = "partner", ignore = true)
    ContactEntity toContact(Contact contact);

    @Mapping(target = "id", expression = "java(mapUuid(address.getUuid()))")
    @Mapping(target = "type", source = "type", qualifiedByName = "toAddressType")
    @Mapping(target = "partner", ignore = true)
    AddressEntity toAddress(Address address);

    @Named("toAddressType")
    static AddressType toAddressType(Address.TypeEnum addressType) {
        return addressType != null ? AddressType.valueOf(addressType.getValue()) : null;
    }

    @Mapping(target = "id", expression = "java(mapUuid(partner.getUuid()))")
    @Mapping(target = "type", constant = "PARTNER")
    @Mapping(target = "legalType", source = "type", qualifiedByName = "toLegalType")
    @Mapping(target = "accounts", source = "accounts", qualifiedByName = "updateAccounts")
    @Mapping(target = "addresses", source = "addresses", qualifiedByName = "updateAddress")
    @Mapping(target = "documents", source = "documents", qualifiedByName = "updateDocuments")
    @Mapping(target = "contacts", source = "contacts", qualifiedByName = "updateDocuments")
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "mergeHistory", ignore = true)
    void updatePartner(Partner partner, @MappingTarget() PartnerEntity partnerEntity);

    @Named("updateAccounts")
    void updateAccounts(List<Account> accounts, @MappingTarget() List<AccountEntity> accountEntities);

    @Named("updateAddress")
    void updateAddress(List<Address> addresses, @MappingTarget() List<AddressEntity> addressEntities);

    @Named("updateDocuments")
    void updateDocuments(List<Document> documents, @MappingTarget() List<DocumentEntity> documentEntities);

    @Named("updateDocuments")
    void updateContacts(List<Contact> contacts, @MappingTarget() List<ContactEntity> contactEntities);
}
