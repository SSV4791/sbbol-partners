package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankAccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.config.BaseConfiguration;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.Bank;
import ru.sberbank.pprb.sbbol.partners.model.BankAccount;
import ru.sberbank.pprb.sbbol.partners.service.partner.BudgetMaskService;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountMapperTest extends BaseConfiguration {

    private static final AccountMapper mapper = Mappers.getMapper(AccountMapper.class);

    @Test
    void testToAccountCreate() {
        var expected = factory.manufacturePojo(AccountCreate.class);
        var account = mapper.toAccount(expected);
        for (var bank : account.getBanks()) {
            bank.setAccount(account);
            for (var bankAccount : bank.getBankAccounts()) {
                bankAccount.setBank(bank);
            }
        }
        var actual = mapper.toAccount(account, Mockito.mock(BudgetMaskService.class));
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields(
                "budget",
                "banks.mediary"
            )
            .isEqualTo(actual);
    }

    @Test
    void testToBank() {
        var expected = factory.manufacturePojo(Bank.class);
        var actual = mapper.toBank(expected);
        var account = new AccountEntity();
        account.setUuid(UUID.fromString(expected.getPartnerAccountId()));
        actual.setAccount(account);
        for (var bankAccount : actual.getBankAccounts()) {
            bankAccount.setBank(actual);
        }
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields(
                "partnerAccountId",
                "bankAccounts.bankId")
            .isEqualTo(mapper.toBank(actual));
    }

    @Test
    void testToBankReverse() {
        var expected = factory.manufacturePojo(BankEntity.class);
        var actual = mapper.toBank(expected);
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
        var expected = factory.manufacturePojo(BankAccount.class)
            .id(UUID.randomUUID().toString());
        var actual = mapper.toBankAccount(expected);
        var bank = new BankEntity();
        bank.setUuid(UUID.fromString(expected.getBankId()));
        actual.setBank(bank);
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields("bankUuid")
            .isEqualTo(mapper.toBankAccount(actual));
    }

    @Test
    void testToBankAccountReverse() {
        var expected = factory.manufacturePojo(BankAccountEntity.class);
        var actual = mapper.toBankAccount(expected);
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields("bank")
            .isEqualTo(mapper.toBankAccount(actual));
    }
}
