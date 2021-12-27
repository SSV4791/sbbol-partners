package ru.sberbank.pprb.sbbol.partners.rest.partner;

import org.junit.jupiter.api.Test;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountResponse;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Bank;
import ru.sberbank.pprb.sbbol.partners.model.BankAccount;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

class AccountControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/partner";

    @Test
    void testGetAccount() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getUuid());
        var actualAccount =
            get(
                baseRoutePath + "/accounts" + "/{digitalId}" + "/{id}",
                AccountResponse.class,
                account.getDigitalId(), account.getUuid()
            );

        assertThat(actualAccount)
            .isNotNull();
        assertThat(actualAccount.getAccount())
            .isNotNull()
            .isEqualTo(account);
    }

    @Test
    void testViewAccount() {
        var partner = createValidPartner("4444");
        createValidAccount(partner.getUuid(), partner.getDigitalId());
        createValidAccount(partner.getUuid(), partner.getDigitalId());
        createValidAccount(partner.getUuid(), partner.getDigitalId());
        createValidAccount(partner.getUuid(), partner.getDigitalId());

        var filter1 = new AccountsFilter()
            .digitalId(partner.getDigitalId())
            .partnerUuid(List.of(partner.getUuid()))
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var response1 = post(
            baseRoutePath + "/accounts/view",
            filter1,
            AccountsResponse.class
        );
        assertThat(response1)
            .isNotNull();
        assertThat(response1.getAccounts().size())
            .isEqualTo(4);
    }

    @Test
    void testCreateAccount() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getUuid());
        assertThat(account)
            .usingRecursiveComparison()
            .ignoringFields(
                "uuid",
                "banks.uuid",
                "banks.accountUuid",
                "banks.bankAccounts.uuid",
                "banks.bankAccounts.bankUuid")
            .isEqualTo(account);
    }


    @Test
    void testUpdateAccount() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getUuid());
        String newName = "Новое наименование";
        var updateAccount = new Account();
        updateAccount.uuid(account.getUuid());
        updateAccount.digitalId(account.getDigitalId());
        updateAccount.partnerUuid(account.getPartnerUuid());
        updateAccount.name(newName);
        var newUpdateAccount = put(baseRoutePath + "/account", updateAccount, AccountResponse.class);
        assertThat(newUpdateAccount)
            .isNotNull();
        assertThat(newUpdateAccount.getAccount().getName())
            .isEqualTo(newName);
        assertThat(newUpdateAccount.getErrors())
            .isNull();
    }

    @Test
    void testDeleteAccount() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getUuid());
        var actualAccount =
            get(
                baseRoutePath + "/accounts" + "/{digitalId}" + "/{id}",
                AccountResponse.class,
                account.getDigitalId(), account.getUuid()
            );
        assertThat(actualAccount)
            .isNotNull();
        assertThat(actualAccount.getAccount())
            .isNotNull()
            .isEqualTo(account);

        var deleteAccount =
            delete(
                baseRoutePath + "/accounts" + "/{digitalId}" + "/{id}",
                Error.class,
                actualAccount.getAccount().getDigitalId(), actualAccount.getAccount().getUuid()
            );
        assertThat(deleteAccount)
            .isNotNull();

        var searchAccount =
            get(
                baseRoutePath + "/accounts" + "/{digitalId}" + "/{id}",
                AccountResponse.class,
                account.getDigitalId(), account.getUuid()
            );
        assertThat(searchAccount)
            .isNotNull();
        assertThat(searchAccount.getAccount())
            .isNull();
    }

    private static Account getValidAccount(String partnerUuid) {
        return getValidAccount(partnerUuid, "111111");
    }

    private static Account getValidAccount(String partnerUuid, String digitalId) {
        return new Account()
            .uuid(UUID.randomUUID().toString())
            .version(0L)
            .partnerUuid(partnerUuid)
            .digitalId(digitalId)
            .name("111111")
            .account("222222")
            .banks(List.of(
                    new Bank()
                        .version(0L)
                        .bic("1111111")
                        .name("222222")
                        .addBankAccountsItem(
                            new BankAccount()
                                .uuid(UUID.randomUUID().toString())
                                .account("111111111111111"))
                )
            )
            .state(Account.StateEnum.NOT_SIGNED);
    }

    private static Account createValidAccount(String partnerUuid, String digitalId) {
        var createAccount = post(baseRoutePath + "/account", getValidAccount(partnerUuid, digitalId), AccountResponse.class);
        assertThat(createAccount)
            .isNotNull();
        assertThat(createAccount.getErrors())
            .isNull();
        return createAccount.getAccount();
    }

    private static Account createValidAccount(String partnerUuid) {
        var createAccount = post(baseRoutePath + "/account", getValidAccount(partnerUuid), AccountResponse.class);
        assertThat(createAccount)
            .isNotNull();
        assertThat(createAccount.getErrors())
            .isNull();
        return createAccount.getAccount();
    }
}
