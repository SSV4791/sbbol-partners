package ru.sberbank.pprb.sbbol.partners.service.mapper.partner;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.BankAccountMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.BankMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.BankMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.BankMapperImpl_;
import ru.sberbank.pprb.sbbol.partners.model.Bank;

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
                "bankAccount.bankId",
                "mediary"
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
}
