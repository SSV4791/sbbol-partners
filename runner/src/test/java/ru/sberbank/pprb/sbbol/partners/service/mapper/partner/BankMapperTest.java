package ru.sberbank.pprb.sbbol.partners.service.mapper.partner;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankAccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.BankAccountMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.BankMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.BankMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.BankMapperImpl_;
import ru.sberbank.pprb.sbbol.partners.model.Bank;
import ru.sberbank.pprb.sbbol.partners.model.BankAccount;
import ru.sberbank.pprb.sbbol.partners.model.BankChangeFullModel;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(
    classes = {
        BankMapperImpl.class,
        BankMapperImpl_.class,
        BankAccountMapperImpl.class
    }
)
class BankMapperTest extends BaseUnitConfiguration {

    @Autowired
    private BankMapper bankMapper;

    @Test
    void testToBank() {
        var expected = factory.manufacturePojo(Bank.class);
        var actual = bankMapper.toBank(expected);
        var account = new AccountEntity();
        account.setUuid(UUID.fromString(expected.getAccountId()));
        actual.setAccount(account);
        var bankAccount = actual.getBankAccount();
        bankAccount.setBank(actual);
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields(
                "accountId",
                "bankAccount.bankId"
            )
            .isEqualTo(bankMapper.toBank(actual));
    }

    @Test
    void testToBankReverse() {
        var expected = factory.manufacturePojo(BankEntity.class);
        var actual = bankMapper.toBank(expected);
        expected.setAccount(factory.manufacturePojo(AccountEntity.class));
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields(
                "account",
                "lastModifiedDate",
                "type",
                "swiftCode",
                "clearingBankCode",
                "clearingBankCodeName",
                "clearingBankSymbolCode",
                "clearingCountryCode",
                "filial",
                "bankOption",
                "bankAccount.bank",
                "bankAccount.lastModifiedDate"
            )
            .isEqualTo(bankMapper.toBank(actual));
    }

    @Test
    void updateBank() {
        var bank = factory.manufacturePojo(Bank.class);
        var actualBankEntity = factory.manufacturePojo(BankEntity.class);
        bankMapper.updateBank(bank, actualBankEntity);
        var expectedBankEntity = new BankEntity();
        expectedBankEntity.setVersion(bank.getVersion());
        expectedBankEntity.setBic(bank.getBic());
        expectedBankEntity.setName(bank.getName());
        Optional.ofNullable(bank.getBankAccount())
            .ifPresent(bankAccount -> {
                var bankAccountEntity = new BankAccountEntity();
                bankAccountEntity.setUuid(UUID.fromString(bankAccount.getId()));
                bankAccountEntity.setVersion(bankAccount.getVersion());
                bankAccountEntity.setAccount(bankAccount.getBankAccount());
                bankAccountEntity.setBank(expectedBankEntity);
                expectedBankEntity.setBankAccount(bankAccountEntity);
            });
        assertThat(actualBankEntity)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .ignoringFields(
                "uuid",
                "lastModifiedDate",
                "account",
                "bankAccount.uuid",
                "bankAccount.bank.uuid",
                "bankAccount.bank.account",
                "bankAccount.lastModifiedDate",
                "account.uuid",
                "account.search",
                "account.lastModifiedDate",
                "bankAccount.bank.lastModifiedDate"
            )
            .isEqualTo(expectedBankEntity);
    }

    @Test
    void patchBank() {
        var bank = new Bank();
        bank.setName("Updated bank name");
        var actualBankEntity = factory.manufacturePojo(BankEntity.class);
        bankMapper.patchBank(bank, actualBankEntity);
        var expectedBankEntity = new BankEntity();
        expectedBankEntity.setVersion(actualBankEntity.getVersion());
        expectedBankEntity.setBic(actualBankEntity.getBic());
        expectedBankEntity.setName(bank.getName());
        expectedBankEntity.setBankAccount(actualBankEntity.getBankAccount());
        assertThat(actualBankEntity)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .ignoringFields(
                "uuid",
                "lastModifiedDate",
                "account",
                "bankAccount.uuid",
                "bankAccount.bank.uuid",
                "bankAccount.bank.account",
                "bankAccount.lastModifiedDate",
                "account.uuid",
                "account.search",
                "account.lastModifiedDate",
                "bankAccount.bank.lastModifiedDate"
            )
            .isEqualTo(expectedBankEntity);
    }

    @Test
    void mapBankChangeFullModelToBank() {
        var bankChangeFullModel = factory.manufacturePojo(BankChangeFullModel.class);
        var actualBank = bankMapper.toBank(bankChangeFullModel);
        var expectedBank = new Bank()
            .id(bankChangeFullModel.getId())
            .version(bankChangeFullModel.getVersion())
            .bic(bankChangeFullModel.getBic())
            .name(bankChangeFullModel.getName());
        Optional.ofNullable(bankChangeFullModel.getBankAccount())
            .ifPresent(bankAccountChangeFullModel -> {
                var bankAccount = new BankAccount()
                    .id(bankAccountChangeFullModel.getId())
                    .version(bankAccountChangeFullModel.getVersion())
                    .bankId(bankChangeFullModel.getId())
                    .bankAccount(bankAccountChangeFullModel.getBankAccount());
                expectedBank.setBankAccount(bankAccount);
            });
        assertThat(actualBank)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expectedBank);
    }
}
