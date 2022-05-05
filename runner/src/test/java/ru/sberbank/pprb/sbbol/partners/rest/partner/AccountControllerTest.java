package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.qameta.allure.AllureId;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.rest.config.SbbolIntegrationWithOutSbbolConfiguration;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.AccountPriority;
import ru.sberbank.pprb.sbbol.partners.model.AccountResponse;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsResponse;
import ru.sberbank.pprb.sbbol.partners.model.BankAccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.BankCreate;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.SearchAccounts;

import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

@ContextConfiguration(classes = SbbolIntegrationWithOutSbbolConfiguration.class)
class AccountControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/partner";

    @Test
    @AllureId("34126")
    void testGetAccount() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        var actualAccount = get(
            baseRoutePath + "/account" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            AccountResponse.class,
            account.getDigitalId(), account.getId()
        );
        assertThat(actualAccount)
            .isNotNull();
        assertThat(actualAccount.getAccount().getComment())
            .isEqualTo("Это тестовый комментарий");
        assertThat(actualAccount.getAccount())
            .isNotNull()
            .isEqualTo(account);
    }

    @Test
    @AllureId("34154")
    void testViewAccount() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        createValidAccount(partner.getId(), partner.getDigitalId());
        createValidAccount(partner.getId(), partner.getDigitalId());
        createValidAccount(partner.getId(), partner.getDigitalId());
        createValidAccount(partner.getId(), partner.getDigitalId());
        createValidAccount(partner.getId(), partner.getDigitalId());

        var filter = new AccountsFilter()
            .digitalId(partner.getDigitalId())
            .partnerIds(List.of(partner.getId()))
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var response = post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            filter,
            AccountsResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getAccounts().size())
            .isEqualTo(4);
        assertThat(response.getPagination().getHasNextPage())
            .isEqualTo(Boolean.TRUE);
    }

    @Test
    @AllureId("34171")
    void testViewSearchAccount() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        createValidAccount(partner.getId(), partner.getDigitalId());
        createValidAccount(partner.getId(), partner.getDigitalId());
        createValidAccount(partner.getId(), partner.getDigitalId());

        var filter = new AccountsFilter()
            .digitalId(partner.getDigitalId())
            .partnerIds(List.of(partner.getId()))
            .search(new SearchAccounts().search(account.getAccount().substring(6)))
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var response = post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            filter,
            AccountsResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getAccounts().size())
            .isEqualTo(4);
    }

    @Test
    @AllureId("34176")
    void testViewBudgetAccount() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        createValidAccount(partner.getId(), partner.getDigitalId());
        createValidAccount(partner.getId(), partner.getDigitalId());
        createValidAccount(partner.getId(), partner.getDigitalId());
        createValidBudgetAccount(partner.getId(), partner.getDigitalId());

        var filter = new AccountsFilter()
            .digitalId(partner.getDigitalId())
            .partnerIds(List.of(partner.getId()))
            .isBudget(true)
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var response1 = post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            filter,
            AccountsResponse.class
        );
        assertThat(response1)
            .isNotNull();
        assertThat(response1.getAccounts().size())
            .isEqualTo(1);
    }

    @Test
    @AllureId("34182")
    void testCreateAccount() {
        var partner = createValidPartner();
        var expected = getValidAccount(partner.getId(), partner.getDigitalId());
        var account = createValidAccount(expected);
        assertThat(account)
            .usingRecursiveComparison()
            .ignoringFields(
                "uuid",
                "bank.uuid",
                "bank.accountUuid",
                "bank.bankAccount.uuid",
                "bank.bankAccount.bankUuid")
            .isEqualTo(account);
    }

    @Test
    @AllureId("34142")
    void testCreateNotValidAccount() {
        var partner = createValidPartner();
        var error = createNotValidAccount(partner.getId(), partner.getDigitalId());
        assertThat(error)
            .isNotNull();
        assertThat(error.getCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.name());
    }

    @Test
    @AllureId("34188")
    void testUpdateAccount() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        var updateAccount = put(
            baseRoutePath + "/account",
            HttpStatus.OK,
            updateAccount(account),
            AccountResponse.class
        );
        assertThat(updateAccount)
            .isNotNull();
        assertThat(updateAccount.getAccount().getComment())
            .isNotEqualTo(account.getComment());
        assertThat(updateAccount.getAccount().getComment())
            .isEqualTo(updateAccount.getAccount().getComment());
        assertThat(updateAccount.getErrors())
            .isNull();
    }

    @Test
    @AllureId("36930")
    void negativeTestUpdateAccountVersion() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        Long version = account.getVersion() + 1;
        account.setVersion(version);
        var accountError = put(
            baseRoutePath + "/account",
            HttpStatus.BAD_REQUEST,
            updateAccount(account),
            Error.class
        );
        assertThat(accountError.getCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.name());
        assertThat(accountError.getText())
            .contains("Версия записи в базе данных " + (account.getVersion() - 1) +
                " не равна версии записи в запросе version=" + version);
    }

    @Test
    @AllureId("36929")
    void positiveTestUpdateAccountVersion() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        var accountVersion = put(
            baseRoutePath + "/account",
            HttpStatus.OK,
            updateAccount(account),
            AccountResponse.class
        );
        var checkAccount = get(
            baseRoutePath + "/account" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            AccountResponse.class,
            accountVersion.getAccount().getDigitalId(), accountVersion.getAccount().getId());
        assertThat(checkAccount)
            .isNotNull();
        assertThat(checkAccount.getAccount().getVersion())
            .isEqualTo(account.getVersion() + 1);
        assertThat(checkAccount.getErrors())
            .isNull();
    }

    @Test
    @AllureId("34185")
    void testDeleteAccount() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        var actualAccount = get(
            baseRoutePath + "/account" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
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
                baseRoutePath + "/account" + "/{digitalId}" + "/{id}",
                HttpStatus.NO_CONTENT,
                actualAccount.getAccount().getDigitalId(), actualAccount.getAccount().getId()
            ).getBody();
        assertThat(deleteAccount)
            .isNotNull();

        var searchAccount = get(
            baseRoutePath + "/account" + "/{digitalId}" + "/{id}",
            HttpStatus.NOT_FOUND,
            Error.class,
            account.getDigitalId(), account.getId()
        );
        assertThat(searchAccount)
            .isNotNull();
        assertThat(searchAccount.getCode())
            .isEqualTo(HttpStatus.NOT_FOUND.name());
    }

    @Test
    @AllureId("34383")
    void testPriorityAccount() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        var foundAccount = get(
            baseRoutePath + "/account" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            AccountResponse.class,
            account.getDigitalId(), account.getId()
        );
        assertThat(foundAccount)
            .isNotNull();
        assertThat(foundAccount.getAccount())
            .isNotNull()
            .isEqualTo(account);
        assertThat(foundAccount.getAccount().getPriorityAccount())
            .isEqualTo(false);

        createValidPriorityAccount(account.getId(), account.getDigitalId());

        var actualAccount = get(
            baseRoutePath + "/account" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            AccountResponse.class,
            account.getDigitalId(), account.getId()
        );
        assertThat(actualAccount)
            .isNotNull();
        assertThat(actualAccount.getAccount().getPriorityAccount())
            .isEqualTo(true);
    }

    @Test
    @AllureId("34378")
    void testPriorityAccountException() {
        var partner = createValidPartner();
        var account1 = createValidAccount(partner.getId(), partner.getDigitalId());
        var account2 = createValidAccount(partner.getId(), partner.getDigitalId());
        createValidPriorityAccount(account1.getId(), account1.getDigitalId());
        var error = notCreatePriorityAccount(account2.getId(), account2.getDigitalId());
        assertThat(error)
            .isNotNull();
        assertThat(error.getCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.name());
    }

    static AccountCreate getValidAccount(String partnerUuid, String digitalId) {
        return new AccountCreate()
            .partnerId(partnerUuid)
            .digitalId(digitalId)
            .account("40802810500490014206")
            .comment("Это тестовый комментарий")
            .bank(new BankCreate()
                .bic("044525411")
                .name("222222")
                .bankAccount(
                    new BankAccountCreate()
                        .account("30101810145250000411"))
            );
    }

    private static AccountCreate getValidBudgetAccount(String partnerUuid, String digitalId) {
        var account = getValidAccount(partnerUuid, digitalId);
        account.setAccount("40601810300490014209");
        return account;
    }

    public static void createValidBudgetAccount(String partnerUuid, String digitalId) {
        var createAccount = post(
            baseRoutePath + "/account",
            HttpStatus.CREATED,
            getValidBudgetAccount(partnerUuid, digitalId),
            AccountResponse.class
        );
        assertThat(createAccount)
            .isNotNull();
        assertThat(createAccount.getErrors())
            .isNull();
    }

    public static Account createValidAccount(String partnerUuid, String digitalId) {
        var createAccount = post(
            baseRoutePath + "/account",
            HttpStatus.CREATED,
            getValidAccount(partnerUuid, digitalId),
            AccountResponse.class
        );
        assertThat(createAccount)
            .isNotNull();
        assertThat(createAccount.getErrors())
            .isNull();
        return createAccount.getAccount();
    }

    public static Account createValidAccount(AccountCreate account) {
        var createAccount = post(
            baseRoutePath + "/account",
            HttpStatus.CREATED,
            account,
            AccountResponse.class
        );
        assertThat(createAccount)
            .isNotNull();
        assertThat(createAccount.getErrors())
            .isNull();
        return createAccount.getAccount();
    }

    public static Error createNotValidAccount(String partnerUuid, String digitalId) {
        var account = getValidAccount(partnerUuid, digitalId);
        account.setAccount("222222");
        account.getBank().setBic("44444");
        return post(baseRoutePath + "/account", HttpStatus.BAD_REQUEST, account, Error.class);
    }

    private static AccountPriority getValidPriorityAccount(String accountId, String digitalId) {
        return new AccountPriority()
            .digitalId(digitalId)
            .id(accountId)
            .priorityAccount(true);
    }

    public static void createValidPriorityAccount(String accountId, String digitalId) {
        var createAccount = put(
            baseRoutePath + "/account/priority",
            HttpStatus.OK,
            getValidPriorityAccount(accountId, digitalId),
            AccountResponse.class);
        assertThat(createAccount)
            .isNotNull();
        assertThat(createAccount.getErrors())
            .isNull();
    }

    public static Error notCreatePriorityAccount(String accountId, String digitalId) {
        return put(
            baseRoutePath + "/account/priority",
            HttpStatus.BAD_REQUEST,
            getValidPriorityAccount(accountId, digitalId),
            Error.class
        );
    }

    public static AccountChange updateAccount(Account account) {
        return new AccountChange()
            .comment(randomAlphabetic(20))
            .version(account.getVersion())
            .digitalId(account.getDigitalId())
            .id(account.getId())
            .partnerId(account.getPartnerId())
            .account(account.getAccount())
            .bank(account.getBank()
                .bic(account.getBank().getBic())
                .name(account.getBank().getName())
                .bankAccount(account.getBank().getBankAccount()
                    .account(account.getBank().getBankAccount().getAccount())));
    }
}
