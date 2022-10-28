package ru.sberbank.pprb.sbbol.partners.rest.partner;

import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Descriptions;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.SearchAccounts;
import ru.sberbank.pprb.sbbol.partners.model.SignType;
import ru.sberbank.pprb.sbbol.partners.rest.config.SbbolIntegrationWithOutSbbolConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.MODEL_DUPLICATE_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.MODEL_NOT_FOUND_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.MODEL_VALIDATION_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.OPTIMISTIC_LOCK_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.PRIORITY_ACCOUNT_MORE_ONE;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountSignControllerTest.createValidAccountsSign;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

@ContextConfiguration(classes = SbbolIntegrationWithOutSbbolConfiguration.class)
class AccountControllerTest extends BaseAccountControllerTest {

    private static final String KPP_WITHOUT_ACCOUNT = "618243879";

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
    void testNegativeViewFilter_whenPaginationIsNull() {
        Descriptions descriptions = new Descriptions()
            .field("pagination")
            .message(
                List.of(MessagesTranslator.toLocale("javax.validation.constraints.NotNull.message"))
            );
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        List<String> account = List.of(createValidAccount(partner.getId(), partner.getDigitalId()).getId());

        var accountsFilter = new AccountsFilter()
            .digitalId(partner.getDigitalId())
            .partnerIds(List.of(partner.getId()))
            .accountIds(account)
            .isHousingServicesProvider(false)
            .pagination(null);
        var response = post(
            baseRoutePath + "/accounts/view",
            HttpStatus.BAD_REQUEST,
            accountsFilter,
            Error.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
        assertThat(response.getDescriptions())
            .contains(descriptions);
    }

    @Test
    void testNegativeViewFilter_whenPaginationCountAndOffsetIsNull() {
        List<Descriptions> errorTexts = List.of(
            new Descriptions()
                .field("pagination.count")
                .message(
                    List.of("Поле обязательно для заполнения")
                ),
            new Descriptions()
                .field("pagination.offset")
                .message(
                    List.of("Поле обязательно для заполнения")
                )
        );
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        List<String> account = List.of(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
        var accountsFilter = new AccountsFilter()
            .digitalId(partner.getDigitalId())
            .partnerIds(List.of(partner.getId()))
            .accountIds(account)
            .isHousingServicesProvider(false)
            .pagination(new Pagination()
                .count(null)
                .offset(null));
        var response = post(
            baseRoutePath + "/accounts/view",
            HttpStatus.BAD_REQUEST,
            accountsFilter,
            Error.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
        for (var text : response.getDescriptions()) {
            assertThat(errorTexts.contains(text)).isTrue();
        }
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
    void testViewFilter_whenPartnerSearchAttributeIsDefinedAndContainsDontMatchedPartnerName() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var partnerWithoutAccount = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        List<String> account = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            account.add(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
        }
        var accountsFilter = new AccountsFilter()
            .digitalId(partner.getDigitalId())
            .partnerIds(List.of(partner.getId()))
            .accountIds(account)
            .partnerSearch(partnerWithoutAccount.getOrgName())
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
        assertThat(response.getPagination().getHasNextPage())
            .isFalse();
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
    void testViewFilter_whenPartnerSearchAttributeIsDefinedAndDontContainsMatchedInn() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        List<String> account = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            account.add(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
        }
        var accountsFilter = new AccountsFilter()
            .digitalId(partner.getDigitalId())
            .partnerIds(List.of(partner.getId()))
            .accountIds(account)
            .partnerSearch(getValidInnNumber(LegalForm.LEGAL_ENTITY))
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
        assertThat(response.getPagination().getHasNextPage())
            .isFalse();
    }

    @Test
    void testViewFilter_whenPartnerSearchAttributeIsDefinedAndContainsMatchedKpp() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        List<String> account = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            account.add(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
        }
        var accountsFilter = new AccountsFilter()
            .digitalId(partner.getDigitalId())
            .partnerIds(List.of(partner.getId()))
            .accountIds(account)
            .partnerSearch(partner.getKpp())
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
    void testViewFilter_whenPartnerSearchAttributeIsDefinedAndDontContainsMatchedKpp() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        List<String> account = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            account.add(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
        }
        var accountsFilter = new AccountsFilter()
            .digitalId(partner.getDigitalId())
            .partnerIds(List.of(partner.getId()))
            .accountIds(account)
            .partnerSearch(KPP_WITHOUT_ACCOUNT)
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
        assertThat(response.getPagination().getHasNextPage())
            .isFalse();
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
        assertThat(response.getPagination().getHasNextPage())
            .isFalse();
    }

    @Test
    void testViewFilter_whenPartnerSearchAttributeIsDefinedAndContainsMatchedAccount() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        List<String> accounts = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            accounts.add(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
        }
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        accounts.add(account.getId());
        var accountsFilter = new AccountsFilter()
            .digitalId(partner.getDigitalId())
            .partnerIds(List.of(partner.getId()))
            .accountIds(accounts)
            .partnerSearch(account.getAccount())
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
            .hasSize(1);
        assertThat(response.getPagination().getHasNextPage())
            .isFalse();
    }

    @Test
    void testViewFilter_whenPartnerSearchAttributeIsDefinedAndContainsPartAccount() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        List<String> accounts = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            accounts.add(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
        }
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        accounts.add(account.getId());
        var accountsFilter = new AccountsFilter()
            .digitalId(partner.getDigitalId())
            .partnerIds(List.of(partner.getId()))
            .accountIds(accounts)
            .partnerSearch(account.getAccount().substring(6))
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
            .hasSize(1);
        assertThat(response.getPagination().getHasNextPage())
            .isFalse();
    }

    @Test
    void testViewFilter_whenPartnerSearchAttributeIsDefinedAndContainsIncorrectAccountNumber() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        List<String> accounts = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            accounts.add(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
        }
        var accountsFilter = new AccountsFilter()
            .digitalId(partner.getDigitalId())
            .partnerIds(List.of(partner.getId()))
            .accountIds(accounts)
            .partnerSearch(RandomStringUtils.randomNumeric(20))
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
            .hasSize(0);
        assertThat(response.getPagination().getHasNextPage())
            .isFalse();
    }

    @Test
    void testViewFilterSignFiveAccount() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        List<String> account = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            account.add(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
        }
        createValidAccountsSign(partner.getDigitalId(), account);
        account.add(createValidAccount(partner.getId(), partner.getDigitalId()).getId());

        var filter = new AccountsFilter()
            .digitalId(partner.getDigitalId())
            .partnerIds(List.of(partner.getId()))
            .accountIds(account)
            .state(SignType.SIGNED)
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
        assertThat(response.getAccounts())
            .hasSize(4);
        assertThat(response.getPagination().getHasNextPage())
            .isEqualTo(Boolean.TRUE);
        for (var accounts : response.getAccounts()) {
            assertThat(accounts.getState()).isEqualTo(SignType.SIGNED);
        }
    }

    @Test
    void testViewFilterNotSignAccount() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        List<String> account = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            account.add(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
        }
        createValidAccountsSign(partner.getDigitalId(), account);
        account.add(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
        var filter = new AccountsFilter()
            .digitalId(partner.getDigitalId())
            .partnerIds(List.of(partner.getId()))
            .accountIds(account)
            .state(SignType.NOT_SIGNED)
            .pagination(new Pagination()
                .count(5)
                .offset(0));
        var response = post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            filter,
            AccountsResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getAccounts())
            .hasSize(1);
        assertThat(response.getPagination().getHasNextPage())
            .isEqualTo(Boolean.FALSE);
        for (var accounts : response.getAccounts()) {
            assertThat(accounts.getState()).isEqualTo(SignType.NOT_SIGNED);
        }
    }

    @Test
    void testViewFilterSignFourAccount() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        List<String> account = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            account.add(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
        }
        createValidAccountsSign(partner.getDigitalId(), account);
        account.add(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
        var filter = new AccountsFilter()
            .digitalId(partner.getDigitalId())
            .partnerIds(List.of(partner.getId()))
            .accountIds(account)
            .state(SignType.SIGNED)
            .pagination(new Pagination()
                .count(5)
                .offset(0));
        var response = post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            filter,
            AccountsResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getAccounts())
            .hasSize(4);
        assertThat(response.getPagination().getHasNextPage())
            .isEqualTo(Boolean.FALSE);
        for (var accounts : response.getAccounts()) {
            assertThat(accounts.getState()).isEqualTo(SignType.SIGNED);
        }
    }

    @Test
    void testGetAccount() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        var actualAccount = get(
            baseRoutePath + "/accounts" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            Account.class,
            account.getDigitalId(), account.getId()
        );
        assertThat(actualAccount.getComment())
            .isEqualTo("Это тестовый комментарий");
        assertThat(actualAccount)
            .isNotNull()
            .isEqualTo(account);
    }

    @Test
    void testViewAccount() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        for (int i = 0; i < 5; i++) {
            createValidAccount(partner.getId(), partner.getDigitalId());
        }

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
        assertThat(response.getAccounts())
            .hasSize(4);
        assertThat(response.getPagination().getHasNextPage())
            .isEqualTo(Boolean.TRUE);
    }

    @Test
    void testViewAccountWithTwoAccountId() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var acc1 = createValidAccount(partner.getId(), partner.getDigitalId());
        var acc2 = createValidAccount(partner.getId(), partner.getDigitalId());
        for (int i = 0; i < 5; i++) {
            createValidAccount(partner.getId(), partner.getDigitalId());
        }

        var filter = new AccountsFilter()
            .digitalId(partner.getDigitalId())
            .partnerIds(List.of(partner.getId()))
            .accountIds(List.of(acc1.getId(), acc2.getId()))
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
        assertThat(response.getAccounts())
            .hasSize(2);
        assertThat(response.getPagination().getHasNextPage())
            .isEqualTo(Boolean.FALSE);
        assertThat(response.getAccounts().stream().map(Account::getId)).contains(acc1.getId(), acc2.getId());
    }

    @Test
    void testViewAccountWithAllAccountsId() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var account1 = createValidAccount(partner.getId(), partner.getDigitalId());
        var account2 = createValidAccount(partner.getId(), partner.getDigitalId());
        var account3 = createValidAccount(partner.getId(), partner.getDigitalId());
        var account4 = createValidAccount(partner.getId(), partner.getDigitalId());
        var account5 = createValidAccount(partner.getId(), partner.getDigitalId());

        var filter = new AccountsFilter()
            .digitalId(partner.getDigitalId())
            .accountIds(List.of(account1.getId(), account2.getId(), account3.getId(), account4.getId(), account5.getId()))
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
        assertThat(response.getAccounts())
            .hasSize(4);
        assertThat(response.getPagination().getHasNextPage())
            .isEqualTo(Boolean.TRUE);
    }

    @Test
    void testViewSearchOneAccount() {
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
                .count(1)
                .offset(0));
        var response = post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            filter,
            AccountsResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getAccounts())
            .hasSize(1);
    }

    @Test
    void testViewSearchAllAccounts() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        createValidAccount(partner.getId(), partner.getDigitalId());
        createValidAccount(partner.getId(), partner.getDigitalId());
        createValidAccount(partner.getId(), partner.getDigitalId());
        createValidAccount(partner.getId(), partner.getDigitalId());

        var filter = new AccountsFilter()
            .digitalId(partner.getDigitalId())
            .partnerIds(List.of(partner.getId()))
            .search(new SearchAccounts().search("40702810"))
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
        assertThat(response.getAccounts())
            .hasSize(4);
    }

    @Test
    void testViewSearchAccountWithFullNumber() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        createValidAccount(partner.getId(), partner.getDigitalId());
        createValidAccount(partner.getId(), partner.getDigitalId());
        createValidAccount(partner.getId(), partner.getDigitalId());

        var filter = new AccountsFilter()
            .digitalId(partner.getDigitalId())
            .partnerIds(List.of(partner.getId()))
            .search(new SearchAccounts().search(account.getAccount()))
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
        assertThat(response.getAccounts())
            .hasSize(1);
    }

    @Test
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
        assertThat(response1.getAccounts())
            .hasSize(1);
    }

    @Test
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
    void testCreateAccount_unique() {
        var partner = createValidPartner();
        var expected = getValidAccount(partner.getId(), partner.getDigitalId());
        createValidAccount(expected);
        var error = post(
            baseRoutePath + "/account",
            HttpStatus.BAD_REQUEST,
            expected,
            Error.class
        );
        assertThat(error)
            .isNotNull();
        assertThat(error.getCode())
            .isEqualTo(MODEL_DUPLICATE_EXCEPTION.getValue());
    }

    @Test
    void testCreateNotValidAccount() {
        var partner = createValidPartner();
        var error = createNotValidAccount(partner.getId(), partner.getDigitalId());
        assertThat(error)
            .isNotNull();
        assertThat(error.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
        var actualAccountDescriptions = error.getDescriptions().stream()
            .filter(descriptions -> "account".equals(descriptions.getField()))
            .findAny().orElse(null);
        assertThat(actualAccountDescriptions)
            .isNotNull();
        assertThat(actualAccountDescriptions.getMessage())
            .asList()
            .contains(MessagesTranslator.toLocale("validation.account.control_number"))
            .contains(MessagesTranslator.toLocale("validation.account.simple_pattern"));
        var actualBankAccountDescriptions = error.getDescriptions().stream()
            .filter(descriptions -> "bank.bankAccount.bankAccount".equals(descriptions.getField()))
            .findAny().orElse(null);
        assertThat(actualBankAccountDescriptions)
            .isNotNull();
        assertThat(actualBankAccountDescriptions.getMessage())
            .asList()
            .contains(MessagesTranslator.toLocale("account.account.bank_account.control_number"))
            .contains(MessagesTranslator.toLocale("validation.account.simple_pattern"));
    }

    @Test
    void testCreateWithEmptyAccountAndBankAccount() {
        var partner = createValidPartner();
        var  account = createAccountEntityWithEmptyAccountAndBankAccount(partner.getId(), partner.getDigitalId());
        assertThat(account)
            .isNotNull();
    }

    @Test
    void testCreateWithNullAccountAndBankAccount() {
        var partner = createValidPartner();
        var  account = createAccountEntityWithNullAccountAndBankAccount(partner.getId(), partner.getDigitalId());
        assertThat(account)
            .isNotNull();
    }

    @Test
    void testCreateAccount_whenBankIsNull_thenBadRequest() {
        var partner = createValidPartner();
        var  error = createAccountEntityWhenBankIsNull(partner.getId(), partner.getDigitalId());

        assertThat(error)
            .isNotNull();

        var actualBankDescriptions = error.getDescriptions().stream()
            .filter(descriptions -> "bank".equals(descriptions.getField()))
            .findAny().orElse(null);

        assertThat(actualBankDescriptions)
            .isNotNull();
        assertThat(actualBankDescriptions.getMessage())
            .asList()
            .contains(MessagesTranslator.toLocale("javax.validation.constraints.NotEmpty.message"));
    }

    @Test
    void testCreateAccount_whenBankNameIsNull_thenBadRequest() {
        var partner = createValidPartner();
        var  error = createAccountEntityWhenBankNameIsNull(partner.getId(), partner.getDigitalId());

        assertThat(error)
            .isNotNull();

        var actualBankDescriptions = error.getDescriptions().stream()
            .filter(descriptions -> "bank.name".equals(descriptions.getField()))
            .findAny().orElse(null);

        assertThat(actualBankDescriptions)
            .isNotNull();
        assertThat(actualBankDescriptions.getMessage())
            .asList()
            .contains(MessagesTranslator.toLocale("javax.validation.constraints.NotEmpty.message"));
    }

    @Test
    void testCreateAccount_whenBankNameIsEmpty_thenBadRequest() {
        var partner = createValidPartner();
        var  error = createAccountEntityWhenBankNameIsEmpty(partner.getId(), partner.getDigitalId());

        assertThat(error)
            .isNotNull();

        var actualBankDescriptions = error.getDescriptions().stream()
            .filter(descriptions -> "bank.name".equals(descriptions.getField()))
            .findAny().orElse(null);

        assertThat(actualBankDescriptions)
            .isNotNull();
        assertThat(actualBankDescriptions.getMessage())
            .asList()
            .contains(MessagesTranslator.toLocale("javax.validation.constraints.NotEmpty.message"));
    }

    @Test
    void testCreateAccount_whenBankBicIsNull_thenBadRequest() {
        var partner = createValidPartner();
        var  error = createAccountEntityWhenBankBicIsNull(partner.getId(), partner.getDigitalId());

        assertThat(error)
            .isNotNull();

        var actualBankDescriptions = error.getDescriptions().stream()
            .filter(descriptions -> "bank.bic".equals(descriptions.getField()))
            .findAny().orElse(null);

        assertThat(actualBankDescriptions)
            .isNotNull();
        assertThat(actualBankDescriptions.getMessage())
            .asList()
            .contains(MessagesTranslator.toLocale("javax.validation.constraints.NotEmpty.message"));
    }

    @Test
    void testCreateAccount_whenBankBicIsEmpty_thenBadRequest() {
        var partner = createValidPartner();
        var  error = createAccountEntityWhenBankBicIsEmpty(partner.getId(), partner.getDigitalId());

        assertThat(error)
            .isNotNull();

        var actualBankDescriptions = error.getDescriptions().stream()
            .filter(descriptions -> "bank.bic".equals(descriptions.getField()))
            .findAny().orElse(null);

        assertThat(actualBankDescriptions)
            .isNotNull();
        assertThat(actualBankDescriptions.getMessage())
            .asList()
            .contains(MessagesTranslator.toLocale("validation.account.bank.bic.length"));
    }

    @Test
    void testCreateUsdAccount() {
        var partner = createValidPartner();
        var expected = getValidAccount(partner.getId(), partner.getDigitalId());
        expected.setAccount("40817840100000000001");
        var error = createInvalidAccount(expected);
        assertThat(error)
            .isNotNull();
        assertThat(error.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
        assertThat(error.getDescriptions())
            .isNotEmpty();
        var messages = error.getDescriptions().stream()
            .map(Descriptions::getMessage)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
        assertThat(messages)
            .contains(MessagesTranslator.toLocale("validation.account.rub_code_currency"));
    }

    @Test
    void testCreateBudgetAccount_whenInvalidCodeCurrency() {
        var partner = createValidPartner();
        var expectedAccount = getValidBudgetAccount(partner.getId(), partner.getDigitalId());
        expectedAccount.setAccount("00817810100000000001");
        var error = createInvalidAccount(expectedAccount);
        assertThat(error)
            .isNotNull();
        assertThat(error.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
        assertThat(error.getDescriptions())
            .isNotEmpty();
        var messages = error.getDescriptions().stream()
            .map(Descriptions::getMessage)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
        assertThat(messages)
            .contains(MessagesTranslator.toLocale("validation.account.treasure_code_currency"));
    }

    @Test
    void testCreateBudgetAccount_whenInvalidBalance() {
        var partner = createValidPartner();
        var expectedAccount = getValidBudgetAccount(partner.getId(), partner.getDigitalId());
        expectedAccount.setAccount("10817643100000000001");
        var error = createInvalidAccount(expectedAccount);
        assertThat(error)
            .isNotNull();
        assertThat(error.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
        assertThat(error.getDescriptions())
            .isNotEmpty();
        var messages = error.getDescriptions().stream()
            .map(Descriptions::getMessage)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
        assertThat(messages)
            .contains(MessagesTranslator.toLocale("validation.account.treasure_balance"));
    }

    @Test
    void testCreateValidBudgetAccount() {
        var partner = createValidPartner();
        var expectedAccount = getValidBudgetAccount(partner.getId(), partner.getDigitalId());
        var actualAccount = createValidAccount(expectedAccount);
        assertThat(actualAccount)
            .isNotNull();
    }

    @Test
    void testUpdateAccount() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        var updateAccount = put(
            baseRoutePath + "/account",
            HttpStatus.OK,
            updateAccount(account),
            Account.class
        );
        assertThat(updateAccount)
            .isNotNull();
        assertThat(updateAccount.getComment())
            .isNotEqualTo(account.getComment());
        assertThat(updateAccount.getComment())
            .isNotNull();
    }

    @Test
    void testUpdateAccount_whenBankIsNull_thenBadRequest() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());

        var error = put(
            baseRoutePath + "/account",
            HttpStatus.BAD_REQUEST,
            updateAccountEntityWhenBankIsNull(account),
            Error.class
        );

        assertThat(error)
            .isNotNull();

        var actualBankDescriptions = error.getDescriptions().stream()
            .filter(descriptions -> "bank".equals(descriptions.getField()))
            .findAny().orElse(null);

        assertThat(actualBankDescriptions)
            .isNotNull();
        assertThat(actualBankDescriptions.getMessage())
            .asList()
            .contains(MessagesTranslator.toLocale("javax.validation.constraints.NotEmpty.message"));
    }

    @Test
    void testUpdateAccount_whenBankNameIsNull_thenBadRequest() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());

        var error = put(
            baseRoutePath + "/account",
            HttpStatus.BAD_REQUEST,
            updateAccountEntityWhenBankNameIsNull(account),
            Error.class
        );

        assertThat(error)
            .isNotNull();

        var actualBankDescriptions = error.getDescriptions().stream()
            .filter(descriptions -> "bank.name".equals(descriptions.getField()))
            .findAny().orElse(null);

        assertThat(actualBankDescriptions)
            .isNotNull();
        assertThat(actualBankDescriptions.getMessage())
            .asList()
            .contains(MessagesTranslator.toLocale("javax.validation.constraints.NotEmpty.message"));
    }

    @Test
    void testUpdateAccount_whenBankNameIsEmpty_thenBadRequest() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());

        var error = put(
            baseRoutePath + "/account",
            HttpStatus.BAD_REQUEST,
            updateAccountEntityWhenBankNameIsEmpty(account),
            Error.class
        );

        assertThat(error)
            .isNotNull();

        var actualBankDescriptions = error.getDescriptions().stream()
            .filter(descriptions -> "bank.name".equals(descriptions.getField()))
            .findAny().orElse(null);

        assertThat(actualBankDescriptions)
            .isNotNull();
        assertThat(actualBankDescriptions.getMessage())
            .asList()
            .contains(MessagesTranslator.toLocale("javax.validation.constraints.NotEmpty.message"));
    }

    @Test
    void testUpdateAccount_whenBankBicIsNull_thenBadRequest() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());

        var error = put(
            baseRoutePath + "/account",
            HttpStatus.BAD_REQUEST,
            updateAccountEntityWhenBankBicIsNull(account),
            Error.class
        );

        assertThat(error)
            .isNotNull();

        var actualBankDescriptions = error.getDescriptions().stream()
            .filter(descriptions -> "bank.bic".equals(descriptions.getField()))
            .findAny().orElse(null);

        assertThat(actualBankDescriptions)
            .isNotNull();
        assertThat(actualBankDescriptions.getMessage())
            .asList()
            .contains(MessagesTranslator.toLocale("javax.validation.constraints.NotEmpty.message"));
    }

    @Test
    void testUpdateAccount_whenBankBicIsEmpty_thenBadRequest() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());

        var error = put(
            baseRoutePath + "/account",
            HttpStatus.BAD_REQUEST,
            updateAccountEntityWhenBankBicIsEmpty(account),
            Error.class
        );

        assertThat(error)
            .isNotNull();

        var actualBankDescriptions = error.getDescriptions().stream()
            .filter(descriptions -> "bank.bic".equals(descriptions.getField()))
            .findAny().orElse(null);

        assertThat(actualBankDescriptions)
            .isNotNull();
        assertThat(actualBankDescriptions.getMessage())
            .asList()
            .contains(MessagesTranslator.toLocale("validation.account.bank.bic.length"));
    }

    @Test
    void testUpdateAccountEntityWithEmptyAccountAndBankAccount() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        var updateAccount = put(
            baseRoutePath + "/account",
            HttpStatus.OK,
            updateAccountEntityWithEmptyAccountAndBankAccount(account),
            Account.class
        );
        assertThat(updateAccount)
            .isNotNull();
        assertThat(updateAccount.getAccount())
            .isEmpty();
        assertThat(updateAccount.getBank().getBankAccount().getBankAccount())
            .isEmpty();
    }

    @Test
    void testUpdateAccountEntityInvalidAccountAndBankAccount() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        var error = put(
            baseRoutePath + "/account",
            HttpStatus.BAD_REQUEST,
            updateAccountEntityWithInvalidAccountAndBankAccount(account),
            Error.class
        );
        var actualAccountDescriptions = error.getDescriptions().stream()
            .filter(descriptions -> "account".equals(descriptions.getField()))
            .findAny().orElse(null);
        assertThat(actualAccountDescriptions)
            .isNotNull();
        assertThat(actualAccountDescriptions.getMessage())
            .asList()
            .contains(MessagesTranslator.toLocale("validation.account.control_number"))
            .contains(MessagesTranslator.toLocale("validation.account.simple_pattern"));
        var actualBankAccountDescriptions = error.getDescriptions().stream()
            .filter(descriptions -> "bank.bankAccount.bankAccount".equals(descriptions.getField()))
            .findAny().orElse(null);
        assertThat(actualBankAccountDescriptions)
            .isNotNull();
        assertThat(actualBankAccountDescriptions.getMessage())
            .asList()
            .contains(MessagesTranslator.toLocale("account.account.bank_account.control_number"))
            .contains(MessagesTranslator.toLocale("validation.account.simple_pattern"));
    }

    @Test
    void testUpdateAccountEntityWithNullAccountAndBankAccount() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        var updateAccount = put(
            baseRoutePath + "/account",
            HttpStatus.OK,
            updateAccountEntityWithNullAccountAndBankAccount(account),
            Account.class
        );
        assertThat(updateAccount)
            .isNotNull();
        assertThat(updateAccount.getAccount())
            .isEqualTo(account.getAccount());
        assertThat(updateAccount.getBank().getBankAccount().getBankAccount())
            .isEqualTo(account.getBank().getBankAccount().getBankAccount());
    }

    @Test
    void testUpdateAccount_unique() {
        var partner = createValidPartner();
        var account1 = createValidAccount(partner.getId(), partner.getDigitalId());
        var account2 = createValidAccount(partner.getId(), partner.getDigitalId());
        var updateAccount = updateAccount(account2);
        updateAccount
            .account(account1.getAccount())
            .getBank()
            .bic(account1.getBank().getBic())
            .getBankAccount()
            .bankAccount(account1.getBank().getBankAccount().getBankAccount());
        var error = put(
            baseRoutePath + "/account",
            HttpStatus.BAD_REQUEST,
            updateAccount,
            Error.class
        );
        assertThat(error)
            .isNotNull();
        assertThat(error.getCode())
            .isEqualTo(MODEL_DUPLICATE_EXCEPTION.getValue());
    }

    @Test
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
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
        AssertionsForClassTypes.assertThat(updateAccount.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains(MessagesTranslator.toLocale("validation.account.bank.bic.length"));

        var acc2 = updateAccount(account)
            .account("12345678901234567890");
        var updateAccount2 = put(
            baseRoutePath + "/account",
            HttpStatus.BAD_REQUEST,
            acc2,
            Error.class
        );
        assertThat(updateAccount2.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());

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
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());

        var acc4 = updateAccount(account)
            .bank(account.getBank()
                .bic("ABC123456789"));
        var updateAccount4 = put(
            baseRoutePath + "/account",
            HttpStatus.BAD_REQUEST,
            acc4,
            Error.class
        );
        assertThat(updateAccount4.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
        AssertionsForClassTypes.assertThat(updateAccount4.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .asList()
            .contains(MessagesTranslator.toLocale("validation.partner.illegal_symbols")+" ABC")
            .contains(MessagesTranslator.toLocale("validation.account.bank.bic.length"));
    }

    @Test
    void testPositiveUpdateAccount() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        var account2 = createValidAccount(partner.getId(), partner.getDigitalId());

        var acc = updateAccount(account);
        var updateAccount = put(
            baseRoutePath + "/account",
            HttpStatus.OK,
            acc,
            Account.class
        );
        assertThat(updateAccount)
            .isNotNull();

        var acc1 = acc
            .version(acc.getVersion() + 1)
            .account("30101810145250000416")
            .bank(account.getBank()
                .bic(getBic())
                .bankAccount(account.getBank().getBankAccount()
                    .bankAccount("30101810145250000411")));
        var updateAccount1 = put(
            baseRoutePath + "/account",
            HttpStatus.OK,
            acc1,
            Account.class
        );
        assertThat(updateAccount1)
            .isNotNull();

        var acc2 = acc1
            .version(acc1.getVersion() + 1)
            .account("30101810145250000429");
        var updateAccount2 = put(
            baseRoutePath + "/account",
            HttpStatus.OK,
            acc2,
            Account.class
        );
        assertThat(updateAccount2)
            .isNotNull();

        var acc3 = acc2
            .version(acc2.getVersion() + 1)
            .account("")
            .bank(account.getBank()
                .bic(getBic())
                .bankAccount(account.getBank().getBankAccount()
                    .bankAccount(null)));
        var updateAccount3 = put(
            baseRoutePath + "/account",
            HttpStatus.OK,
            acc3,
            Account.class
        );
        assertThat(updateAccount3)
            .isNotNull();

        var acc4 = updateAccount(account2)
            .account("");
        var updateAccount4 = put(
            baseRoutePath + "/account",
            HttpStatus.OK,
            acc4,
            Account.class
        );
        assertThat(updateAccount4)
            .isNotNull();

        var acc5 = acc4
            .version(acc4.getVersion() + 1)
            .bank(acc4.getBank()
                .bic("044525000"));
        var updateAccount5 = put(
            baseRoutePath + "/account",
            HttpStatus.OK,
            acc5,
            Account.class
        );
        assertThat(updateAccount5)
            .isNotNull();

        var acc6 = acc5
            .version(acc5.getVersion() + 1)
            .account("40101810045250010041");
        var updateAccount6 = put(
            baseRoutePath + "/account",
            HttpStatus.OK,
            acc6,
            Account.class
        );
        assertThat(updateAccount6)
            .isNotNull();
    }

    @Test
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
            .isEqualTo(OPTIMISTIC_LOCK_EXCEPTION.getValue());
        assertThat(accountError.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
            .contains("Версия записи в базе данных " + (account.getVersion() - 1) +
                " не равна версии записи в запросе version=" + version);
    }

    @Test
    void positiveTestUpdateAccountVersion() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        var accountVersion = put(
            baseRoutePath + "/account",
            HttpStatus.OK,
            updateAccount(account),
            Account.class
        );
        var checkAccount = get(
            baseRoutePath + "/accounts" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            Account.class,
            accountVersion.getDigitalId(), accountVersion.getId());
        assertThat(checkAccount)
            .isNotNull();
        assertThat(checkAccount.getVersion())
            .isEqualTo(account.getVersion() + 1);
    }

    @Test
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
            Account.class
        );
        var checkAccount = get(
            baseRoutePath + "/accounts" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            Account.class,
            accountVersion.getDigitalId(), accountVersion.getId());
        assertThat(checkAccount)
            .isNotNull();
        assertThat(checkAccount.getVersion())
            .isEqualTo(account.getVersion() + 1);

        updateAccount(account);
        account.setVersion(accountVersion.getVersion() + 1);
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
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());

        updateAccount(account);
        account.setAccount("40702810600000009222");
        account.setVersion(accountVersion.getVersion() + 1);
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
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());

        updateAccount(account);
        account.setVersion(accountVersion.getVersion());
        account.setAccount("00101643600000010006");
        account.getBank().setBic("048602001");
        account.getBank().getBankAccount().setBankAccount("40102810945370000073");
        var accountVersion3 = put(
            baseRoutePath + "/account",
            HttpStatus.OK,
            account,
            Account.class
        );
        var checkAccount3 = get(
            baseRoutePath + "/accounts" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            Account.class,
            accountVersion3.getDigitalId(), accountVersion.getId());
        assertThat(checkAccount3)
            .isNotNull();
        assertThat(checkAccount3.getVersion())
            .isEqualTo(accountVersion3.getVersion());
    }

    @Test
    void testDeleteAccount() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        var actualAccount = get(
            baseRoutePath + "/accounts" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            Account.class,
            account.getDigitalId(), account.getId()
        );
        assertThat(actualAccount)
            .isNotNull()
            .isEqualTo(account);

        var deleteAccount =
            delete(
                baseRoutePath + "/accounts" + "/{digitalId}",
                HttpStatus.NO_CONTENT,
                Map.of("ids", actualAccount.getId()),
                actualAccount.getDigitalId()
            ).getBody();
        assertThat(deleteAccount)
            .isNotNull();

        var searchAccount = get(
            baseRoutePath + "/accounts" + "/{digitalId}" + "/{id}",
            HttpStatus.NOT_FOUND,
            Error.class,
            account.getDigitalId(), account.getId()
        );
        assertThat(searchAccount)
            .isNotNull();
        assertThat(searchAccount.getCode())
            .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
    }

    @Test
    void testPriorityAccount() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        var foundAccount = get(
            baseRoutePath + "/accounts" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            Account.class,
            account.getDigitalId(), account.getId()
        );
        assertThat(foundAccount)
            .isNotNull()
            .isEqualTo(account);
        assertThat(foundAccount.getPriorityAccount())
            .isFalse();

        createValidPriorityAccount(account.getId(), account.getDigitalId());

        var actualAccount = get(
            baseRoutePath + "/accounts" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            Account.class,
            account.getDigitalId(), account.getId()
        );
        assertThat(actualAccount)
            .isNotNull();
        assertThat(actualAccount.getPriorityAccount())
            .isTrue();
    }

    @Test
    void testPriorityAccountException() {
        var partner = createValidPartner();
        var account1 = createValidAccount(partner.getId(), partner.getDigitalId());
        var account2 = createValidAccount(partner.getId(), partner.getDigitalId());
        createValidPriorityAccount(account1.getId(), account1.getDigitalId());
        var error = notCreatePriorityAccount(account2.getId(), account2.getDigitalId());
        assertThat(error)
            .isNotNull();
        assertThat(error.getCode())
            .isEqualTo(PRIORITY_ACCOUNT_MORE_ONE.getValue());
    }

    public static String getBic() {
        String bic = "525411";
        var key = randomNumeric(3);
        return key + bic;
    }
}
