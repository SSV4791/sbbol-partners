package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankAccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.config.BaseConfiguration;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.Bank;
import ru.sberbank.pprb.sbbol.partners.model.BankAccount;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountMapperTest extends BaseConfiguration {

    private static final AccountMapper mapper = Mappers.getMapper(AccountMapper.class);

    @Test
    void testToAccount() {
        Account expected = factory.manufacturePojo(Account.class);
        for (Bank bank : expected.getBanks()) {
            bank.setAccountUuid(expected.getUuid());
            for (BankAccount bankAccount : bank.getBankAccounts()) {
                bankAccount.setBankUuid(bank.getUuid());
            }
        }
        AccountEntity account = mapper.toAccount(expected);
        for (BankEntity bank : account.getBanks()) {
            bank.setAccount(account);
            for (BankAccountEntity bankAccount : bank.getBankAccounts()) {
                bankAccount.setBank(bank);
            }
        }
        Account actual = mapper.toAccount(account);
        assertThat(expected)
            .usingRecursiveComparison()
            .isEqualTo(actual);
    }


    @Test
    void testToBank() {
        Bank expected = factory.manufacturePojo(Bank.class);
        BankEntity actual = mapper.toBank(expected);
        actual.setAccount(factory.manufacturePojo(AccountEntity.class));
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields(
                "accountUuid",
                "bankAccounts.bankUuid")
            .isEqualTo(mapper.toBank(actual));
    }

    @Test
    void testToBankReverse() {
        BankEntity expected = factory.manufacturePojo(BankEntity.class);
        Bank actual = mapper.toBank(expected);
        expected.setAccount(factory.manufacturePojo(AccountEntity.class));
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields(
                "account",
                "bankAccounts.bank"
            )
            .isEqualTo(mapper.toBank(actual));
    }

    @Test
    void testToBankAccount() {
        BankAccount expected = factory.manufacturePojo(BankAccount.class)
            .uuid(UUID.randomUUID().toString());
        BankAccountEntity actual = mapper.toBankAccount(expected);
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields("bankUuid")
            .isEqualTo(mapper.toBankAccount(actual));
    }

    @Test
    void testToBankAccountReverse() {
        BankAccountEntity expected = factory.manufacturePojo(BankAccountEntity.class);
        BankAccount actual = mapper.toBankAccount(expected);
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields("bank")
            .isEqualTo(mapper.toBankAccount(actual));
    }
}
