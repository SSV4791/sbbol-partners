package ru.sberbank.pprb.sbbol.partners.service.mapper.partner;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankAccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.BankAccountMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.BankAccountMapperImpl;
import ru.sberbank.pprb.sbbol.partners.model.BankAccount;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(
    classes = {
        BankAccountMapperImpl.class
    }
)
class BankAccountMapperTest extends BaseUnitConfiguration {

    @Autowired
    private BankAccountMapper bankAccountMapper;

    @Test
    void testToBankAccount() {
        var expected = factory.manufacturePojo(BankAccount.class)
            .id(UUID.randomUUID());
        var actual = bankAccountMapper.toBankAccount(expected);
        var bank = new BankEntity();
        bank.setUuid(expected.getBankId());
        actual.setBank(bank);
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields("bankUuid")
            .isEqualTo(bankAccountMapper.toBankAccount(actual));
    }

    @Test
    void testToBankAccountReverse() {
        var expected = factory.manufacturePojo(BankAccountEntity.class);
        var actual = bankAccountMapper.toBankAccount(expected);
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields(
                "bank",
                "lastModifiedDate"
            )
            .isEqualTo(bankAccountMapper.toBankAccount(actual));
    }
}
