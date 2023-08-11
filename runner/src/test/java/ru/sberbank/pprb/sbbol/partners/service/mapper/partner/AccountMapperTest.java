package ru.sberbank.pprb.sbbol.partners.service.mapper.partner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankAccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AccountStateType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapperImpl_;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.BankAccountMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.BankMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.BankMapperImpl_;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerMapper;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.AccountWithPartnerResponse;
import ru.sberbank.pprb.sbbol.partners.model.Bank;
import ru.sberbank.pprb.sbbol.partners.model.BankAccount;
import ru.sberbank.pprb.sbbol.partners.model.BankAccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.BankCreate;
import ru.sberbank.pprb.sbbol.partners.model.SignType;
import ru.sberbank.pprb.sbbol.partners.service.partner.BudgetMaskService;
import ru.sberbank.pprb.sbbol.partners.storage.GkuInnCacheableStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

    @MockBean
    private GkuInnCacheableStorage gkuInnCacheableStorage;

    @MockBean
    private BudgetMaskService budgetMaskService;

    @BeforeEach
    void initEach() {
        when(gkuInnCacheableStorage.isGkuInn(any())).thenReturn(false);
    }

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
                .isEqualTo(actual.get(i).getPartnerId());
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
    }

    @Test
    void testToAccount() {
        var expected = factory.manufacturePojo(AccountEntity.class);
        var actual = accountMapper.toAccount(expected);
        assertThat(actual)
            .isNotNull();
        assertThat(expected.getPartnerUuid())
            .isEqualTo(actual.getPartnerId());
        assertThat(expected.getUuid())
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
        assertThat(actual.getBank())
            .isNotNull();
        assertThat(expected.getBank().getBic())
            .isEqualTo(actual.getBank().getBic());
        assertThat(expected.getBank().getBankAccount().getAccount())
            .isEqualTo(actual.getBank().getBankAccount().getBankAccount());
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
                "externalId"
            )
            .isEqualTo(actual);
    }

    @Test
    void testToAccountWithPartner() {
        var accountEntity = factory.manufacturePojo(AccountEntity.class);
        var expectedAccountWithPartnerResponse = getExpectedAccountWithPartnerResponse(accountEntity);
        var actualAccountWithPartnerResponse = accountMapper.toAccountWithPartner(accountEntity);
        assertThat(actualAccountWithPartnerResponse)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expectedAccountWithPartnerResponse);
    }

    @Test
    void testToAccountsWithPartner() {
        var accountEntity1 = factory.manufacturePojo(AccountEntity.class);
        var accountEntity2 = factory.manufacturePojo(AccountEntity.class);
        var accountEntity3 = factory.manufacturePojo(AccountEntity.class);
        var accountEntities = List.of(accountEntity1, accountEntity2, accountEntity3);
        var expectedAccountWithPartnerResponse = accountEntities.stream()
            .map(this::getExpectedAccountWithPartnerResponse)
            .collect(Collectors.toList());
        var actualAccountWithPartnerResponse = accountMapper.toAccountsWithPartner(accountEntities);
        assertThat(actualAccountWithPartnerResponse)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expectedAccountWithPartnerResponse);
    }

    private AccountWithPartnerResponse getExpectedAccountWithPartnerResponse(AccountEntity accountEntity) {
        return new AccountWithPartnerResponse()
            .id(accountEntity.getPartner().getUuid())
            .digitalId(accountEntity.getPartner().getDigitalId())
            .version(accountEntity.getPartner().getVersion())
            .legalForm(PartnerMapper.toLegalType(accountEntity.getPartner().getLegalType()))
            .inn(accountEntity.getPartner().getInn())
            .kpp(accountEntity.getPartner().getKpp())
            .firstName(accountEntity.getPartner().getFirstName())
            .middleName(accountEntity.getPartner().getMiddleName())
            .secondName(accountEntity.getPartner().getSecondName())
            .orgName(accountEntity.getPartner().getOrgName())
            .comment(accountEntity.getPartner().getComment())
            .gku(false)
            .account(
                new Account()
                    .id(accountEntity.getUuid())
                    .digitalId(accountEntity.getDigitalId())
                    .version(accountEntity.getVersion())
                    .partnerId(accountEntity.getPartnerUuid())
                    .account(accountEntity.getAccount())
                    .priorityAccount(accountEntity.getPriorityAccount())
                    .state(mapSignType(accountEntity.getState()))
                    .comment(accountEntity.getComment())
                    .bank(
                        new Bank()
                            .id(accountEntity.getBank().getUuid())
                            .version(accountEntity.getBank().getVersion())
                            .accountId(accountEntity.getBank().getAccount().getUuid())
                            .name(accountEntity.getBank().getName())
                            .bic(accountEntity.getBank().getBic())
                            .bankAccount(
                                new BankAccount()
                                    .id(accountEntity.getBank().getBankAccount().getUuid())
                                    .version(accountEntity.getBank().getBankAccount().getVersion())
                                    .bankId(accountEntity.getBank().getBankAccount().getBank().getUuid())
                                    .bankAccount(accountEntity.getBank().getBankAccount().getAccount())
                            )
                    )
            );
    }

    private SignType mapSignType(AccountStateType accountStateType) {
        return switch (accountStateType) {
            case SIGNED -> SignType.SIGNED;
            case NOT_SIGNED -> SignType.NOT_SIGNED;
        };
    }

    @Test
    void mapAccountChangeFullModelToAccountChange() {
        var accountChangeFullModel = factory.manufacturePojo(AccountChangeFullModel.class);
        var digitalId = factory.manufacturePojo(String.class);
        var partnerId = UUID.randomUUID();
        var actualAccountChange = accountMapper.toAccount(accountChangeFullModel, digitalId, partnerId);
        var expectedAccountChange = new AccountChange()
            .id(accountChangeFullModel.getId())
            .version(accountChangeFullModel.getVersion())
            .digitalId(digitalId)
            .partnerId(partnerId)
            .account(accountChangeFullModel.getAccount())
            .comment(accountChangeFullModel.getComment());
        Optional.ofNullable(accountChangeFullModel.getBank())
            .ifPresent(bank ->
                expectedAccountChange.setBank(new Bank()
                    .id(bank.getId())
                    .version(bank.getVersion())
                    .accountId(accountChangeFullModel.getId())
                    .bic(bank.getBic())
                    .name(bank.getName())
                    .bankAccount(
                        Optional.ofNullable(bank.getBankAccount())
                            .map(bankAccount -> new BankAccount()
                                .id(bankAccount.getId())
                                .version(bankAccount.getVersion())
                                .bankId(bank.getId())
                                .bankAccount(bankAccount.getBankAccount())
                            )
                            .orElse(null)
                    )
                ));
        assertThat(actualAccountChange)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expectedAccountChange);
    }

    @Test
    void mapAccountChangeFullModelToAccountCreate() {
        var accountChangeFullModel = factory.manufacturePojo(AccountChangeFullModel.class);
        var digitalId = factory.manufacturePojo(String.class);
        var partnerId = UUID.randomUUID();
        var actualAccountCreate = accountMapper.toAccountCreate(accountChangeFullModel, digitalId, partnerId);
        var expectedAccountCreate = new AccountCreate()
            .digitalId(digitalId)
            .partnerId(partnerId)
            .account(accountChangeFullModel.getAccount())
            .comment(accountChangeFullModel.getComment());
        Optional.ofNullable(accountChangeFullModel.getBank())
            .ifPresent(bank ->
                expectedAccountCreate.setBank(new BankCreate()
                    .bic(bank.getBic())
                    .name(bank.getName())
                    .bankAccount(
                        Optional.ofNullable(bank.getBankAccount())
                            .map(bankAccount -> new BankAccountCreate()
                                .bankAccount(bankAccount.getBankAccount())
                            )
                            .orElse(null)
                    )
                ));
        assertThat(actualAccountCreate)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(expectedAccountCreate);
    }

    @Test
    void updateAccount() {
        var accountChange = factory.manufacturePojo(AccountChange.class);
        var actualAccountEntity = factory.manufacturePojo(AccountEntity.class);
        accountMapper.updateAccount(accountChange, actualAccountEntity);
        var expectedAccountEntity = new AccountEntity();
        expectedAccountEntity.setUuid(accountChange.getId());
        expectedAccountEntity.setPartnerUuid(accountChange.getPartnerId());
        expectedAccountEntity.setAccount(accountChange.getAccount());
        expectedAccountEntity.setComment(accountChange.getComment());
        expectedAccountEntity.setDigitalId(accountChange.getDigitalId());
        expectedAccountEntity.setVersion(accountChange.getVersion());
        Optional.ofNullable(accountChange.getBank())
            .ifPresent(bank -> {
                var bankEntity = new BankEntity();
                bankEntity.setUuid(bank.getId());
                bankEntity.setVersion(bank.getVersion());
                bankEntity.setBic(bank.getBic());
                bankEntity.setName(bank.getName());
                bankEntity.setAccount(expectedAccountEntity);
                Optional.ofNullable(bank.getBankAccount())
                    .ifPresent(bankAccount -> {
                        var bankAccountEntity = new BankAccountEntity();
                        bankAccountEntity.setUuid(bankAccount.getId());
                        bankAccountEntity.setVersion(bankAccount.getVersion());
                        bankAccountEntity.setAccount(bankAccount.getBankAccount());
                        bankAccountEntity.setBank(bankEntity);
                        bankEntity.setBankAccount(bankAccountEntity);
                    });
                expectedAccountEntity.setBank(bankEntity);
            });
        assertThat(actualAccountEntity)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .ignoringFields(
                "uuid",
                "createDate",
                "lastModifiedDate",
                "priorityAccount",
                "search",
                "state",
                "partner",
                "idLinks",
                "bank.uuid",
                "bank.lastModifiedDate",
                "bank.intermediary",
                "bank.account.uuid",
                "bank.account.partner",
                "bank.account.createDate",
                "bank.account.lastModifiedDate",
                "bank.account.search",
                "bank.account.priorityAccount",
                "bank.account.state",
                "bank.account.idLinks",
                "bank.bankAccount.uuid",
                "bank.bankAccount.lastModifiedDate"

            )
            .isEqualTo(expectedAccountEntity);
    }

    @Test
    void patchAccount() {
        var accountChange = new AccountChange()
            .comment("Новый комментарий")
            .version(1L);
        var actualAccountEntity = factory.manufacturePojo(AccountEntity.class);
        accountMapper.patchAccount(accountChange, actualAccountEntity);
        var expectedAccountEntity = new AccountEntity();
        expectedAccountEntity.setUuid(actualAccountEntity.getUuid());
        expectedAccountEntity.setPartnerUuid(actualAccountEntity.getPartnerUuid());
        expectedAccountEntity.setPartner(actualAccountEntity.getPartner());
        expectedAccountEntity.setAccount(actualAccountEntity.getAccount());
        expectedAccountEntity.setComment(accountChange.getComment());
        expectedAccountEntity.setDigitalId(actualAccountEntity.getDigitalId());
        expectedAccountEntity.setVersion(accountChange.getVersion());
        expectedAccountEntity.setBank(actualAccountEntity.getBank());
        assertThat(actualAccountEntity)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .ignoringFields(
                "uuid",
                "createDate",
                "lastModifiedDate",
                "priorityAccount",
                "search",
                "state",
                "partner",
                "bank.uuid",
                "bank.lastModifiedDate",
                "bank.account.uuid",
                "bank.account.partner",
                "bank.account.createDate",
                "bank.account.lastModifiedDate",
                "bank.account.search",
                "bank.account.priorityAccount",
                "bank.account.state",
                "bank.bankAccount.uuid",
                "bank.bankAccount.lastModifiedDate",
                "idLinks"
            )
            .isEqualTo(expectedAccountEntity);
    }
}
