package ru.sberbank.pprb.sbbol.partners.service.mapper.partner;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankAccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapper;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.Bank;
import ru.sberbank.pprb.sbbol.partners.model.BankAccount;
import ru.sberbank.pprb.sbbol.partners.service.partner.BudgetMaskService;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AccountMapperTest extends BaseUnitConfiguration {

    private static final AccountMapper mapper = Mappers.getMapper(AccountMapper.class);

    @Test
    void testToAccount() {
        var expected = factory.manufacturePojo(AccountEntity.class);
        var actual = mapper.toAccount(expected);
        assertThat(expected.getPartnerUuid().toString())
            .isEqualTo(actual.getPartnerId());
        assertThat(expected.getUuid().toString())
            .isEqualTo(actual.getId());
        assertThat(expected.getDigitalId())
            .isEqualTo(actual.getDigitalId());
        assertThat(expected.getAccount())
            .isEqualTo(actual.getAccount());
        assertThat(expected.getPriorityAccount())
            .isEqualTo(actual.getPriorityAccount());
        assertThat(expected.getVersion())
            .isEqualTo(actual.getVersion());
        assertThat(expected.getComment())
            .isEqualTo(actual.getComment());
        assertThat(expected.getBank().getName())
            .isEqualTo(actual.getBank().getName());
        assertThat(expected.getBank().getBic())
            .isEqualTo(actual.getBank().getBic());
        assertThat(expected.getBank().getIntermediary())
            .isEqualTo(actual.getBank().getMediary());
        assertThat(expected.getBank().getBankAccount().getAccount())
            .isEqualTo(actual.getBank().getBankAccount().getBankAccount());
    }

    @Test
    void testToAccountCreate() {
        var expected = factory.manufacturePojo(AccountCreate.class);
        var account = mapper.toAccount(expected);
        var bank = account.getBank();
        bank.setAccount(account);
        var bankAccount = bank.getBankAccount();
        bankAccount.setBank(bank);
        var actual = mapper.toAccount(account, Mockito.mock(BudgetMaskService.class));
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields(
                "budget",
                "bank.mediary"
            )
            .isEqualTo(actual);
    }

    @Test
    void testToBank() {
        var expected = factory.manufacturePojo(Bank.class);
        var actual = mapper.toBank(expected);
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
                "lastModifiedDate",
                "bankAccount.bank",
                "bankAccount.lastModifiedDate"
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
            .ignoringFields(
                "bank",
                "lastModifiedDate"
            )
            .isEqualTo(mapper.toBankAccount(actual));
    }
}
