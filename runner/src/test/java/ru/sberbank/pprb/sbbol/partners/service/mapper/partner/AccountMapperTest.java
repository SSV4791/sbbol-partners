package ru.sberbank.pprb.sbbol.partners.service.mapper.partner;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.BankType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapperImpl_;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.BankAccountMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.BankMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.BankMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.BankMapperImpl_;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.AccountWithPartnerResponse;
import ru.sberbank.pprb.sbbol.partners.service.partner.BudgetMaskService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(
    classes = {
        AccountMapperImpl.class,
        AccountMapperImpl_.class,
        BankMapperImpl.class,
        BankMapperImpl_.class,
        BankAccountMapperImpl.class
    }
)
class AccountMapperTest extends BaseUnitConfiguration {

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private BankMapper bankMapper;

    @Test
    void testToAccounts() {
        List<AccountEntity> expected = factory.manufacturePojo(ArrayList.class, AccountEntity.class);
        var actual = accountMapper.toAccounts(expected);
        assertThat(actual)
            .isNotNull();
        assertThat(expected)
            .hasSameSizeAs(actual);
        for (int i = 0; i < expected.size(); i++) {
            assertThat(expected.get(i).getDigitalId())
                .isEqualTo(actual.get(i).getDigitalId());
            assertThat(expected.get(i).getAccount())
                .isEqualTo(actual.get(i).getAccount());
            assertThat(actual.get(i).getBank())
                .isNotNull();
            assertThat(expected.get(i).getBank().getBic())
                .isEqualTo(actual.get(i).getBank().getBic());
            assertThat(expected.get(i).getBank().getName())
                .isEqualTo(actual.get(i).getBank().getName());
            assertThat(expected.get(i).getComment())
                .isEqualTo(actual.get(i).getComment());
            assertThat(expected.get(i).getPartnerUuid())
                .hasToString(actual.get(i).getPartnerId());
        }
    }

    @Test
    void testToAccountWithAccountCreateFullModel() {
        var expected = factory.manufacturePojo(AccountCreateFullModel.class);
        var digitalId = factory.manufacturePojo(String.class);
        var unifiedUuid = factory.manufacturePojo(UUID.class);
        var actual = accountMapper.toAccount(expected, digitalId, unifiedUuid);
        assertThat(actual)
            .isNotNull();
        assertThat(expected.getAccount())
            .isEqualTo(actual.getAccount());
        assertThat(actual.getBank())
            .isNotNull();
        assertThat(expected.getBank().getBic())
            .isEqualTo(actual.getBank().getBic());
        assertThat(expected.getBank().getName())
            .isEqualTo(actual.getBank().getName());
        assertThat(expected.getComment())
            .isEqualTo(actual.getComment());
        assertThat(digitalId)
            .isEqualTo(actual.getDigitalId());
        assertThat(unifiedUuid)
            .isEqualTo(actual.getPartnerUuid());
        assertThat(actual.getBank().getType())
            .isEqualTo(bankMapper.getBankType(expected.getBank().getMediary(), expected.getBank().getType()));
    }

    @Test
    void testToAccount() {
        var expected = factory.manufacturePojo(AccountEntity.class);
        var actual = accountMapper.toAccount(expected);
        assertThat(actual)
            .isNotNull();
        assertThat(expected.getPartnerUuid())
            .hasToString(actual.getPartnerId());
        assertThat(expected.getUuid())
            .hasToString(actual.getId());
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
        assertThat(actual.getBank())
            .isNotNull();
        assertThat(expected.getBank().getBic())
            .isEqualTo(actual.getBank().getBic());
        assertThat(expected.getBank().getBankAccount().getAccount())
            .isEqualTo(actual.getBank().getBankAccount().getBankAccount());
        assertThat(actual.getBank().getMediary())
            .isEqualTo(expected.getBank().getType() == BankType.AGENT);
    }

    @Test
    void testToAccountCreate() {
        var expected = factory.manufacturePojo(AccountCreate.class);
        var account = accountMapper.toAccount(expected);
        var bank = account.getBank();
        bank.setAccount(account);
        var bankAccount = bank.getBankAccount();
        bankAccount.setBank(bank);
        var actual = accountMapper.toAccount(account, Mockito.mock(BudgetMaskService.class));
        assertThat(expected)
            .usingRecursiveComparison()
            .ignoringFields(
                "budget",
                "bank.mediary"
            )
            .isEqualTo(actual);
    }

    @Test
    void testToAccountWithPartner() {
        AccountEntity expected = factory.manufacturePojo(AccountEntity.class);
        AccountWithPartnerResponse actual = accountMapper.toAccountWithPartner(expected);
        assertThat(actual.getId())
            .isEqualTo(expected.getPartner().getUuid().toString());
        assertThat(actual.getOrgName())
            .isEqualTo(expected.getPartner().getOrgName());
        assertThat(actual.getFirstName())
            .isEqualTo(expected.getPartner().getFirstName());
        assertThat(actual.getSecondName())
            .isEqualTo(expected.getPartner().getSecondName());
        assertThat(actual.getMiddleName())
            .isEqualTo(expected.getPartner().getMiddleName());
        assertThat(actual.getInn())
            .isEqualTo(expected.getPartner().getInn());
        assertThat(actual.getKpp())
            .isEqualTo(expected.getPartner().getKpp());
        assertThat(actual.getComment())
            .isEqualTo(expected.getPartner().getComment());
        assertThat(actual.getAccount().getPartnerId() )
            .hasToString(expected.getPartnerUuid().toString());
        assertThat(actual.getAccount().getId())
            .hasToString(expected.getUuid().toString());
        assertThat(actual.getAccount().getDigitalId())
            .isEqualTo(expected.getDigitalId() );
        assertThat(actual.getAccount().getAccount())
            .isEqualTo(expected.getAccount() );
        assertThat(actual.getAccount().getBank().getName())
            .isEqualTo(expected.getBank().getName() );
        assertThat(expected.getBank().getBic())
            .isEqualTo(actual.getAccount().getBank().getBic());
        assertThat(expected.getBank().getBankAccount().getAccount())
            .isEqualTo(actual.getAccount().getBank().getBankAccount().getBankAccount());
        assertThat(actual.getAccount().getBank().getMediary())
            .isEqualTo(expected.getBank().getType() == BankType.AGENT);
    }
}
