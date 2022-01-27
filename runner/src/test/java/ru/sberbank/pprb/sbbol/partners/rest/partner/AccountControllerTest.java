package ru.sberbank.pprb.sbbol.partners.rest.partner;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationWithOutSbbolTest;
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

class AccountControllerTest extends AbstractIntegrationWithOutSbbolTest {

    public static final String baseRoutePath = "/partner";

    @Test
    void testGetAccount() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        var actualAccount =
            get(
                baseRoutePath + "/accounts" + "/{digitalId}" + "/{id}",
                AccountResponse.class,
                account.getDigitalId(), account.getId()
            );
        assertThat(actualAccount)
            .isNotNull();
        assertThat(actualAccount.getAccount())
            .isNotNull()
            .isEqualTo(account);
    }

    @Test
    void testViewAccount() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        createValidAccount(partner.getId(), partner.getDigitalId());
        createValidAccount(partner.getId(), partner.getDigitalId());
        createValidAccount(partner.getId(), partner.getDigitalId());
        createValidAccount(partner.getId(), partner.getDigitalId());

        var filter1 = new AccountsFilter()
            .digitalId(partner.getDigitalId())
            .partnerIds(List.of(partner.getId()))
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
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
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
    void testCreateNotValidAccount() {
        var partner = createValidPartner();
        var error = createNotValidAccount(partner.getId(), partner.getDigitalId());
        assertThat(error)
            .isNotNull();
        assertThat(error.getCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.name());
    }

    @Test
    void testUpdateAccount() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        String newName = "Новое наименование";
        account.setName(newName);
        var newUpdateAccount = put(baseRoutePath + "/account", account, AccountResponse.class);
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
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        var actualAccount =
            get(
                baseRoutePath + "/accounts" + "/{digitalId}" + "/{id}",
                AccountResponse.class,
                account.getDigitalId(), account.getId()
            );
        assertThat(actualAccount)
            .isNotNull();
        assertThat(actualAccount.getAccount())
            .isNotNull()
            .isEqualTo(account);

        var deleteAccount =
            delete(
                baseRoutePath + "/accounts" + "/{digitalId}" + "/{id}",
                actualAccount.getAccount().getDigitalId(), actualAccount.getAccount().getId()
            );
        assertThat(deleteAccount)
            .isNotNull();

        var searchAccount =
            getNotFound(
                baseRoutePath + "/accounts" + "/{digitalId}" + "/{id}",
                Error.class,
                account.getDigitalId(), account.getId()
            );
        assertThat(searchAccount)
            .isNotNull();
        assertThat(searchAccount.getCode())
            .isEqualTo(HttpStatus.NOT_FOUND.name());
    }

    private static Account getValidAccount(String partnerUuid, String digitalId) {
        return new Account()
            .id(UUID.randomUUID().toString())
            .version(0L)
            .partnerId(partnerUuid)
            .digitalId(digitalId)
            .name("111111")
            .account("40802810500490014206")
            .banks(List.of(
                    new Bank()
                        .version(0L)
                        .bic("044525411")
                        .name("222222")
                        .addBankAccountsItem(
                            new BankAccount()
                                .id(UUID.randomUUID().toString())
                                .account("30101810145250000411"))
                )
            )
            .state(Account.StateEnum.NOT_SIGNED);
    }

    public static Account createValidAccount(String partnerUuid, String digitalId) {
        var createAccount = createPost(baseRoutePath + "/account", getValidAccount(partnerUuid, digitalId), AccountResponse.class);
        assertThat(createAccount)
            .isNotNull();
        assertThat(createAccount.getErrors())
            .isNull();
        return createAccount.getAccount();
    }

    private static Error createNotValidAccount(String partnerUuid, String digitalId) {
        var account = getValidAccount(partnerUuid, digitalId);
        account.setAccount("222222");
        for (Bank bank : account.getBanks()) {
            bank.setBic("44444");
        }
        return createBadRequestPost(baseRoutePath + "/account", account, Error.class);
    }
}
