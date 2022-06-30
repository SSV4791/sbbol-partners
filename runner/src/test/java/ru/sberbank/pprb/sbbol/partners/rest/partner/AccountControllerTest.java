package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.qameta.allure.AllureId;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.AccountPriority;
import ru.sberbank.pprb.sbbol.partners.model.AccountResponse;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsResponse;
import ru.sberbank.pprb.sbbol.partners.model.BankAccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.BankCreate;
import ru.sberbank.pprb.sbbol.partners.model.Descriptions;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.SearchAccounts;
import ru.sberbank.pprb.sbbol.partners.rest.config.SbbolIntegrationWithOutSbbolConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountSignControllerTest.createValidAccountsSign;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

@ContextConfiguration(classes = SbbolIntegrationWithOutSbbolConfiguration.class)
class AccountControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/partner";

    public static final String ACCOUNT_FOR_TEST_PARTNER = "40802810500490014206";

    @Test
    void testViewFilter_whenGkuAttributeIsDefinedAndIsTrue() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        List<String> account = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            account.add(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
        }
        var accountsFilter = new AccountsFilter()
            .digitalId(partner.getDigitalId())
            .partnerIds(List.of(partner.getId()))
            .accountIds(account)
            .isHousingServicesProvider(true)
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var response = post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            accountsFilter,
            AccountsResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getAccounts())
            .isEmpty();
    }

    @Test
    void testViewFilter_whenGkuAttributeIsDefinedAndIsFalse() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        List<String> account = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            account.add(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
        }
        var accountsFilter = new AccountsFilter()
            .digitalId(partner.getDigitalId())
            .partnerIds(List.of(partner.getId()))
            .accountIds(account)
            .isHousingServicesProvider(false)
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var response = post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            accountsFilter,
            AccountsResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getAccounts())
            .hasSize(4);
        assertThat(response.getPagination().getHasNextPage())
            .isTrue();
    }

    @Test
    void testViewFilter_whenPartnerSearchAttributeIsDefinedAndContainsMatchedPartnerName() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        List<String> account = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            account.add(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
        }
        var accountsFilter = new AccountsFilter()
            .digitalId(partner.getDigitalId())
            .partnerIds(List.of(partner.getId()))
            .accountIds(account)
            .partnerSearch(partner.getOrgName())
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var response = post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            accountsFilter,
            AccountsResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getAccounts())
            .hasSize(4);
        assertThat(response.getPagination().getHasNextPage())
            .isTrue();
    }

    @Test
    void testViewFilter_whenPartnerSearchAttributeIsDefinedAndContainsMatchedInn() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        List<String> account = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            account.add(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
        }
        var accountsFilter = new AccountsFilter()
            .digitalId(partner.getDigitalId())
            .partnerIds(List.of(partner.getId()))
            .accountIds(account)
            .partnerSearch(partner.getInn())
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var response = post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            accountsFilter,
            AccountsResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getAccounts())
            .hasSize(4);
        assertThat(response.getPagination().getHasNextPage())
            .isTrue();
    }

    @Test
    void testViewFilter_whenPartnerSearchAttributeIsDefinedAndContainsNotMatchedPattern() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        List<String> account = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            account.add(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
        }
        var accountsFilter = new AccountsFilter()
            .digitalId(partner.getDigitalId())
            .partnerIds(List.of(partner.getId()))
            .accountIds(account)
            .partnerSearch("No matched pattern")
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var response = post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            accountsFilter,
            AccountsResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getAccounts())
            .isEmpty();
    }

    @Test
    void testViewFilter_whenPartnerSearchAttributeIsDefinedAndContainsMatchedAccount() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        List<String> accounts = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            accounts.add(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
        }
        var accountsFilter = new AccountsFilter()
            .digitalId(partner.getDigitalId())
            .partnerIds(List.of(partner.getId()))
            .accountIds(accounts)
            .partnerSearch(ACCOUNT_FOR_TEST_PARTNER)
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var response = post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            accountsFilter,
            AccountsResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getAccounts())
            .hasSize(4);
        assertThat(response.getPagination().getHasNextPage())
            .isTrue();
    }

    @Test
    void testViewFilterSignFiveAccount() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        List<String> account = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            account.add(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
        }
        createValidAccountsSign(partner.getDigitalId(), account);

        var filter1 = new AccountsFilter()
            .digitalId(partner.getDigitalId())
            .partnerIds(List.of(partner.getId()))
            .accountIds(account)
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var response = post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            filter1,
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
    void testViewFilterNotSignAccount() {
        var partner2 = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        List<String> account2 = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            account2.add(createValidAccount(partner2.getId(), partner2.getDigitalId()).getId());
        }
        createValidAccountsSign(partner2.getDigitalId(), account2);
        account2.add(createValidAccount(partner2.getId(), partner2.getDigitalId()).getId());
        var filter2 = new AccountsFilter()
            .digitalId(partner2.getDigitalId())
            .partnerIds(List.of(partner2.getId()))
            .accountIds(account2)
            .state(AccountsFilter.StateEnum.NOT_SIGNED)
            .pagination(new Pagination()
                .count(5)
                .offset(0));
        var response2 = post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            filter2,
            AccountsResponse.class
        );
        assertThat(response2)
            .isNotNull();
        assertThat(response2.getAccounts().size())
            .isEqualTo(1);
        assertThat(response2.getPagination().getHasNextPage())
            .isEqualTo(Boolean.FALSE);
    }

    @Test
    void testViewFilterSignFourAccount() {
        var partner3 = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        List<String> account3 = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            account3.add(createValidAccount(partner3.getId(), partner3.getDigitalId()).getId());
        }
        createValidAccountsSign(partner3.getDigitalId(), account3);
        account3.add(createValidAccount(partner3.getId(), partner3.getDigitalId()).getId());
        var filter3 = new AccountsFilter()
            .digitalId(partner3.getDigitalId())
            .partnerIds(List.of(partner3.getId()))
            .accountIds(account3)
            .state(AccountsFilter.StateEnum.SIGNED)
            .pagination(new Pagination()
                .count(5)
                .offset(0));
        var response3 = post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            filter3,
            AccountsResponse.class
        );
        assertThat(response3)
            .isNotNull();
        assertThat(response3.getAccounts().size())
            .isEqualTo(4);
        assertThat(response3.getPagination().getHasNextPage())
            .isEqualTo(Boolean.FALSE);
    }

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
    @AllureId("")
    void testNegativeViewAccount() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        createValidAccount(partner.getId(), partner.getDigitalId());
        createValidAccount(partner.getId(), partner.getDigitalId());
        createValidAccount(partner.getId(), partner.getDigitalId());
        createValidAccount(partner.getId(), partner.getDigitalId());
        createValidAccount(partner.getId(), partner.getDigitalId());

        var filter0 = new AccountsFilter()
            .digitalId(partner.getDigitalId())
            .partnerIds(List.of(partner.getId()))
            .pagination(new Pagination()
                .offset(0));
        var response0 = post(
            baseRoutePath + "/accounts/view",
            HttpStatus.BAD_REQUEST,
            filter0,
            Error.class
        );

        var filter1 = new AccountsFilter()
            .digitalId(partner.getDigitalId())
            .partnerIds(List.of(partner.getId()))
            .pagination(new Pagination()
                .count(4));
        var response1 = post(
            baseRoutePath + "/accounts/view",
            HttpStatus.BAD_REQUEST,
            filter1,
            Error.class
        );

        var filter2 = new AccountsFilter()
            .digitalId(null)
            .partnerIds(List.of(partner.getId()));
        var response2 = post(
            baseRoutePath + "/accounts/view",
            HttpStatus.BAD_REQUEST,
            filter2,
            Error.class
        );
        assertThat(response1)
            .isNotNull();
        assertThat(response1.getCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.name());
        assertThat(response0)
            .isNotNull();
        assertThat(response0.getCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.name());
        assertThat(response2)
            .isNotNull();
        assertThat(response2.getCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.name());
    }

    @Test
    @AllureId("34154")
    void testViewAccount() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var acc1 = createValidAccount(partner.getId(), partner.getDigitalId());
        var acc2 = createValidAccount(partner.getId(), partner.getDigitalId());
        var acc3 = createValidAccount(partner.getId(), partner.getDigitalId());
        var acc4 = createValidAccount(partner.getId(), partner.getDigitalId());
        var acc5 = createValidAccount(partner.getId(), partner.getDigitalId());

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

        var filter1 = new AccountsFilter()
            .digitalId(partner.getDigitalId())
            .partnerIds(List.of(partner.getId()))
            .accountIds(List.of(acc1.getId(), acc3.getId()))
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var response1 = post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            filter1,
            AccountsResponse.class
        );
        assertThat(response1)
            .isNotNull();
        assertThat(response1.getAccounts().size())
            .isEqualTo(2);
        assertThat(response1.getPagination().getHasNextPage())
            .isEqualTo(Boolean.FALSE);
        assertThat(response1.getAccounts().stream().map(Account::getId)).contains(acc1.getId(), acc3.getId());

        var filter2 = new AccountsFilter()
            .digitalId(partner.getDigitalId())
            .accountIds(List.of(acc1.getId(), acc2.getId(), acc3.getId(), acc4.getId(), acc5.getId()))
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var response2 = post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            filter2,
            AccountsResponse.class
        );
        assertThat(response2)
            .isNotNull();
        assertThat(response2.getAccounts().size())
            .isEqualTo(4);
        assertThat(response2.getPagination().getHasNextPage())
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
            .isNotNull();
        assertThat(updateAccount.getErrors())
            .isNull();
    }

    @Test
    @AllureId("")
    void testNegativeUpdateAccount() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());

        var acc = updateAccount(account)
            .bank(account.getBank()
                .bic("")
                .bankAccount(account.getBank().getBankAccount()
                    .bankAccount("")));
        var updateAccount = put(
            baseRoutePath + "/account",
            HttpStatus.BAD_REQUEST,
            acc,
            Error.class
        );
        assertThat(updateAccount)
            .isNotNull();
        assertThat(updateAccount.getCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.name());

        var acc1 = updateAccount(account)
            .bank(account.getBank()
                .bankAccount(account.getBank().getBankAccount()
                    .bankAccount("")));
        var updateAccount1 = put(
            baseRoutePath + "/account",
            HttpStatus.BAD_REQUEST,
            acc1,
            Error.class
        );
        assertThat(updateAccount1.getCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.name());

        var acc2 = updateAccount(account)
            .account("12345678901234567890");
        var updateAccount2 = put(
            baseRoutePath + "/account",
            HttpStatus.BAD_REQUEST,
            acc2,
            Error.class
        );
        assertThat(updateAccount2.getCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.name());

        var acc3 = updateAccount(account)
            .bank(account.getBank()
                .bic("044525002"));
        var updateAccount3 = put(
            baseRoutePath + "/account",
            HttpStatus.BAD_REQUEST,
            acc3,
            Error.class
        );
        assertThat(updateAccount3.getCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.name());

        var acc4 = updateAccount(account)
            .bank(account.getBank()
                .bic(""));
        var updateAccount4 = put(
            baseRoutePath + "/account",
            HttpStatus.BAD_REQUEST,
            acc4,
            Error.class
        );
        assertThat(updateAccount4.getCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.name());
    }

    @Test
    @AllureId("")
    void testPositiveUpdateAccount() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        var account2 = createValidAccount(partner.getId(), partner.getDigitalId());

        var acc = updateAccount(account);
        var updateAccount = put(
            baseRoutePath + "/account",
            HttpStatus.OK,
            acc,
            AccountResponse.class
        );
        assertThat(updateAccount)
            .isNotNull();
        assertThat(updateAccount.getErrors())
            .isNull();

        var acc1 = acc
            .version(acc.getVersion() + 1)
            .account("30101810145250000416")
            .bank(account.getBank()
                .bic("044525411")
                .bankAccount(account.getBank().getBankAccount()
                    .bankAccount("30101810145250000411")));
        var updateAccount1 = put(
            baseRoutePath + "/account",
            HttpStatus.OK,
            acc1,
            AccountResponse.class
        );
        assertThat(updateAccount1)
            .isNotNull();
        assertThat(updateAccount1.getErrors())
            .isNull();

        var acc2 = acc1
            .version(acc1.getVersion() + 1)
            .account("30101810145250000429");
        var updateAccount2 = put(
            baseRoutePath + "/account",
            HttpStatus.OK,
            acc2,
            AccountResponse.class
        );
        assertThat(updateAccount2)
            .isNotNull();
        assertThat(updateAccount2.getErrors())
            .isNull();

        var acc3 = acc2
            .version(acc2.getVersion() + 1)
            .account("")
            .bank(account.getBank()
                .bic("044525000")
                .bankAccount(account.getBank().getBankAccount()
                    .bankAccount(null)));
        var updateAccount3 = put(
            baseRoutePath + "/account",
            HttpStatus.OK,
            acc3,
            AccountResponse.class
        );
        assertThat(updateAccount3)
            .isNotNull();
        assertThat(updateAccount3.getErrors())
            .isNull();

        var acc4 = updateAccount(account2)
            .account("");
        var updateAccount4 = put(
            baseRoutePath + "/account",
            HttpStatus.OK,
            acc4,
            AccountResponse.class
        );
        assertThat(updateAccount4)
            .isNotNull();
        assertThat(updateAccount4.getErrors())
            .isNull();

        var acc5 = acc4
            .version(acc4.getVersion() + 1)
            .bank(acc4.getBank()
                .bic("044525000"));
        var updateAccount5 = put(
            baseRoutePath + "/account",
            HttpStatus.OK,
            acc5,
            AccountResponse.class
        );
        assertThat(updateAccount5)
            .isNotNull();
        assertThat(updateAccount5.getErrors())
            .isNull();

        var acc6 = acc5
            .version(acc5.getVersion())
            .account("40101810045250010041");
        var updateAccount6 = put(
            baseRoutePath + "/account",
            HttpStatus.OK,
            acc6,
            AccountResponse.class
        );
        assertThat(updateAccount6)
            .isNotNull();
        assertThat(updateAccount6.getErrors())
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
        assertThat(accountError.getDescriptionErrors().stream().map(Descriptions::getMessage).findAny().orElse(null))
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
    @AllureId("")
    void TestUpdateEKSAccount() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        updateAccount(account);
        account.setAccount("03214643000000017300");
        account.getBank().getBankAccount().setBankAccount("40102810545370000003");
        var accountVersion = put(
            baseRoutePath + "/account",
            HttpStatus.OK,
            account,
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

        updateAccount(account);
        account.setVersion(accountVersion.getAccount().getVersion() + 1);
        account.setAccount("40702810600000109222");
        account.getBank().getBankAccount().setBankAccount("40102810545370000003");
        var accountVersion1 = put(
            baseRoutePath + "/account",
            HttpStatus.BAD_REQUEST,
            account,
            Error.class
        );
        assertThat(accountVersion1)
            .isNotNull();
        assertThat(accountVersion1.getCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.name());

        updateAccount(account);
        account.setAccount("40702810600000009222");
        account.setVersion(accountVersion.getAccount().getVersion() + 1);
        account.getBank().setBic("048602001");
        account.getBank().getBankAccount().setBankAccount("40102810945370000073");
        var accountVersion2 = put(
            baseRoutePath + "/account",
            HttpStatus.BAD_REQUEST,
            account,
            Error.class
        );
        assertThat(accountVersion2)
            .isNotNull();
        assertThat(accountVersion2.getCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.name());

        updateAccount(account);
        account.setVersion(accountVersion.getAccount().getVersion() + 1);
        account.setAccount("40101810600000010006");
        account.getBank().setBic("048602001");
        account.getBank().getBankAccount().setBankAccount("40102810945370000073");
        var accountVersion3 = put(
            baseRoutePath + "/account",
            HttpStatus.OK,
            account,
            AccountResponse.class
        );
        var checkAccount3 = get(
            baseRoutePath + "/account" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            AccountResponse.class,
            accountVersion3.getAccount().getDigitalId(), accountVersion.getAccount().getId());
        assertThat(checkAccount3)
            .isNotNull();
        assertThat(checkAccount3.getAccount().getVersion())
            .isEqualTo(accountVersion3.getAccount().getVersion() + 1);
        assertThat(checkAccount3.getErrors())
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
            .account(ACCOUNT_FOR_TEST_PARTNER)
            .comment("Это тестовый комментарий")
            .bank(new BankCreate()
                .bic("044525411")
                .name(randomAlphabetic(10))
                .bankAccount(
                    new BankAccountCreate()
                        .bankAccount("30101810145250000411"))
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
        account.getBank().getBankAccount().setBankAccount("2131243255234324123123123");
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
            .account(ACCOUNT_FOR_TEST_PARTNER)
            .bank(account.getBank()
                .bic("044525411")
                .name(account.getBank().getName())
                .bankAccount(account.getBank().getBankAccount()
                    .bankAccount("30101810145250000411")));
    }
}
