package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
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
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PartnerMapperTest {

    private static final PartnerMapper mapper = Mappers.getMapper(PartnerMapper.class);
    private static final PodamFactory factory = new PodamFactoryImpl();

    @Test
    void toPartner() {
        Partner partner = factory.manufacturePojo(Partner.class)
            .uuid(UUID.randomUUID().toString());
        for (var account : partner.getAccounts()) {
            account.uuid(UUID.randomUUID().toString());
            account.getBank().uuid(UUID.randomUUID().toString());
            for (var bankAccount : account.getBank().getAccounts()) {
                bankAccount.uuid(UUID.randomUUID().toString());
            }
        }
        for (var address : partner.getAddresses()) {
            address.uuid(UUID.randomUUID().toString());
        }
        for (var contact : partner.getContacts()) {
            contact.uuid(UUID.randomUUID().toString());
        }
        for (var document : partner.getDocuments()) {
            document.uuid(UUID.randomUUID().toString());
        }
        PartnerEntity partnerEntity = mapper.toPartner(partner);
        assertThat(partner)
            .isEqualTo(mapper.toPartner(partnerEntity));
    }

    @Test
    void toLegalType() {
        Partner.TypeEnum typeEnum = factory.manufacturePojo(Partner.TypeEnum.class);
        LegalType legalType = PartnerMapper.toLegalType(typeEnum);
        assertThat(typeEnum)
            .isEqualTo(PartnerMapper.toLegalType(legalType));
    }

    @Test
    void toAddress() {
        Address address = factory.manufacturePojo(Address.class)
            .uuid(UUID.randomUUID().toString());
        AddressEntity addressEntity = mapper.toAddress(address);
        assertThat(address)
            .isEqualTo(mapper.toAddress(addressEntity));
    }

    @Test
    void toAccount() {
        Account account = factory.manufacturePojo(Account.class)
            .uuid(UUID.randomUUID().toString());
        account.getBank().uuid(UUID.randomUUID().toString());
        for (var bankAccount : account.getBank().getAccounts()) {
            bankAccount.uuid(UUID.randomUUID().toString());
        }
        AccountEntity accountEntity = mapper.toAccount(account);
        assertThat(account)
            .isEqualTo(mapper.toAccount(accountEntity));
    }

    @Test
    void toBank() {
        Bank bank = factory.manufacturePojo(Bank.class)
            .uuid(UUID.randomUUID().toString());
        for (var account : bank.getAccounts()) {
            account.uuid(UUID.randomUUID().toString());
        }
        BankEntity bankEntity = mapper.toBank(bank);
        assertThat(bank)
            .isEqualTo(mapper.toBank(bankEntity));
    }

    @Test
    void toBankAccount() {
        BankAccount bankAccount = factory.manufacturePojo(BankAccount.class)
            .uuid(UUID.randomUUID().toString());
        BankAccountEntity bankAccountEntity = mapper.toBankAccount(bankAccount);
        assertThat(bankAccount)
            .isEqualTo(mapper.toBankAccount(bankAccountEntity));
    }

    @Test
    void toDocument() {
        Document document = factory.manufacturePojo(Document.class)
            .uuid(UUID.randomUUID().toString());
        DocumentEntity documentEntity = mapper.toDocument(document);
        assertThat(document)
            .isEqualTo(mapper.toDocument(documentEntity));
    }

    @Test
    void toContact() {
        Contact contact = factory.manufacturePojo(Contact.class)
            .uuid(UUID.randomUUID().toString());
        ContactEntity contactEntity = mapper.toContact(contact);
        assertThat(contact)
            .isEqualTo(mapper.toContact(contactEntity));
    }

    @Test
    void toAddressType() {
        Address.TypeEnum typeEnum = factory.manufacturePojo(Address.TypeEnum.class);
        AddressType addressType = PartnerMapper.toAddressType(typeEnum);
        assertThat(typeEnum)
            .isEqualTo(PartnerMapper.toAddressType(addressType));
    }
}
