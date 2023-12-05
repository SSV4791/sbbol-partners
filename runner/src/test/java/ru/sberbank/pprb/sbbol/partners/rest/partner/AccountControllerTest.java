package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.qameta.allure.Allure;
import io.restassured.common.mapper.TypeRef;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.entity.partner.GkuInnEntity;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountAndPartnerRequest;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.AccountWithPartnerResponse;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Descriptions;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.ExternalInternalIdLink;
import ru.sberbank.pprb.sbbol.partners.model.ExternalInternalIdLinksResponse;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;
import ru.sberbank.pprb.sbbol.partners.model.SearchAccounts;
import ru.sberbank.pprb.sbbol.partners.model.SignType;
import ru.sberbank.pprb.sbbol.partners.repository.partner.GkuInnDictionaryRepository;
import ru.sberbank.pprb.sbbol.partners.rest.config.SbbolIntegrationWithOutSbbolConfiguration;
import ru.sberbank.pprb.sbbol.partners.rest.partner.provider.account.GetAtAllRequisitesThenFindPartnerArgumentProvider;
import ru.sberbank.pprb.sbbol.partners.rest.partner.provider.account.GetAtAllRequisitesThenNotFoundExceptionArgumentsProvider;
import ru.sberbank.pprb.sbbol.partners.service.ids.history.IdsHistoryService;
import ru.sberbank.pprb.sbbol.partners.storage.CacheNames;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.qameta.allure.Allure.step;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.config.PodamConfiguration.getBic;
import static ru.sberbank.pprb.sbbol.partners.config.PodamConfiguration.getValidInnNumber;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.ACCOUNT_DUPLICATE_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.EXTERNAL_ID_DUPLICATE_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.MODEL_NOT_FOUND_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.MODEL_VALIDATION_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.OPTIMISTIC_LOCK_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.PRIORITY_ACCOUNT_MORE_ONE;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper.prepareSearchString;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountSignControllerTest.createValidAccountsSign;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.getValidLegalEntityPartner;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.getValidPhysicalPersonPartner;

@ContextConfiguration(classes = SbbolIntegrationWithOutSbbolConfiguration.class)
class AccountControllerTest extends BaseAccountControllerTest {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private IdsHistoryService idsHistoryService;

    @Autowired
    private GkuInnDictionaryRepository innDictionaryRepository;

    private static final String KPP_WITHOUT_ACCOUNT = "618243879";

    private GkuInnEntity gkuInnEntity;

    @AfterEach
    void after() {
        if (gkuInnEntity != null) {
            innDictionaryRepository.delete(gkuInnEntity);
        }
    }

    @Test
    @DisplayName("POST /partner/accounts/view аттрибут ЖКУ = true")
    void testViewFilter_whenGkuAttributeIsDefinedAndIsTrue() {
        var accountsFilter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            List<UUID> account = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                account.add(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
            }
            return new AccountsFilter()
                .digitalId(partner.getDigitalId())
                .partnerIds(List.of(partner.getId()))
                .accountIds(account)
                .isHousingServicesProvider(true)
                .pagination(new Pagination()
                    .count(4)
                    .offset(0));
        });

        var response = step("Выполнение post-запроса /partner/accounts/view, код ответа 200", () -> post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            accountsFilter,
            AccountsResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getAccounts())
                .isEmpty();
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/view аттрибут ЖКУ = false")
    void testViewFilter_whenGkuAttributeIsDefinedAndIsFalse() {
        var accountsFilter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            List<UUID> account = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                account.add(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
            }
            return new AccountsFilter()
                .digitalId(partner.getDigitalId())
                .partnerIds(List.of(partner.getId()))
                .accountIds(account)
                .isHousingServicesProvider(false)
                .pagination(new Pagination()
                    .count(4)
                    .offset(0));
        });

        var response = step("Выполнение post-запроса /partner/accounts/view, код ответа 200", () -> post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            accountsFilter,
            AccountsResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getAccounts())
                .hasSize(4);
            assertThat(response.getPagination().getHasNextPage())
                .isTrue();
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/view аттрибут pagination = null")
    void testNegativeViewFilter_whenPaginationIsNull() {
        var accountsFilter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            List<UUID> account = List.of(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
            return new AccountsFilter()
                .digitalId(partner.getDigitalId())
                .partnerIds(List.of(partner.getId()))
                .accountIds(account)
                .isHousingServicesProvider(false)
                .pagination(null);
        });

        var response = step("Выполнение post-запроса /partner/accounts/view, код ответа 400", () -> post(
            baseRoutePath + "/accounts/view",
            HttpStatus.BAD_REQUEST,
            accountsFilter,
            Error.class));

        step("Проверка корректности ответа", () -> {
            Descriptions descriptions = new Descriptions()
                .field("pagination")
                .message(
                    List.of(MessagesTranslator.toLocale("javax.validation.constraints.NotNull.message"))
                );
            assertThat(response)
                .isNotNull();
            assertThat(response.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
            assertThat(response.getDescriptions())
                .contains(descriptions);
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/view у pagination аттрибуты count и offset равны null")
    void testNegativeViewFilter_whenPaginationCountAndOffsetIsNull() {
        var accountsFilter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            List<UUID> account = List.of(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
            return new AccountsFilter()
                .digitalId(partner.getDigitalId())
                .partnerIds(List.of(partner.getId()))
                .accountIds(account)
                .isHousingServicesProvider(false)
                .pagination(new Pagination()
                    .count(null)
                    .offset(null));
        });

        var response = step("Выполнение post-запроса /partner/accounts/view, код ответа 400", () -> post(
            baseRoutePath + "/accounts/view",
            HttpStatus.BAD_REQUEST,
            accountsFilter,
            Error.class));

        step("Проверка корректности ответа", () -> {
            List<Descriptions> errorTexts = List.of(
                new Descriptions()
                    .field("pagination.count")
                    .message(
                        List.of("Поле обязательно для заполнения")),
                new Descriptions()
                    .field("pagination.offset")
                    .message(
                        List.of("Поле обязательно для заполнения")));
            assertThat(response)
                .isNotNull();
            assertThat(response.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
            for (var text : response.getDescriptions()) {
                assertThat(errorTexts).contains(text);
            }
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/view аттрибут partnerSearch с корректным значением Наименование партнера")
    void testViewFilter_whenPartnerSearchAttributeIsDefinedAndContainsMatchedPartnerName() {
        var accountsFilter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            List<UUID> account = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                account.add(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
            }
            return new AccountsFilter()
                .digitalId(partner.getDigitalId())
                .partnerIds(List.of(partner.getId()))
                .accountIds(account)
                .partnerSearch(partner.getOrgName())
                .pagination(new Pagination()
                    .count(4)
                    .offset(0));
        });

        var response = step("Выполнение post-запроса /partner/accounts/view, код ответа 200", () -> post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            accountsFilter,
            AccountsResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getAccounts())
                .hasSize(4);
            assertThat(response.getPagination().getHasNextPage())
                .isTrue();
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/view аттрибут partnerSearch с некорректным значением Наименование партнера")
    void testViewFilter_whenPartnerSearchAttributeIsDefinedAndContainsDontMatchedPartnerName() {
        var accountsFilter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            var partnerWithoutAccount = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            List<UUID> account = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                account.add(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
            }
            return new AccountsFilter()
                .digitalId(partner.getDigitalId())
                .partnerIds(List.of(partner.getId()))
                .accountIds(account)
                .partnerSearch(partnerWithoutAccount.getOrgName())
                .pagination(new Pagination()
                    .count(4)
                    .offset(0));
        });

        var response = step("Выполнение post-запроса /partner/accounts/view, код ответа 200", () -> post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            accountsFilter,
            AccountsResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getAccounts())
                .isEmpty();
            assertThat(response.getPagination().getHasNextPage())
                .isFalse();
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/view аттрибут partnerSearch с корректным значением ИНН")
    void testViewFilter_whenPartnerSearchAttributeIsDefinedAndContainsMatchedInn() {
        var accountsFilter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            List<UUID> account = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                account.add(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
            }
            return new AccountsFilter()
                .digitalId(partner.getDigitalId())
                .partnerIds(List.of(partner.getId()))
                .accountIds(account)
                .partnerSearch(partner.getInn())
                .pagination(new Pagination()
                    .count(4)
                    .offset(0));
        });

        var response = step("Выполнение post-запроса /partner/accounts/view, код ответа 200", () -> post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            accountsFilter,
            AccountsResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getAccounts())
                .hasSize(4);
            assertThat(response.getPagination().getHasNextPage())
                .isTrue();
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/view аттрибут partnerSearch с некорректным значением ИНН")
    void testViewFilter_whenPartnerSearchAttributeIsDefinedAndDontContainsMatchedInn() {
        var accountsFilter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            List<UUID> account = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                account.add(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
            }
            return new AccountsFilter()
                .digitalId(partner.getDigitalId())
                .partnerIds(List.of(partner.getId()))
                .accountIds(account)
                .partnerSearch(getValidInnNumber(LegalForm.LEGAL_ENTITY))
                .pagination(new Pagination()
                    .count(4)
                    .offset(0));
        });

        var response = step("Выполнение post-запроса /partner/accounts/view, код ответа 200", () -> post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            accountsFilter,
            AccountsResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getAccounts())
                .isEmpty();
            assertThat(response.getPagination().getHasNextPage())
                .isFalse();
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/view аттрибут partnerSearch с корректным значением КПП")
    void testViewFilter_whenPartnerSearchAttributeIsDefinedAndContainsMatchedKpp() {
        var accountsFilter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            List<UUID> account = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                account.add(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
            }
            return new AccountsFilter()
                .digitalId(partner.getDigitalId())
                .partnerIds(List.of(partner.getId()))
                .accountIds(account)
                .partnerSearch(partner.getKpp())
                .pagination(new Pagination()
                    .count(4)
                    .offset(0));
        });

        var response = step("Выполнение post-запроса /partner/accounts/view, код ответа 200", () -> post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            accountsFilter,
            AccountsResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getAccounts())
                .hasSize(4);
            assertThat(response.getPagination().getHasNextPage())
                .isTrue();
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/view аттрибут partnerSearch с некорректным значением КПП")
    void testViewFilter_whenPartnerSearchAttributeIsDefinedAndDontContainsMatchedKpp() {
        var accountsFilter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            List<UUID> account = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                account.add(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
            }
            return new AccountsFilter()
                .digitalId(partner.getDigitalId())
                .partnerIds(List.of(partner.getId()))
                .accountIds(account)
                .partnerSearch(KPP_WITHOUT_ACCOUNT)
                .pagination(new Pagination()
                    .count(4)
                    .offset(0));
        });

        var response = step("Выполнение post-запроса /partner/accounts/view, код ответа 200", () -> post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            accountsFilter,
            AccountsResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getAccounts())
                .isEmpty();
            assertThat(response.getPagination().getHasNextPage())
                .isFalse();
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/view аттрибут partnerSearch = No matched pattern")
    void testViewFilter_whenPartnerSearchAttributeIsDefinedAndContainsNotMatchedPattern() {
        var accountsFilter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            List<UUID> account = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                account.add(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
            }
            return new AccountsFilter()
                .digitalId(partner.getDigitalId())
                .partnerIds(List.of(partner.getId()))
                .accountIds(account)
                .partnerSearch("No matched pattern")
                .pagination(new Pagination()
                    .count(4)
                    .offset(0));
        });

        var response = step("Выполнение post-запроса /partner/accounts/view, код ответа 200", () -> post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            accountsFilter,
            AccountsResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getAccounts())
                .isEmpty();
            assertThat(response.getPagination().getHasNextPage())
                .isFalse();
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/view аттрибут partnerSearch содержит корректные данные")
    void testViewFilter_whenPartnerSearchAttributeIsDefinedAndContainsMatchedAccount() {
        var accountsFilter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            List<UUID> accounts = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                accounts.add(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
            }
            var account = createValidAccount(partner.getId(), partner.getDigitalId());
            accounts.add(account.getId());
            return new AccountsFilter()
                .digitalId(partner.getDigitalId())
                .partnerIds(List.of(partner.getId()))
                .accountIds(accounts)
                .partnerSearch(account.getAccount())
                .pagination(new Pagination()
                    .count(4)
                    .offset(0));
        });

        var response = step("Выполнение post-запроса /partner/accounts/view, код ответа 200", () -> post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            accountsFilter,
            AccountsResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getAccounts())
                .hasSize(1);
            assertThat(response.getPagination().getHasNextPage())
                .isFalse();
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/view аттрибут partnerSearch содержит часть счета")
    void testViewFilter_whenPartnerSearchAttributeIsDefinedAndContainsPartAccount() {
        var accountsFilter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            List<UUID> accounts = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                accounts.add(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
            }
            var account = createValidAccount(partner.getId(), partner.getDigitalId());
            accounts.add(account.getId());
            return new AccountsFilter()
                .digitalId(partner.getDigitalId())
                .partnerIds(List.of(partner.getId()))
                .accountIds(accounts)
                .partnerSearch(account.getAccount().substring(6))
                .pagination(new Pagination()
                    .count(4)
                    .offset(0));
        });

        var response = step("Выполнение post-запроса /partner/accounts/view, код ответа 200", () -> post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            accountsFilter,
            AccountsResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getAccounts())
                .hasSize(1);
            assertThat(response.getPagination().getHasNextPage())
                .isFalse();
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/view аттрибут partnerSearch с некорректным счетом")
    void testViewFilter_whenPartnerSearchAttributeIsDefinedAndContainsIncorrectAccountNumber() {
        var accountsFilter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            List<UUID> accounts = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                accounts.add(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
            }
            return new AccountsFilter()
                .digitalId(partner.getDigitalId())
                .partnerIds(List.of(partner.getId()))
                .accountIds(accounts)
                .partnerSearch(randomNumeric(20))
                .pagination(new Pagination()
                    .count(4)
                    .offset(0));
        });

        var response = step("Выполнение post-запроса /partner/accounts/view, код ответа 200", () -> post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            accountsFilter,
            AccountsResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getAccounts())
                .isEmpty();
            assertThat(response.getPagination().getHasNextPage())
                .isFalse();
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/view аттрибут partnerSearch с некорректным счетом")
    void testViewFilter_whenPartnerSearchAttributeIsDefinedAndMatchWithName() {
        var accountsFilter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            for (int i = 0; i < 5; i++) {
                createValidAccount(partner.getId(), partner.getDigitalId());
            }
            return new AccountsFilter()
                .digitalId(partner.getDigitalId())
                .partnerSearch(partner.getOrgName())
                .state(SignType.NOT_SIGNED)
                .pagination(new Pagination()
                    .count(30)
                    .offset(0));
        });

        var response = step("Выполнение post-запроса /partner/accounts/view, код ответа 200", () -> post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            accountsFilter,
            AccountsResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getAccounts())
                .hasSize(5);
            assertThat(response.getPagination().getHasNextPage())
                .isFalse();
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/view с 5 подписанными счетами")
    void testViewFilterSignFiveAccount() {
        var filter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            List<Account> accounts = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                accounts.add(createValidAccount(partner.getId(), partner.getDigitalId()));
            }
            createValidAccountsSign(partner.getDigitalId(), accounts, getBase64FraudMetaData());
            accounts.add(createValidAccount(partner.getId(), partner.getDigitalId()));
            var accountIds = accounts.stream().map(Account::getId).collect(Collectors.toList());
            return new AccountsFilter()
                .digitalId(partner.getDigitalId())
                .partnerIds(List.of(partner.getId()))
                .accountIds(accountIds)
                .state(SignType.SIGNED)
                .pagination(new Pagination()
                    .count(4)
                    .offset(0));
        });

        var response = step("Выполнение post-запроса /partner/accounts/view, код ответа 200", () -> post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            filter,
            AccountsResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getAccounts())
                .hasSize(4);
            assertThat(response.getPagination().getHasNextPage())
                .isEqualTo(Boolean.TRUE);
            for (var accounts : response.getAccounts()) {
                assertThat(accounts.getState()).isEqualTo(SignType.SIGNED);
            }
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/view с неподписанными счетами")
    void testViewFilterNotSignAccount() {
        var filter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            List<Account> accounts = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                accounts.add(createValidAccount(partner.getId(), partner.getDigitalId()));
            }
            createValidAccountsSign(partner.getDigitalId(), accounts, getBase64FraudMetaData());
            accounts.add(createValidAccount(partner.getId(), partner.getDigitalId()));
            var accountIds = accounts.stream().map(Account::getId).collect(Collectors.toList());
            return new AccountsFilter()
                .digitalId(partner.getDigitalId())
                .partnerIds(List.of(partner.getId()))
                .accountIds(accountIds)
                .state(SignType.NOT_SIGNED)
                .pagination(new Pagination()
                    .count(5)
                    .offset(0));
        });

        var response = step("Выполнение post-запроса /partner/accounts/view, код ответа 200", () -> post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            filter,
            AccountsResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getAccounts())
                .hasSize(1);
            assertThat(response.getPagination().getHasNextPage())
                .isEqualTo(Boolean.FALSE);
            for (var accounts : response.getAccounts()) {
                assertThat(accounts.getState()).isEqualTo(SignType.NOT_SIGNED);
            }
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/view с 4 подписанными счетами")
    void testViewFilterSignFourAccount() {
        var filter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            List<Account> accounts = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                accounts.add(createValidAccount(partner.getId(), partner.getDigitalId()));
            }
            createValidAccountsSign(partner.getDigitalId(), accounts, getBase64FraudMetaData());
            accounts.add(createValidAccount(partner.getId(), partner.getDigitalId()));
            var accountIds = accounts.stream().map(Account::getId).collect(Collectors.toList());
            return new AccountsFilter()
                .digitalId(partner.getDigitalId())
                .partnerIds(List.of(partner.getId()))
                .accountIds(accountIds)
                .state(SignType.SIGNED)
                .pagination(new Pagination()
                    .count(5)
                    .offset(0));
        });

        var response = step("Выполнение post-запроса /partner/accounts/view, код ответа 200", () -> post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            filter,
            AccountsResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getAccounts())
                .hasSize(4);
            assertThat(response.getPagination().getHasNextPage())
                .isEqualTo(Boolean.FALSE);
            for (var accounts : response.getAccounts()) {
                assertThat(accounts.getState()).isEqualTo(SignType.SIGNED);
            }
        });
    }

    @Test
    @DisplayName("GET /partner/{digitalId}/{id} получение счета")
    void testGetAccount() {
        var account = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });

        var actualAccount = step("Выполнение get-запроса /partner/{digitalId}/{id}, код ответа 200", () -> get(
            baseRoutePath + "/accounts" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            Account.class,
            account.getDigitalId(), account.getId()));

        step("Проверка корректности ответа", () -> {
            assertThat(actualAccount.getComment())
                .isEqualTo("Это тестовый комментарий");
            assertThat(actualAccount)
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("changeDate")
                .isEqualTo(account);
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/view просмотр счета")
    void testViewAccount() {
        var filter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            for (int i = 0; i < 5; i++) {
                createValidAccount(partner.getId(), partner.getDigitalId());
            }
            return new AccountsFilter()
                .digitalId(partner.getDigitalId())
                .partnerIds(List.of(partner.getId()))
                .pagination(new Pagination()
                    .count(4)
                    .offset(0));
        });

        var response = step("Выполнение post-запроса /partner/accounts/view, код ответа 200", () -> post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            filter,
            AccountsResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getAccounts())
                .hasSize(4);
            assertThat(response.getPagination().getHasNextPage())
                .isEqualTo(Boolean.TRUE);
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/view просмотр списка счетов с 2-мя accountId")
    void testViewAccountWithTwoAccountId() {
        var filter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            var acc1 = createValidAccount(partner.getId(), partner.getDigitalId());
            var acc2 = createValidAccount(partner.getId(), partner.getDigitalId());
            for (int i = 0; i < 5; i++) {
                createValidAccount(partner.getId(), partner.getDigitalId());
            }
            return new AccountsFilter()
                .digitalId(partner.getDigitalId())
                .partnerIds(List.of(partner.getId()))
                .accountIds(List.of(acc1.getId(), acc2.getId()))
                .pagination(new Pagination()
                    .count(4)
                    .offset(0));
        });

        var response = step("Выполнение post-запроса /partner/accounts/view, код ответа 200", () -> post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            filter,
            AccountsResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getAccounts())
                .hasSize(2);
            assertThat(response.getPagination().getHasNextPage())
                .isEqualTo(Boolean.FALSE);
            assertThat(response.getAccounts().stream().map(Account::getId)).contains(filter.getAccountIds().get(0), filter.getAccountIds().get(1));
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/view список счетов со всеми accountId")
    void testViewAccountWithAllAccountsId() {
        var filter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            var account1 = createValidAccount(partner.getId(), partner.getDigitalId());
            var account2 = createValidAccount(partner.getId(), partner.getDigitalId());
            var account3 = createValidAccount(partner.getId(), partner.getDigitalId());
            var account4 = createValidAccount(partner.getId(), partner.getDigitalId());
            var account5 = createValidAccount(partner.getId(), partner.getDigitalId());
            return new AccountsFilter()
                .digitalId(partner.getDigitalId())
                .accountIds(List.of(account1.getId(), account2.getId(), account3.getId(), account4.getId(), account5.getId()))
                .pagination(new Pagination()
                    .count(4)
                    .offset(0));
        });

        var response = step("Выполнение post-запроса /partner/accounts/view, код ответа 200", () -> post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            filter,
            AccountsResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getAccounts())
                .hasSize(4);
            assertThat(response.getPagination().getHasNextPage())
                .isEqualTo(Boolean.TRUE);
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/view список найденного счета")
    void testViewSearchOneAccount() {
        var filter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            var account = createValidAccount(partner.getId(), partner.getDigitalId());
            createValidAccount(partner.getId(), partner.getDigitalId());
            createValidAccount(partner.getId(), partner.getDigitalId());
            createValidAccount(partner.getId(), partner.getDigitalId());
            return new AccountsFilter()
                .digitalId(partner.getDigitalId())
                .partnerIds(List.of(partner.getId()))
                .search(new SearchAccounts().search(account.getAccount().substring(6)))
                .pagination(new Pagination()
                    .count(1)
                    .offset(0));
        });

        var response = step("Выполнение post-запроса /partner/accounts/view, код ответа 200", () -> post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            filter,
            AccountsResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getAccounts())
                .hasSize(1);
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/view список всех найденных счетов")
    void testViewSearchAllAccounts() {
        var filter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            createValidAccount(partner.getId(), partner.getDigitalId());
            createValidAccount(partner.getId(), partner.getDigitalId());
            createValidAccount(partner.getId(), partner.getDigitalId());
            createValidAccount(partner.getId(), partner.getDigitalId());
            return new AccountsFilter()
                .digitalId(partner.getDigitalId())
                .partnerIds(List.of(partner.getId()))
                .search(new SearchAccounts().search("40702810"))
                .pagination(new Pagination()
                    .count(4)
                    .offset(0));
        });

        var response = step("Выполнение post-запроса /partner/accounts/view, код ответа 200", () -> post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            filter,
            AccountsResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getAccounts())
                .hasSize(4);
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/view поиск по полному значению счета")
    void testViewSearchAccountWithFullNumber() {
        var filter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            var account = createValidAccount(partner.getId(), partner.getDigitalId());
            createValidAccount(partner.getId(), partner.getDigitalId());
            createValidAccount(partner.getId(), partner.getDigitalId());
            createValidAccount(partner.getId(), partner.getDigitalId());
            return new AccountsFilter()
                .digitalId(partner.getDigitalId())
                .partnerIds(List.of(partner.getId()))
                .search(new SearchAccounts().search(account.getAccount()))
                .pagination(new Pagination()
                    .count(4)
                    .offset(0));
        });

        var response = step("Выполнение post-запроса /partner/accounts/view, код ответа 200", () -> post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            filter,
            AccountsResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getAccounts())
                .hasSize(1);
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/view бюджетный счет")
    void testViewBudgetAccount() {
        var filter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            createValidAccount(partner.getId(), partner.getDigitalId());
            createValidAccount(partner.getId(), partner.getDigitalId());
            createValidAccount(partner.getId(), partner.getDigitalId());
            createValidBudgetAccount(partner.getId(), partner.getDigitalId());
            return new AccountsFilter()
                .digitalId(partner.getDigitalId())
                .partnerIds(List.of(partner.getId()))
                .isBudget(true)
                .pagination(new Pagination()
                    .count(4)
                    .offset(0));
        });

        var response1 = step("Выполнение post-запроса /partner/accounts/view, код ответа 200", () -> post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            filter,
            AccountsResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(response1)
                .isNotNull();
            assertThat(response1.getAccounts())
                .hasSize(1);
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/view проверка в ответе признака бюджетности для бюджетного счета")
    void testGetAccounts_whenBudget40101Account_thenBudgetAttributeIsTrue() {
        var filter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            createValidBudgetAccountWith40101Balance(partner.getId(), partner.getDigitalId());
            return new AccountsFilter()
                .digitalId(partner.getDigitalId())
                .partnerIds(List.of(partner.getId()))
                .pagination(new Pagination()
                    .count(4)
                    .offset(0));
        });

        var accountsResponse = step("Выполнение post-запроса /partner/accounts/view, код ответа 200", () -> post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            filter,
            AccountsResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(accountsResponse)
                .isNotNull();
            assertThat(accountsResponse.getAccounts())
                .hasSize(1);
            var account = accountsResponse.getAccounts().get(0);
            assertThat(account.getBudget())
                .isTrue();
        });
    }

    @ParameterizedTest
    @DisplayName("POST /partner/accounts/view проверка получения счёта с маской 40101 с флагом бюджетности")
    @MethodSource("argumentsForGettingAccountByBudgetFilter")
    void testGetAccounts_whenBudget40101AccountAndBudgetFlagExistInRequest(Boolean isBudget, int responseAccountCount) {
        var filter = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
            createValidAccount(partner.getId(), partner.getDigitalId());
            createValidAccount(partner.getId(), partner.getDigitalId());
            createValidBudgetAccountWith40101Balance(partner.getId(), partner.getDigitalId());

            return new AccountsFilter()
                .digitalId(partner.getDigitalId())
                .partnerIds(List.of(partner.getId()))
                .isBudget(isBudget)
                .pagination(new Pagination()
                    .count(4)
                    .offset(0));
        });

        var accountsResponse = step("Выполнение post-запроса /partner/accounts/view, код ответа 200", () -> post(
            baseRoutePath + "/accounts/view",
            HttpStatus.OK,
            filter,
            AccountsResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(accountsResponse)
                .isNotNull();
            assertThat(accountsResponse.getAccounts())
                .hasSize(responseAccountCount);
        });
    }

    static Stream<? extends Arguments> argumentsForGettingAccountByBudgetFilter() {
        return Stream.of(
            Arguments.of(Boolean.TRUE, 1),
            Arguments.of(Boolean.FALSE, 3),
            Arguments.of(null, 3));
    }

    @Test
    @DisplayName("POST /partner/accounts создание счета")
    void testCreateAccount() {
        var expected = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner();
            var validAccount = getValidAccount(partner.getId(), partner.getDigitalId());
            validAccount.setExternalId(UUID.randomUUID());
            return validAccount;
        });

        var account = step("Выполнение post-запроса /partner/accounts, код ответа 201", () ->
            createValidAccount(expected));

        step("Проверка корректности ответа", () ->
            assertThat(account)
                .usingRecursiveComparison()
                .ignoringFields(
                    "id",
                    "changeDate",
                    "version",
                    "priorityAccount",
                    "state",
                    "budget",
                    "externalIds",
                    "bank.accountId",
                    "bank.id",
                    "bank.version",
                    "bank.bankAccount.bankId",
                    "bank.bankAccount.id",
                    "bank.bankAccount.version")
                .isEqualTo(expected));
    }

    @Test
    @DisplayName("POST /partner/accounts создание счета")
    void testCreateAccountCheckExternalId() {
        var account = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner();
            return createValidAccount(getValidAccount(partner.getId(), partner.getDigitalId()));
        });

        var idsLink = step("Выполнение запроса", () ->
            idsHistoryService.getAccountInternalIds(account.getDigitalId(), List.of(account.getId())));

        step("Проверка корректности ответа", () -> {
            assertThat(idsLink)
                .isNotNull();
            assertThat(idsLink.getIdLinks())
                .isNotNull()
                .asList()
                .hasSize(1);
            assertThat(idsLink.getIdLinks().get(0).getExternalId())
                .isEqualTo(account.getId());
        });
    }

    @Test
    @DisplayName("POST /partner/accounts дублирование счета когда account = '' и account = null")
    void testCreateAccount_whenDuplicationByEmptyAndNullAccount() {
        var expectedAccount = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner();
            var account = getValidAccount(partner.getId(), partner.getDigitalId());
            account.setAccount(StringUtils.EMPTY);
            createValidAccount(account);
            return account;
        });

        var error = step("Выполнение post-запроса /partner/account, код ответа 400", () -> {
            expectedAccount.setAccount(null);
            return post(
                baseRoutePath + "/account",
                HttpStatus.BAD_REQUEST,
                expectedAccount,
                Error.class);
        });

        step("Проверка корректности ответа", () -> {
            assertThat(error)
                .isNotNull();
            assertThat(error.getCode())
                .isEqualTo(ACCOUNT_DUPLICATE_EXCEPTION.getValue());
        });
    }

    @Test
    @DisplayName("POST /partner/accounts дублирование счета когда bankAccount = '' и bankAccount = null")
    void testCreateAccount_whenDuplicationByEmptyAndNullBankAccount() {
        var expectedAccount = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner();
            var account = getValidAccount(partner.getId(), partner.getDigitalId());
            account.getBank().getBankAccount().setBankAccount(StringUtils.EMPTY);
            createValidAccount(account);
            return account;
        });

        var error = step("Выполнение post-запроса /partner/account, код ответа 400", () -> {
            expectedAccount.getBank().getBankAccount().setBankAccount(null);
            return post(
                baseRoutePath + "/account",
                HttpStatus.BAD_REQUEST,
                expectedAccount,
                Error.class);
        });

        step("Проверка корректности ответа", () -> {
            assertThat(error)
                .isNotNull();
            assertThat(error.getCode())
                .isEqualTo(ACCOUNT_DUPLICATE_EXCEPTION.getValue());
        });
    }

    @Test
    @DisplayName("POST /partner/account создание счета, проверка на уникальность")
    void testCreateAccount_unique() {
        var expected_account = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner();
            var expected = getValidAccount(partner.getId(), partner.getDigitalId());
            createValidAccount(expected);
            return expected;
        });

        var error = step("Выполнение post-запроса /partner/account, код ответа 400", () -> post(
            baseRoutePath + "/account",
            HttpStatus.BAD_REQUEST,
            expected_account,
            Error.class));

        step("Проверка корректности ответа", () -> {
            assertThat(error)
                .isNotNull();
            assertThat(error.getCode())
                .isEqualTo(ACCOUNT_DUPLICATE_EXCEPTION.getValue());
        });
    }

    @Test
    @DisplayName("POST /partner/accounts создание невалидного счета")
    void testCreateNotValidAccount() {
        var error = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner();
            return createNotValidAccount(partner.getId(), partner.getDigitalId());
        });

        step("Проверка корректности ответа", () -> {
            assertThat(error)
                .isNotNull();
            assertThat(error.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
        });

        step("Проверка ошибок счета", () -> {
            var actualAccountDescriptions = error.getDescriptions().stream()
                .filter(descriptions -> "account".equals(descriptions.getField()))
                .findAny().orElse(null);
            assertThat(actualAccountDescriptions)
                .isNotNull();
            assertThat(actualAccountDescriptions.getMessage())
                .asList()
                .contains(MessagesTranslator.toLocale("validation.account.simple_pattern"));
        });

        step("Проверка ошибок банковского счета", () -> {
            var actualBankAccountDescriptions = error.getDescriptions().stream()
                .filter(descriptions -> "bank.bankAccount.bankAccount".equals(descriptions.getField()))
                .findAny().orElse(null);
            assertThat(actualBankAccountDescriptions)
                .isNotNull();
            assertThat(actualBankAccountDescriptions.getMessage())
                .asList()
                .contains(MessagesTranslator.toLocale("validation.account.simple_pattern"));
        });
    }

    @Test
    @DisplayName("POST /partner/accounts с пустыми счетом и корр. счетом")
    void testCreateWithEmptyAccountAndBankAccount() {
        var partner = step("Подготовка тестовых данных",
            (Allure.ThrowableRunnable<Partner>) PartnerControllerTest::createValidPartner);
        var account = step("Выполнение post-запроса /partner/accounts, код ответа 201", () ->
            createAccountEntityWithEmptyAccountAndBankAccount(partner.getId(), partner.getDigitalId()));

        step("Проверка корректности ответа", () ->
            assertThat(account)
                .isNotNull());
    }

    @Test
    @DisplayName("POST /partner/accounts с счетом и корр. счетомБ равные null")
    void testCreateWithNullAccountAndBankAccount() {
        var partner = step("Подготовка тестовых данных",
            (Allure.ThrowableRunnable<Partner>) PartnerControllerTest::createValidPartner);
        var account = step("Выполнение post-запроса /partner/accounts, код ответа 201", () ->
            createAccountEntityWithNullAccountAndBankAccount(partner.getId(), partner.getDigitalId()));

        step("Проверка корректности ответа", () ->
            assertThat(account)
                .isNotNull());
    }

    @Test
    @DisplayName("POST /partner/account с банком равным null")
    void testCreateAccount_whenBankIsNull_thenBadRequest() {
        var partner = step("Подготовка тестовых данных",
            (Allure.ThrowableRunnable<Partner>) PartnerControllerTest::createValidPartner);
        var error = step("Выполнение post-запроса /partner/account, код ответа 400", () ->
            createAccountEntityWhenBankIsNull(partner.getId(), partner.getDigitalId()));

        step("Проверка корректности ответа", () ->
            assertThat(error)
                .isNotNull());

        var actualBankDescriptions = step("Подготовка тестовых данных", () ->
            error.getDescriptions().stream()
                .filter(descriptions -> "bank".equals(descriptions.getField()))
                .findAny().orElse(null));

        step("Проверка корректности ответа", () -> {
            assertThat(actualBankDescriptions)
                .isNotNull();
            assertThat(actualBankDescriptions.getMessage())
                .asList()
                .contains(MessagesTranslator.toLocale("javax.validation.constraints.NotEmpty.message"));
        });
    }

    @Test
    @DisplayName("POST /partner/account с именем банка равным null")
    void testCreateAccount_whenBankNameIsNull_thenBadRequest() {
        var partner = step("Подготовка тестовых данных",
            (Allure.ThrowableRunnable<Partner>) PartnerControllerTest::createValidPartner);
        var error = step("Выполнение post-запроса /partner/account, код ответа 400", () ->
            createAccountEntityWhenBankNameIsNull(partner.getId(), partner.getDigitalId()));

        step("Проверка корректности ответа", () ->
            assertThat(error)
                .isNotNull());

        var actualBankDescriptions = step("Подготовка тестовых данных", () ->
            error.getDescriptions().stream()
                .filter(descriptions -> "bank.name".equals(descriptions.getField()))
                .findAny().orElse(null));

        step("Проверка корректности ответа", () -> {
            assertThat(actualBankDescriptions)
                .isNotNull();
            assertThat(actualBankDescriptions.getMessage())
                .asList()
                .contains(MessagesTranslator.toLocale("javax.validation.constraints.NotEmpty.message"));
        });
    }

    @Test
    @DisplayName("POST /partner/account с пустым именем банка")
    void testCreateAccount_whenBankNameIsEmpty_thenBadRequest() {
        var partner = step("Подготовка тестовых данных",
            (Allure.ThrowableRunnable<Partner>) PartnerControllerTest::createValidPartner);
        var error = step("Выполнение post-запроса /partner/account, код ответа 400", () ->
            createAccountEntityWhenBankNameIsEmpty(partner.getId(), partner.getDigitalId()));

        step("Проверка корректности ответа", () ->
            assertThat(error)
                .isNotNull());

        var actualBankDescriptions = step("Подготовка тестовых данных", () ->
            error.getDescriptions().stream()
                .filter(descriptions -> "bank.name".equals(descriptions.getField()))
                .findAny().orElse(null));

        step("Проверка корректности ответа", () -> {
            assertThat(actualBankDescriptions)
                .isNotNull();
            assertThat(actualBankDescriptions.getMessage())
                .asList()
                .contains(MessagesTranslator.toLocale("javax.validation.constraints.NotEmpty.message"));
        });
    }

    @Test
    @DisplayName("POST /partner/account с БИК банка равным null")
    void testCreateAccount_whenBankBicIsNull_thenBadRequest() {
        var partner = step("Подготовка тестовых данных",
            (Allure.ThrowableRunnable<Partner>) PartnerControllerTest::createValidPartner);
        var error = step("Выполнение post-запроса /partner/account, код ответа 400", () ->
            createAccountEntityWhenBankBicIsNull(partner.getId(), partner.getDigitalId()));

        step("Проверка корректности ответа", () ->
            assertThat(error)
                .isNotNull());

        var actualBankDescriptions = step("Подготовка тестовых данных", () ->
            error.getDescriptions().stream()
                .filter(descriptions -> "bank.bic".equals(descriptions.getField()))
                .findAny().orElse(null));

        step("Проверка корректности ответа", () -> {
            assertThat(actualBankDescriptions)
                .isNotNull();
            assertThat(actualBankDescriptions.getMessage())
                .asList()
                .contains(MessagesTranslator.toLocale("javax.validation.constraints.NotEmpty.message"));
        });
    }

    @Test
    @DisplayName("POST /partner/account с пустым БИК банка")
    void testCreateAccount_whenBankBicIsEmpty_thenBadRequest() {
        var partner = step("Подготовка тестовых данных",
            (Allure.ThrowableRunnable<Partner>) PartnerControllerTest::createValidPartner);
        var error = step("Выполнение post-запроса /partner/account, код ответа 400", () ->
            createAccountEntityWhenBankBicIsEmpty(partner.getId(), partner.getDigitalId()));

        step("Проверка корректности ответа", () ->
            assertThat(error)
                .isNotNull());

        var actualBankDescriptions = step("Подготовка тестовых данных", () ->
            error.getDescriptions().stream()
                .filter(descriptions -> "bank.bic".equals(descriptions.getField()))
                .findAny().orElse(null));

        step("Проверка корректности ответа", () -> {
            assertThat(actualBankDescriptions)
                .isNotNull();
            assertThat(actualBankDescriptions.getMessage())
                .asList()
                .contains(MessagesTranslator.toLocale("validation.account.bank.bic.length"));
        });
    }

    @Test
    @DisplayName("POST /partner/accounts счет с валютой USD")
    void testCreateUsdAccount() {
        var expected_account = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner();
            var expected = getValidAccount(partner.getId(), partner.getDigitalId());
            expected.setAccount("40817840100000000001");
            return expected;
        });

        var error = step("Выполнение post-запроса /partner/accounts, код ответа 400", () ->
            createInvalidAccount(expected_account));

        step("Проверка корректности ответа", () -> {
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
        });
    }

    @Test
    @DisplayName("POST /partner/accounts счет с невалидной валютой")
    void testCreateBudgetAccount_whenInvalidCodeCurrency() {
        var expected = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner();
            var expectedAccount = getValidBudgetAccount(partner.getId(), partner.getDigitalId());
            expectedAccount.setAccount("00817810100000000001");
            return expectedAccount;
        });

        var error = step("Выполнение post-запроса /partner/accounts, код ответа 400", () ->
            createInvalidAccount(expected));

        step("Проверка корректности ответа", () -> {
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
        });
    }

    @Test
    @DisplayName("POST /partner/accounts невалидный бюджетный счет")
    void testCreateBudgetAccount_whenInvalidBalance() {
        var expected = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner();
            var expectedAccount = getValidBudgetAccount(partner.getId(), partner.getDigitalId());
            expectedAccount.setAccount("10817643100000000001");
            return expectedAccount;
        });

        var error = step("Выполнение post-запроса /partner/accounts, код ответа 400", () ->
            createInvalidAccount(expected));

        step("Проверка корректности ответа", () -> {
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
        });
    }

    @Test
    @DisplayName("POST /partner/accounts валидный бюджетный счет")
    void testCreateValidBudgetAccount() {
        var partner = step("Подготовка тестовых данных",
            (Allure.ThrowableRunnable<Partner>) PartnerControllerTest::createValidPartner);

        var expected = step("Выполнение post-запроса /partner/accounts, код ответа 201", () ->
            getValidBudgetAccount(partner.getId(), partner.getDigitalId()));

        step("Проверка корректности ответа", () -> {
            var actualAccount = createValidAccount(expected);
            assertThat(actualAccount)
                .isNotNull();
        });
    }

    @Test
    @DisplayName("PUT /partner/accounts редактирование счета")
    void testUpdateAccount() {
        var account = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });

        var updateAccount = step("Выполнение put-запроса /partner/accounts, код ответа 200", () -> put(
            baseRoutePath + "/account",
            HttpStatus.OK,
            updateAccount(account),
            Account.class));

        step("Проверка корректности ответа", () -> {
            assertThat(updateAccount)
                .isNotNull();
            assertThat(updateAccount.getComment())
                .isNotEqualTo(account.getComment());
            assertThat(updateAccount.getComment())
                .isNotNull();
        });
    }

    @Test
    @DisplayName("PUT /partner/accounts редактирование счета")
    void testUpdateAccount_whenBankAccountIsEmpty() {
        var account = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner();
            return createValidAccountWithEmptyBankAccount(partner.getId(), partner.getDigitalId());
        });

        var updateAccount = step("Выполнение put-запроса /partner/accounts, код ответа 200", () -> put(
            baseRoutePath + "/account",
            HttpStatus.OK,
            updateAccount(account),
            Account.class));

        step("Проверка корректности ответа", () -> {
            assertThat(updateAccount)
                .isNotNull();
            assertThat(updateAccount.getComment())
                .isNotEqualTo(account.getComment());
            assertThat(updateAccount.getComment())
                .isNotNull();
        });
    }

    @Test
    @DisplayName("PUT /partner/account редактирование счета когда банк равен null")
    void testUpdateAccount_whenBankIsNull_thenBadRequest() {
        var account = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });

        var error = step("Выполнение put-запроса /partner/account, код ответа 400", () -> put(
            baseRoutePath + "/account",
            HttpStatus.BAD_REQUEST,
            updateAccountEntityWhenBankIsNull(account),
            Error.class));

        step("Проверка корректности ответа", () -> {
            assertThat(error)
                .isNotNull();
        });

        var actualBankDescriptions = step("Подготовка тестовых данных", () ->
            error.getDescriptions().stream()
                .filter(descriptions -> "bank".equals(descriptions.getField()))
                .findAny().orElse(null));

        step("Проверка корректности ответа", () -> {
            assertThat(actualBankDescriptions)
                .isNotNull();
            assertThat(actualBankDescriptions.getMessage())
                .asList()
                .contains(MessagesTranslator.toLocale("javax.validation.constraints.NotEmpty.message"));
        });
    }

    @Test
    @DisplayName("PUT /partner/account редактирование счета когда наименование банка равно null")
    void testUpdateAccount_whenBankNameIsNull_thenBadRequest() {
        var account = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });

        var error = step("Выполнение put-запроса /partner/account, код ответа 400", () -> put(
            baseRoutePath + "/account",
            HttpStatus.BAD_REQUEST,
            updateAccountEntityWhenBankNameIsNull(account),
            Error.class
        ));

        step("Проверка корректности ответа", () -> {
            assertThat(error)
                .isNotNull();
        });

        var actualBankDescriptions = step("Подготовка тестовых данных", () ->
            error.getDescriptions().stream()
                .filter(descriptions -> "bank.name".equals(descriptions.getField()))
                .findAny().orElse(null));

        step("Проверка корректности ответа", () -> {
            assertThat(actualBankDescriptions)
                .isNotNull();
            assertThat(actualBankDescriptions.getMessage())
                .asList()
                .contains(MessagesTranslator.toLocale("javax.validation.constraints.NotEmpty.message"));
        });
    }

    @Test
    @DisplayName("PUT /partner/account редактирование счета когда наименование банка пустое")
    void testUpdateAccount_whenBankNameIsEmpty_thenBadRequest() {
        var account = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });

        var error = step("Выполнение put-запроса /partner/account, код ответа 400", () -> put(
            baseRoutePath + "/account",
            HttpStatus.BAD_REQUEST,
            updateAccountEntityWhenBankNameIsEmpty(account),
            Error.class
        ));

        step("Проверка корректности ответа", () -> {
            assertThat(error)
                .isNotNull();
        });

        var actualBankDescriptions = step("Подготовка тестовых данных", () ->
            error.getDescriptions().stream()
                .filter(descriptions -> "bank.name".equals(descriptions.getField()))
                .findAny().orElse(null));

        step("Проверка корректности ответа", () -> {
            assertThat(actualBankDescriptions)
                .isNotNull();
            assertThat(actualBankDescriptions.getMessage())
                .asList()
                .contains(MessagesTranslator.toLocale("javax.validation.constraints.NotEmpty.message"));
        });
    }

    @Test
    @DisplayName("PUT /partner/account редактирование счета когда БИК банка равен null")
    void testUpdateAccount_whenBankBicIsNull_thenBadRequest() {
        var account = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });

        var error = step("Выполнение put-запроса /partner/account, код ответа 400", () -> put(
            baseRoutePath + "/account",
            HttpStatus.BAD_REQUEST,
            updateAccountEntityWhenBankBicIsNull(account),
            Error.class
        ));

        step("Проверка корректности ответа", () -> {
            assertThat(error)
                .isNotNull();
        });

        var actualBankDescriptions = step("Подготовка тестовых данных", () ->
            error.getDescriptions().stream()
                .filter(descriptions -> "bank.bic".equals(descriptions.getField()))
                .findAny().orElse(null));

        step("Проверка корректности ответа", () -> {
            assertThat(actualBankDescriptions)
                .isNotNull();
            assertThat(actualBankDescriptions.getMessage())
                .asList()
                .contains(MessagesTranslator.toLocale("javax.validation.constraints.NotEmpty.message"));
        });
    }

    @Test
    @DisplayName("PUT /partner/account редактирование счета когда БИК банка пустой")
    void testUpdateAccount_whenBankBicIsEmpty_thenBadRequest() {
        var account = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });

        var error = step("Выполнение put-запроса /partner/account, код ответа 400", () -> put(
            baseRoutePath + "/account",
            HttpStatus.BAD_REQUEST,
            updateAccountEntityWhenBankBicIsEmpty(account),
            Error.class
        ));

        step("Проверка корректности ответа", () -> {
            assertThat(error)
                .isNotNull();
        });

        var actualBankDescriptions = step("Подготовка тестовых данных", () ->
            error.getDescriptions().stream()
                .filter(descriptions -> "bank.bic".equals(descriptions.getField()))
                .findAny().orElse(null));

        step("Проверка корректности ответа", () -> {
            assertThat(actualBankDescriptions)
                .isNotNull();
            assertThat(actualBankDescriptions.getMessage())
                .asList()
                .contains(MessagesTranslator.toLocale("validation.account.bank.bic.length"));
        });
    }

    @Test
    @DisplayName("PUT /partner/accounts пустые счет и корр. счет")
    void testUpdateAccountEntityWithEmptyAccountAndBankAccount() {
        var account = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });

        var updateAccount = step("Выполнение put-запроса /partner/accounts, код ответа 200", () -> put(
            baseRoutePath + "/account",
            HttpStatus.OK,
            updateAccountEntityWithEmptyAccountAndBankAccount(account),
            Account.class
        ));

        step("Проверка корректности ответа", () -> {
            assertThat(updateAccount)
                .isNotNull();
            assertThat(updateAccount.getAccount())
                .isEmpty();
            assertThat(updateAccount.getBank().getBankAccount().getBankAccount())
                .isEmpty();
        });
    }

    @Test
    @DisplayName("PUT /partner/account редактирование счета когда не верный номер счёта и номер корр счёта")
    void testUpdateAccountEntityInvalidAccountAndBankAccount() {
        var account = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });

        var error = step("Выполнение put-запроса /partner/account, код ответа 400", () -> put(
            baseRoutePath + "/account",
            HttpStatus.BAD_REQUEST,
            updateAccountEntityWithInvalidAccountAndBankAccount(account),
            Error.class));

        var actualAccountDescriptions = step("Подготовка тестовых данных", () ->
            error.getDescriptions().stream()
                .filter(descriptions -> "account".equals(descriptions.getField()))
                .findAny().orElse(null));

        step("Проверка корректности ответа", () -> {
            assertThat(actualAccountDescriptions)
                .isNotNull();
            assertThat(actualAccountDescriptions.getMessage())
                .asList()
                .contains(MessagesTranslator.toLocale("validation.account.simple_pattern"))
                .doesNotContainSequence(MessagesTranslator.toLocale("validation.account.rub_code_currency"));
        });

        var actualBankAccountDescriptions = step("Подготовка тестовых данных", () ->
            error.getDescriptions().stream()
                .filter(descriptions -> "bank.bankAccount.bankAccount".equals(descriptions.getField()))
                .findAny().orElse(null));

        step("Проверка корректности ответа", () -> {
            assertThat(actualBankAccountDescriptions)
                .isNotNull();
            assertThat(actualBankAccountDescriptions.getMessage())
                .asList()
                .contains(MessagesTranslator.toLocale("validation.account.simple_pattern"));
        });
    }

    @Test
    @DisplayName("PUT /partner/accounts счет и корр. счет равны null")
    void testUpdateAccountEntityWithNullAccountAndBankAccount() {
        var accountCreate = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });

        var accountChange = step("Подготовка тестовых данных", () ->
            updateAccountEntityWithNullAccountAndBankAccount(accountCreate));

        var updateAccount = step("Выполнение put-запроса /partner/accounts, код ответа 200", () -> put(
            baseRoutePath + "/account",
            HttpStatus.OK,
            accountChange,
            Account.class));

        step("Проверка корректности ответа", () -> {
            assertThat(updateAccount)
                .isNotNull();
            assertThat(updateAccount.getAccount())
                .isNotEqualTo(accountCreate.getAccount())
                .isEqualTo(accountChange.getAccount());
            assertThat(updateAccount.getBank().getBankAccount().getBankAccount())
                .isNotEqualTo(accountCreate.getBank().getBankAccount().getBankAccount())
                .isEqualTo(accountChange.getBank().getBankAccount().getBankAccount());
        });
    }

    @Test
    @DisplayName("PUT /partner/accounts проверка на уникальность")
    void testUpdateAccount_unique() {
        var expected = step("Подготовка тестовых данных", () -> {
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
            return updateAccount;
        });

        var error = step("Выполнение put-запроса /partner/accounts, код ответа 400", () -> put(
            baseRoutePath + "/account",
            HttpStatus.BAD_REQUEST,
            expected,
            Error.class));

        step("Проверка корректности ответа", () -> {
            assertThat(error)
                .isNotNull();
            assertThat(error.getCode())
                .isEqualTo(ACCOUNT_DUPLICATE_EXCEPTION.getValue());
        });
    }

    @Test
    @DisplayName("PUT /partner/accounts негативные сценарии")
    void testNegativeUpdateAccount() {
        var account = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });

        var updateAccount = step("Выполнение put-запроса /partner/accounts(БИК банка и корр. счет пустые), код ответа 400", () -> {
            var acc = updateAccount(account)
                .bank(account.getBank()
                    .bic("")
                    .bankAccount(account.getBank().getBankAccount()
                        .bankAccount("")));
            return put(
                baseRoutePath + "/account",
                HttpStatus.BAD_REQUEST,
                acc,
                Error.class);
        });

        step("Проверка корректности ответа", () -> {
            assertThat(updateAccount)
                .isNotNull();
            assertThat(updateAccount.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
            AssertionsForClassTypes.assertThat(updateAccount.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
                .asList()
                .contains(MessagesTranslator.toLocale("validation.account.bank.bic.length"));
        });

        var updateAccount2 = step("Выполнение put-запроса /partner/accounts(некорректный счет), код ответа 400", () -> {
            var acc2 = updateAccount(account)
                .account("12345678901234567890");
            return put(
                baseRoutePath + "/account",
                HttpStatus.BAD_REQUEST,
                acc2,
                Error.class);
        });
        step("Проверка корректности ответа", () ->
            assertThat(updateAccount2.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue()));

        var updateAccount3 = step("Выполнение put-запроса /partner/accounts(не найден БИК), код ответа 400", () -> {
            var acc3 = updateAccount(account)
                .bank(account.getBank()
                    .bic("044525002"));
            return put(
                baseRoutePath + "/account",
                HttpStatus.BAD_REQUEST,
                acc3,
                Error.class);
        });
        step("Проверка корректности ответа", () ->
            assertThat(updateAccount3.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue()));

        var updateAccount4 = step("Выполнение put-запроса /partner/accounts(некорректный БИК), код ответа 400", () -> {
            var acc4 = updateAccount(account)
                .bank(account.getBank()
                    .bic("ABC123456789"));
            return put(
                baseRoutePath + "/account",
                HttpStatus.BAD_REQUEST,
                acc4,
                Error.class);
        });
        step("Проверка корректности ответа", () -> {
            assertThat(updateAccount4.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
            AssertionsForClassTypes.assertThat(updateAccount4.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
                .asList()
                .contains(MessagesTranslator.toLocale("validation.partner.illegal_symbols") + " ABC")
                .contains(MessagesTranslator.toLocale("validation.account.bank.bic.length"));
        });
    }

    @Test
    @DisplayName("PUT /partner/accounts позитивные сценарии")
    void testPositiveUpdateAccount() {
        var partner = step("Создание партнера",
            (Allure.ThrowableRunnable<Partner>) PartnerControllerTest::createValidPartner);
        var account = step("Создание счета", () ->
            createValidAccount(partner.getId(), partner.getDigitalId()));
        var account2 = step("Создание второго счета", () ->
            createValidAccount(partner.getId(), partner.getDigitalId()));

        var updateAcc = step("Выполнение put-запроса /partner/accounts, код ответа 200", () -> {
            var acc = updateAccount(account);
            var updateAccount = put(
                baseRoutePath + "/account",
                HttpStatus.OK,
                acc,
                Account.class);
            assertThat(updateAccount)
                .isNotNull();
            return acc;
        });

        var updateAcc1 = step("Выполнение put-запроса /partner/accounts(изменение счета партнера на корр. счет), код ответа 200", () -> {
            var acc1 = updateAcc
                .version(updateAcc.getVersion() + 1)
                .account("30101810145250000416")
                .bank(account.getBank()
                    .bic(getBic())
                    .bankAccount(account.getBank().getBankAccount()
                        .bankAccount("30101810145250000411")));
            var updateAccount1 = put(
                baseRoutePath + "/account",
                HttpStatus.OK,
                acc1,
                Account.class);
            assertThat(updateAccount1)
                .isNotNull();
            return acc1;
        });

        var updateAcc2 = step("Выполнение put-запроса /partner/accounts(изменение корр. счета у партнера), код ответа 200", () -> {
            var acc2 = updateAcc1
                .version(updateAcc1.getVersion() + 1)
                .account("30101810145250000429");
            var updateAccount2 = put(
                baseRoutePath + "/account",
                HttpStatus.OK,
                acc2,
                Account.class);
            assertThat(updateAccount2)
                .isNotNull();
            return acc2;
        });

        step("Выполнение put-запроса /partner/accounts(счет партнера пуст,корр. счет банка null), код ответа 200", () -> {
            var acc3 = updateAcc2
                .version(updateAcc2.getVersion() + 1)
                .account("")
                .bank(account.getBank()
                    .bic(getBic())
                    .bankAccount(account.getBank().getBankAccount()
                        .bankAccount(null)));
            var updateAccount3 = put(
                baseRoutePath + "/account",
                HttpStatus.OK,
                acc3,
                Account.class);
            assertThat(updateAccount3)
                .isNotNull();
        });

        var updateAcc4 = step("Выполнение put-запроса /partner/accounts(второй счет пуст), код ответа 200", () -> {
            var acc4 = updateAccount(account2)
                .account("");
            var updateAccount4 = put(
                baseRoutePath + "/account",
                HttpStatus.OK,
                acc4,
                Account.class);
            assertThat(updateAccount4)
                .isNotNull();
            return acc4;
        });

        var updateAcc5 = step("Выполнение put-запроса /partner/accounts(второй счет пуст), код ответа 200", () -> {
            var acc5 = updateAcc4
                .version(updateAcc4.getVersion() + 1)
                .bank(updateAcc4.getBank()
                    .bic("044525000"));
            var updateAccount5 = put(
                baseRoutePath + "/account",
                HttpStatus.OK,
                acc5,
                Account.class);
            assertThat(updateAccount5)
                .isNotNull();
            return acc5;
        });

        step("Выполнение put-запроса /partner/accounts(корректный счет), код ответа 200", () -> {
            var acc6 = updateAcc5
                .version(updateAcc5.getVersion() + 1)
                .account("40101810045250010041");
            var updateAccount6 = put(
                baseRoutePath + "/account",
                HttpStatus.OK,
                acc6,
                Account.class);
            assertThat(updateAccount6)
                .isNotNull();
        });
    }

    @Test
    @DisplayName("PUT /partner/accounts негативный сценарий обновления версии")
    void negativeTestUpdateAccountVersion() {
        var expected = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner();
            var account = createValidAccount(partner.getId(), partner.getDigitalId());
            Long version = account.getVersion() + 1;
            account.setVersion(version);
            return account;
        });

        var accountError = step("Выполнение put-запроса /partner/accounts, код ответа 400", () -> put(
            baseRoutePath + "/account",
            HttpStatus.BAD_REQUEST,
            updateAccount(expected),
            Error.class));

        step("Проверка корректности ответа", () -> {
            assertThat(accountError.getCode())
                .isEqualTo(OPTIMISTIC_LOCK_EXCEPTION.getValue());
            assertThat(accountError.getDescriptions().stream().map(Descriptions::getMessage).findAny().orElse(null))
                .contains("Версия записи в базе данных " + (expected.getVersion() - 1) +
                    " не равна версии записи в запросе version=" + expected.getVersion());
        });
    }

    @Test
    @DisplayName("PUT /partner/account обновление версии")
    void positiveTestUpdateAccountVersion() {
        var account = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });

        var accountVersion = step("Выполнение put-запроса /partner/account, код ответа 200", () -> put(
            baseRoutePath + "/account",
            HttpStatus.OK,
            updateAccount(account),
            Account.class));

        var checkAccount = step("Выполнение get-запроса /partner/accounts/{digitalId}/{id}, код ответа 200", () -> get(
            baseRoutePath + "/accounts" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            Account.class,
            accountVersion.getDigitalId(), accountVersion.getId()));

        step("Проверка корректности ответа get-запроса", () -> {
            assertThat(checkAccount)
                .isNotNull();
            assertThat(checkAccount.getVersion())
                .isEqualTo(account.getVersion() + 1);
        });
    }

    @Test
    @DisplayName("PUT /partner/account обновление счета")
    void TestUpdateEKSAccount() {
        var updateAccount = step("Подготовка тестовых данных(казначейский счет)", () -> {
            var partner = createValidPartner();
            var account = createValidAccount(partner.getId(), partner.getDigitalId());
            updateAccount(account);
            account.setAccount("03214643000000017300");
            account.getBank().getBankAccount().setBankAccount("40102810545370000003");
            return account;
        });

        var accountVersion = step("Выполнение put-запроса /partner/account, код ответа 200", () -> put(
            baseRoutePath + "/account",
            HttpStatus.OK,
            updateAccount,
            Account.class));

        var checkAccount = step("Выполнение get-запроса /partner/accounts/{digitalId}/{id}, код ответа 200", () -> get(
            baseRoutePath + "/accounts" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            Account.class,
            accountVersion.getDigitalId(), accountVersion.getId()));

        step("Проверка корректности ответа get-запроса", () -> {
            assertThat(checkAccount)
                .isNotNull();
            assertThat(checkAccount.getVersion())
                .isEqualTo(updateAccount.getVersion() + 1);
        });

        var updateAccount1 = step("Подготовка тестовых данных(расчетный счет)", () -> {
            updateAccount(updateAccount);
            updateAccount.setVersion(accountVersion.getVersion() + 1);
            updateAccount.setAccount("40702810600000109222");
            updateAccount.getBank().getBankAccount().setBankAccount("40102810545370000003");
            return updateAccount;
        });

        var accountVersion1 = step("Выполнение put-запроса /partner/account, код ответа 400", () -> put(
            baseRoutePath + "/account",
            HttpStatus.BAD_REQUEST,
            updateAccount1,
            Error.class));

        step("Проверка корректности ответа put-запроса", () -> {
            assertThat(accountVersion1)
                .isNotNull();
            assertThat(accountVersion1.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
        });

        var updateAccount2 = step("Подготовка тестовых данных(изменение банка расчетного счета)", () -> {
            updateAccount(updateAccount1);
            updateAccount1.setAccount("40702810600000009222");
            updateAccount1.setVersion(accountVersion.getVersion() + 1);
            updateAccount1.getBank().setBic("048602001");
            updateAccount1.getBank().getBankAccount().setBankAccount("40102810945370000073");
            return updateAccount1;
        });

        var accountVersion2 = step("Выполнение put-запроса /partner/account, код ответа 400", () -> put(
            baseRoutePath + "/account",
            HttpStatus.BAD_REQUEST,
            updateAccount2,
            Error.class));

        step("Проверка корректности ответа put-запроса", () -> {
            assertThat(accountVersion2)
                .isNotNull();
            assertThat(accountVersion2.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
        });

        var updateAccount3 = step("Подготовка тестовых данных(изменение банка казначейского счета)", () -> {
            updateAccount(updateAccount2);
            updateAccount2.setVersion(accountVersion.getVersion());
            updateAccount2.setAccount("00101643600000010006");
            updateAccount2.getBank().setBic("048602001");
            updateAccount2.getBank().getBankAccount().setBankAccount("40102810945370000073");
            return updateAccount2;
        });

        var accountVersion3 = step("Выполнение put-запроса /partner/account, код ответа 200", () -> put(
            baseRoutePath + "/account",
            HttpStatus.OK,
            updateAccount3,
            Account.class));

        var checkAccount3 = step("Выполнение get-запроса /partner/accounts/{digitalId}/{id}, код ответа 200", () -> get(
            baseRoutePath + "/accounts" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            Account.class,
            accountVersion3.getDigitalId(), accountVersion.getId()));

        step("Проверка корректности ответа get-запроса", () -> {
            assertThat(checkAccount3)
                .isNotNull();
            assertThat(checkAccount3.getVersion())
                .isEqualTo(accountVersion3.getVersion());
        });
    }

    @Test
    @DisplayName("DELETE /partner/accounts/{digitalId} удаление счетов")
    void testDeleteAccount() {
        var account = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });

        var actualAccount = step("Выполнение get-запроса /partner/accounts/{digitalId}/{id}, код ответа 200", () -> get(
            baseRoutePath + "/accounts" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            Account.class,
            account.getDigitalId(), account.getId()));

        step("Проверка корректности ответа get-запроса", () -> {
            assertThat(actualAccount)
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("changeDate")
                .isEqualTo(account);
        });

        var id = "[" + actualAccount.getId().toString() + "]";
        var deleteAccount = step("Выполнение delete-запроса /partner/accounts/{digitalId}, код ответа 204 (удаление счетов)", () -> delete(
            baseRoutePath + "/accounts" + "/{digitalId}" + "?ids=" + id,
            HttpStatus.NO_CONTENT,
            actualAccount.getDigitalId()
        ).getBody());

        step("Проверка корректности ответа delete-запроса", () ->
            assertThat(deleteAccount)
                .isNotNull());

        var searchAccount = step("Выполнение get-запроса /partner/accounts/{digitalId}/{id}, код ответа 404", () -> get(
            baseRoutePath + "/accounts" + "/{digitalId}" + "/{id}",
            HttpStatus.NOT_FOUND,
            Error.class,
            account.getDigitalId(), account.getId()));

        step("Проверка корректности ответа get-запроса", () -> {
            assertThat(searchAccount)
                .isNotNull();
            assertThat(searchAccount.getCode())
                .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
        });
    }

    @Test
    @DisplayName("GET /partner/accounts/{digitalId}/{id} без приоритета и с приоритетом true")
    void testPriorityAccount() {
        var account = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });

        var foundAccount = step("Выполнение get-запроса /partner/accounts/{digitalId}/{id}, код ответа 200", () -> get(
            baseRoutePath + "/accounts" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            Account.class,
            account.getDigitalId(), account.getId()));

        step("Проверка корректности ответа", () -> {
            assertThat(foundAccount)
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("changeDate")
                .isEqualTo(account);
            assertThat(foundAccount.getPriorityAccount())
                .isFalse();
        });

        step("Изменение приоритета на true", () ->
            createValidPriorityAccount(account.getId(), account.getDigitalId()));

        var actualAccount = step("Выполнение get-запроса /partner/accounts/{digitalId}/{id}, код ответа 200", () -> get(
            baseRoutePath + "/accounts" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            Account.class,
            account.getDigitalId(), account.getId()));

        step("Проверка корректности ответа", () -> {
            assertThat(actualAccount)
                .isNotNull();
            assertThat(actualAccount.getPriorityAccount())
                .isTrue();
        });
    }

    @Test
    @DisplayName("PUT /partner/account/priority присвоение priority для 2-х счетов")
    void testPriorityAccountException() {
        var partner = step("Создание партнера",
            (Allure.ThrowableRunnable<Partner>) PartnerControllerTest::createValidPartner);
        var account1 = step("Создание первого счета", () ->
            createValidAccount(partner.getId(), partner.getDigitalId()));
        var account2 = step("Создание второго счета", () ->
            createValidAccount(partner.getId(), partner.getDigitalId()));

        step("Выполнение put-запроса /partner/account/priority, код ответа 200 (для 1 счета)", () ->
            createValidPriorityAccount(account1.getId(), account1.getDigitalId()));
        var error = step("Выполнение put-запроса /partner/account/priority, код ответа 400 (для 2 счета)", () ->
            notCreatePriorityAccount(account2.getId(), account2.getDigitalId()));

        step("Проверка корректности ответа для 2 счета", () -> {
            assertThat(error)
                .isNotNull();
            assertThat(error.getCode())
                .isEqualTo(PRIORITY_ACCOUNT_MORE_ONE.getValue());
        });
    }

    @Test
    @DisplayName("POST /partner/account проверка сохранения счёта с не верным кодом валюты и баланса")
    void testSaveAccountNotValidateCodeCurrencyAndBalance() {
        var account = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner();
            var validAccount = getValidAccount(partner.getId(), partner.getDigitalId());
            validAccount.setAccount("00101643145250000411");
            return validAccount;
        });

        var error = step("Выполнение post-запроса /partner/account, код ответа 400", () -> post(
            baseRoutePath + "/account",
            HttpStatus.BAD_REQUEST,
            account,
            Error.class));

        step("Проверка корректности ответа", () -> {
            assertThat(error)
                .isNotNull();
            assertThat(error.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
            List<String> errorsMessage = error.getDescriptions().stream()
                .map(Descriptions::getMessage)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
            assertThat(errorsMessage)
                .asList()
                .contains("Единый казначейский счёт должен начинаться с 40102");
        });
    }

    @Test
    @DisplayName("PUT /partner/account проверка редактирования счёта с не верным кодом валюты и баланса")
    void testUpdateAccountNotValidateCodeCurrencyAndBalance() {
        var account = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner();
            var validAccount = createValidAccount(partner.getId(), partner.getDigitalId());
            AccountChange accountChange = updateAccount(validAccount);
            accountChange.setAccount("00101643145250000411");
            return accountChange;
        });

        var error = step("Выполнение put-запроса /partner/account, код ответа 400", () -> put(
            baseRoutePath + "/account",
            HttpStatus.BAD_REQUEST,
            account,
            Error.class));

        step("Проверка корректности ответа", () -> {
            assertThat(error)
                .isNotNull();
            assertThat(error.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
            List<String> errorsMessage = error.getDescriptions().stream()
                .map(Descriptions::getMessage)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
            assertThat(errorsMessage)
                .asList()
                .contains("Единый казначейский счёт должен начинаться с 40102");
        });
    }

    @Test
    @DisplayName("POST /partner/account проверка сохранения счёта физического лица с бюджетным корр счётом")
    void testSaveAccountPhysicalPersonNotSetBudgetCorrAccount() {
        var partner = step("Создание партнера Физ лицо", () ->
            post(baseRoutePath, HttpStatus.CREATED, getValidPhysicalPersonPartner(), Partner.class));
        var account = step("Создание тестового счета", () -> {
            var acc = getValidAccount(partner.getId(), partner.getDigitalId());
            acc.getBank().getBankAccount().setBankAccount("40102643145250000411");
            return acc;
        });

        var error = step("Запрос создания счета, невалидный счет", () ->
            post(baseRoutePath + "/account", HttpStatus.BAD_REQUEST, account, Error.class));

        step("Проверка результата", () -> {
            assertThat(error)
                .isNotNull();
            List<String> errorsMessage = error.getDescriptions().stream()
                .map(Descriptions::getMessage)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
            assertThat(errorsMessage)
                .contains(MessagesTranslator.toLocale("validation.partner.bank_account.not_person_or_entrepreneur_entity_format"))
                .contains(MessagesTranslator.toLocale("validation.account.treasure_code_currency"))
                .contains(MessagesTranslator.toLocale("validation.account.treasure_balance"));
        });
    }

    @Test
    @DisplayName("POST /partner/account/get-at-requisites получение счета партнера вместе с партнером")
    void testGetAtRequisites_whenRequestIsCorrect() {
        var partner = step("Создание партнера",
            (Allure.ThrowableRunnable<Partner>) PartnerControllerTest::createValidPartner);
        step("Проливка ИНН партнера в справочник ЖКУ", () ->
            saveGkuInn(partner.getInn()));
        Account account = step("Создание счета", () ->
            createValidAccount(partner.getId(), partner.getDigitalId()));

        step("Создание второго счета", () -> createValidAccount(partner.getId(), partner.getDigitalId())
            .account(account.getAccount())
            .getBank()
            .bic(account.getBank().getBic())
            .bankAccount(account.getBank().getBankAccount()));

        step("Создание третьего счета", () -> createValidAccount(partner.getId(), partner.getDigitalId())
            .account(account.getAccount())
            .getBank()
            .bic(account.getBank().getBic())
            .bankAccount(account.getBank().getBankAccount()));

        var request = step("Подготовка тестовых данных", () ->
            new AccountAndPartnerRequest()
                .digitalId(partner.getDigitalId())
                .account(account.getAccount())
                .bic(account.getBank().getBic())
                .bankAccount(account.getBank().getBankAccount().getBankAccount())
                .inn(partner.getInn())
                .kpp(partner.getKpp())
                .name(partner.getOrgName()));
        step("Очищаем локальный кэш ЖКУ ИНН", () ->
            Objects.requireNonNull(cacheManager.getCache(CacheNames.IS_GKU_INN)).clear());

        List<AccountWithPartnerResponse> accountsWithPartner =
            step("Выполнение post-запроса /partner/account/get-at-requisites",
                () -> post(
                    baseRoutePath + "/account/get-at-requisites",
                    request,
                    new TypeRef<>() {
                    }));

        step("Проверка корректности ответа", () -> {
            assertThat(accountsWithPartner)
                .isNotNull();
            assertThat(accountsWithPartner)
                .size().isEqualTo(1);
            AccountWithPartnerResponse accountWithPartnerActual = accountsWithPartner.get(0);
            assertThat(accountWithPartnerActual.getGku())
                .isTrue();
            assertThat(accountWithPartnerActual.getVersion())
                .isNotNull();
            assertThat(accountWithPartnerActual.getId())
                .isEqualTo(partner.getId());
            assertThat(accountWithPartnerActual.getInn())
                .isEqualTo(partner.getInn());
            assertThat(accountWithPartnerActual.getKpp())
                .isEqualTo(partner.getKpp());
            Account actualAccount = accountWithPartnerActual.getAccount();
            assertThat(actualAccount)
                .isNotNull();
            assertThat(actualAccount.getAccount())
                .isEqualTo(account.getAccount());
        });
    }

    @Test
    @DisplayName("POST /partner/account/get-at-requisites получение бюджетного счета партнера вместе с партнером")
    void testGetAtRequisites_whenBudgetAccount() {
        var partner = step("Создание партнера",
            (Allure.ThrowableRunnable<Partner>) PartnerControllerTest::createValidPartner);
        step("Проливка ИНН партнера в справочник ЖКУ", () ->
            saveGkuInn(partner.getInn()));
        Account account = step("Создание бюджетного счета", () ->
            createValidBudgetAccount(partner.getId(), partner.getDigitalId()));

        var request = step("Подготовка тестовых данных", () ->
            new AccountAndPartnerRequest()
                .digitalId(partner.getDigitalId())
                .account(account.getAccount())
                .bic(account.getBank().getBic())
                .bankAccount(account.getBank().getBankAccount().getBankAccount())
                .inn(partner.getInn())
                .kpp(partner.getKpp())
                .name(partner.getOrgName()));
        step("Очищаем локальный кэш ЖКУ ИНН", () ->
            cacheManager.getCache(CacheNames.IS_GKU_INN).clear());
        List<AccountWithPartnerResponse> accountsWithPartner =
            step("Выполнение post-запроса /partner/account/get-at-requisites", () ->
                post(
                    baseRoutePath + "/account/get-at-requisites",
                    request,
                    new TypeRef<>() {
                    }));
        step("Проверка корректности ответа", () -> {
            assertThat(accountsWithPartner)
                .isNotNull();
            assertThat(accountsWithPartner)
                .size().isEqualTo(1);
            AccountWithPartnerResponse accountWithPartnerActual = accountsWithPartner.get(0);
            assertThat(accountWithPartnerActual.getGku())
                .isTrue();
            assertThat(accountWithPartnerActual.getVersion())
                .isNotNull();
            assertThat(accountWithPartnerActual.getId())
                .isEqualTo(partner.getId());
            assertThat(accountWithPartnerActual.getInn())
                .isEqualTo(partner.getInn());
            assertThat(accountWithPartnerActual.getKpp())
                .isEqualTo(partner.getKpp());
            Account actualAccount = accountWithPartnerActual.getAccount();
            assertThat(actualAccount)
                .isNotNull();
            assertThat(actualAccount.getAccount())
                .isEqualTo(account.getAccount());
            assertThat(actualAccount.getBudget())
                .isTrue();
        });
    }

    @Test
    @DisplayName("POST /partner/account/get-at-requisites получение c партнером")
    void testGetAtRequisites_whenRequestIsCorrect_thenFindPartner() {
        var partner = step("Создание партнера",
            (Allure.ThrowableRunnable<Partner>) PartnerControllerTest::createValidPartner);
        Account account = step("Создание счета", () ->
            createValidAccount(partner.getId(), partner.getDigitalId()));
        var request = step("Подготовка тестовых данных", () ->
            new AccountAndPartnerRequest()
                .digitalId(partner.getDigitalId())
                .account(account.getAccount())
                .bic(getBic())
                .bankAccount(account.getBank().getBankAccount().getBankAccount())
                .inn(partner.getInn())
                .kpp(partner.getKpp())
                .name(partner.getOrgName()));
        List<AccountWithPartnerResponse> accountsWithPartner =
            step("Выполнение post-запроса /partner/account/get-at-requisites",
                () -> post(
                    baseRoutePath + "/account/get-at-requisites",
                    request,
                    new TypeRef<>() {
                    }));
        step("Проверка корректности ответа", () -> {
            assertThat(accountsWithPartner)
                .isNotNull();
            assertThat(accountsWithPartner)
                .size().isEqualTo(1);
            AccountWithPartnerResponse accountWithPartnerActual = accountsWithPartner.get(0);
            assertThat(accountWithPartnerActual.getId())
                .isEqualTo(partner.getId());
            assertThat(accountWithPartnerActual.getInn())
                .isEqualTo(partner.getInn());
            assertThat(accountWithPartnerActual.getKpp())
                .isEqualTo(partner.getKpp());
            assertThat(accountWithPartnerActual.getGku())
                .isFalse();
            assertThat(accountWithPartnerActual.getVersion())
                .isNotNull();
            Account actualAccount = accountWithPartnerActual.getAccount();
            assertThat(actualAccount)
                .isNull();
        });
    }

    @Test
    @DisplayName("POST /partner/account/get-at-requisites получение счета партнера вместе с партнером результат не найден")
    void testGetAtRequisites_whenRequestIsCorrect_thenNotFound() {
        var partner = step("Создание партнера",
            (Allure.ThrowableRunnable<Partner>) PartnerControllerTest::createValidPartner);
        Account account = step("Создание второго счета", () ->
            createValidAccount(partner.getId(), partner.getDigitalId()));

        var request = step("Подготовка тестовых данных", () ->
            new AccountAndPartnerRequest()
                .digitalId(partner.getDigitalId())
                .account(account.getAccount())
                .bic(getBic())
                .bankAccount(account.getBank().getBankAccount().getBankAccount())
                .inn(getValidInnNumber(partner.getLegalForm()))
                .kpp(partner.getKpp())
                .name(partner.getOrgName()));

        Error error = step("Выполнение post-запроса /partner/account/get-at-requisites", () -> post(
            baseRoutePath + "/account/get-at-requisites",
            HttpStatus.NOT_FOUND,
            request,
            Error.class));

        step("Проверка корректности ответа", () -> {
            assertThat(error)
                .isNotNull();
            assertThat(error.getCode())
                .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
        });
    }

    @Test
    @DisplayName("POST /partner/account/get-at-requisites получение счета и партнера когда отсутствует корсчет Банка")
    void testGetAtRequisites_whenBankAccountIsNull() {
        var creatingPartner = step("Подготовка данных о партнере",
            (Allure.ThrowableRunnable<PartnerCreate>) PartnerControllerTest::getValidLegalEntityPartner);

        var partner = step("Создание партнера", () ->
            createValidPartner(creatingPartner));

        Account account = step("Создание счета", () -> {
            var creatingAccount = getValidAccount(partner.getId(), partner.getDigitalId());
            creatingAccount.getBank().setBankAccount(null);
            return createValidAccount(creatingAccount);
        });

        var request = step("Подготовка тестовых данных", () ->
            new AccountAndPartnerRequest()
                .digitalId(partner.getDigitalId())
                .account(account.getAccount())
                .bic(account.getBank().getBic())
                .bankAccount(null)
                .inn(partner.getInn())
                .kpp(partner.getKpp())
                .name(partner.getOrgName()));

        List<AccountWithPartnerResponse> actualAccountWithPartnerList =
            step("Выполнение post-запроса /account/get-at-requisites", () ->
                post(
                    baseRoutePath + "/account/get-at-requisites",
                    request,
                    new TypeRef<>() {
                    }));

        step("Проверка корректности ответа", () -> {
            assertThat(actualAccountWithPartnerList).asList()
                .hasSize(1);
            var actualAccountWithPartner = actualAccountWithPartnerList.get(0);
            assertThat(actualAccountWithPartner.getId())
                .isEqualTo(partner.getId());
            assertThat(actualAccountWithPartner.getInn())
                .isEqualTo(partner.getInn());
            assertThat(actualAccountWithPartner.getKpp())
                .isEqualTo(partner.getKpp());
            Account actualAccount = actualAccountWithPartner.getAccount();
            assertThat(actualAccount)
                .isNotNull();
        });
    }

    @Test
    @DisplayName("POST /partner/account/get-at-all-requisites получение счета и партнера по всем реквизитам запроса " +
        "когда все атрибуты запроса валидные")
    void testGetAtAllRequisites_whenAllAttributesIsValid_thenFindPartner() {
        var partner = step("Создание партнера",
            (Allure.ThrowableRunnable<Partner>) PartnerControllerTest::createValidPartner);
        step("Запись ИНН партнера в справочник ЖКУ", () ->
            saveGkuInn(partner.getInn()));
        Account account = step("Создание счета", () ->
            createValidAccount(partner.getId(), partner.getDigitalId()));

        var request = step("Подготовка тестовых данных", () ->
            new AccountAndPartnerRequest()
                .digitalId(partner.getDigitalId())
                .account(account.getAccount())
                .bic(account.getBank().getBic())
                .bankAccount(account.getBank().getBankAccount().getBankAccount())
                .inn(partner.getInn())
                .kpp(partner.getKpp())
                .name(partner.getOrgName()));

        step("Очищаем локальный кэш ЖКУ ИНН", () ->
            cacheManager.getCache(CacheNames.IS_GKU_INN).clear());

        AccountWithPartnerResponse accountWithPartnerActual =
            step("Выполнение post-запроса /partner/account/get-at-all-requisites", () ->
                post(
                    baseRoutePath + "/account/get-at-all-requisites",
                    request,
                    new TypeRef<>() {
                    }));

        step("Проверка корректности ответа", () -> {
            assertThat(accountWithPartnerActual)
                .isNotNull();
            assertThat(accountWithPartnerActual.getId())
                .isEqualTo(partner.getId());
            assertThat(accountWithPartnerActual.getInn())
                .isEqualTo(partner.getInn());
            assertThat(accountWithPartnerActual.getKpp())
                .isEqualTo(partner.getKpp());
            assertThat(accountWithPartnerActual.getGku())
                .isTrue();
            assertThat(accountWithPartnerActual.getVersion())
                .isNotNull();
            Account actualAccount = accountWithPartnerActual.getAccount();
            assertThat(actualAccount)
                .isNotNull();
        });
    }

    @Test
    @DisplayName("POST /partner/account/get-at-all-requisites получение счета и партнера по всем реквизитам запроса " +
        "когда два партнера имеют одинаковые реквизиты счета но разные имена")
    void testGetAtAllRequisites_whenPartnersHaveIdenticalAccountDetailsAndDifferentOrgName() {
        var creatingPartner = step("Подготовка данных о партнере", () -> {
            var partnerCreate = getValidPhysicalPersonPartner();
            partnerCreate.setInn(null);
            partnerCreate.setKpp(null);
            partnerCreate.setMiddleName(null);
            return partnerCreate;
        });
        var creatingPartnerName = step("Сохранение названия партнере", () ->
            prepareSearchString(creatingPartner.getSecondName(), creatingPartner.getFirstName(), creatingPartner.getMiddleName()));
        var partner1 = step("Создание первого партнера ", () -> createValidPartner(creatingPartner));
        var creatingAccount = step("Подготовка данных о счете партнера", () -> getValidAccount(partner1.getId(), partner1.getDigitalId()));
        var account1 = step("Создание счета для первого партнера", () -> createValidAccount(creatingAccount));
        var partner2 = step("Создание второго партнера", () -> {
            creatingPartner.setMiddleName("middleName");
            return createValidPartner(creatingPartner);
        });
        step("Создание счета для второго партнера", () -> {
            creatingAccount.setPartnerId(partner2.getId());
            createValidAccount(creatingAccount);
        });

        var request = step("Подготовка тестовых данных", () ->
            new AccountAndPartnerRequest()
                .digitalId(partner1.getDigitalId())
                .account(account1.getAccount())
                .bic(account1.getBank().getBic())
                .bankAccount(account1.getBank().getBankAccount().getBankAccount())
                .inn(partner1.getInn())
                .kpp(partner1.getKpp())
                .name(creatingPartnerName));

        step("Очищаем локальный кэш ЖКУ ИНН", () ->
            cacheManager.getCache(CacheNames.IS_GKU_INN).clear());

        AccountWithPartnerResponse accountWithPartnerActual =
            step("Выполнение post-запроса /partner/account/get-at-all-requisites", () ->
                post(
                    baseRoutePath + "/account/get-at-all-requisites",
                    request,
                    new TypeRef<>() {
                    }));

        step("Проверка корректности ответа", () -> {
            assertThat(accountWithPartnerActual)
                .isNotNull();
            assertThat(accountWithPartnerActual.getId())
                .isEqualTo(partner1.getId());
        });
    }


    @Test
    @DisplayName("POST /partner/account/get-at-all-requisites получение счета и партнера по всем реквизитам запроса " +
        "когда бюджетный счет")
    void testGetAtAllRequisites_whenBudgetAccount() {
        var partner = step("Создание партнера",
            (Allure.ThrowableRunnable<Partner>) PartnerControllerTest::createValidPartner);
        step("Проливка ИНН партнера в справочник ЖКУ", () ->
            saveGkuInn(partner.getInn()));
        Account account = step("Создание счета", () ->
            createValidBudgetAccount(partner.getId(), partner.getDigitalId()));

        var request = step("Подготовка тестовых данных", () ->
            new AccountAndPartnerRequest()
                .digitalId(partner.getDigitalId())
                .account(account.getAccount())
                .bic(account.getBank().getBic())
                .bankAccount(account.getBank().getBankAccount().getBankAccount())
                .inn(partner.getInn())
                .kpp(partner.getKpp())
                .name(partner.getOrgName())
        );

        step("Очищаем локальный кэш ЖКУ ИНН", () -> cacheManager.getCache(CacheNames.IS_GKU_INN).clear());
        AccountWithPartnerResponse accountWithPartnerActual =
            step("Выполнение post-запроса /partner/account/get-at-all-requisites", () ->
                post(
                    baseRoutePath + "/account/get-at-all-requisites",
                    request,
                    new TypeRef<>() {
                    }));

        step("Проверка корректности ответа", () -> {
            assertThat(accountWithPartnerActual)
                .isNotNull();
            assertThat(accountWithPartnerActual.getId())
                .isEqualTo(partner.getId());
            assertThat(accountWithPartnerActual.getInn())
                .isEqualTo(partner.getInn());
            assertThat(accountWithPartnerActual.getKpp())
                .isEqualTo(partner.getKpp());
            assertThat(accountWithPartnerActual.getGku())
                .isTrue();
            assertThat(accountWithPartnerActual.getVersion())
                .isNotNull();
            Account actualAccount = accountWithPartnerActual.getAccount();
            assertThat(actualAccount)
                .isNotNull();
            assertThat(actualAccount.getBudget())
                .isTrue();
        });
    }

    @Test
    @DisplayName("POST /partner/account/get-at-all-requisites получение только партнера по всем реквизитам запроса")
    void testGetAtAllRequisites_whenOnlyPartner() {
        var partner = step("Создание партнера",
            (Allure.ThrowableRunnable<Partner>) PartnerControllerTest::createValidPartner);
        AccountCreate account = step("Поулчение счета", () ->
            getValidBudgetAccount(partner.getId(), partner.getDigitalId()));

        var request = step("Подготовка тестовых данных", () ->
            new AccountAndPartnerRequest()
                .digitalId(partner.getDigitalId())
                .account(account.getAccount())
                .bic(account.getBank().getBic())
                .bankAccount(account.getBank().getBankAccount().getBankAccount())
                .inn(partner.getInn())
                .kpp(partner.getKpp())
                .name(partner.getOrgName()));

        AccountWithPartnerResponse accountWithPartnerActual =
            step("Выполнение post-запроса /partner/account/get-at-all-requisites", () ->
                post(
                    baseRoutePath + "/account/get-at-all-requisites",
                    request,
                    new TypeRef<>() {
                    }));

        step("Проверка корректности ответа", () -> {
            assertThat(accountWithPartnerActual)
                .isNotNull();
            assertThat(accountWithPartnerActual.getId())
                .isEqualTo(partner.getId());
            assertThat(accountWithPartnerActual.getInn())
                .isEqualTo(partner.getInn());
            assertThat(accountWithPartnerActual.getKpp())
                .isEqualTo(partner.getKpp());
            assertThat(accountWithPartnerActual.getVersion())
                .isNotNull();
        });
    }

    @ParameterizedTest
    @ArgumentsSource(GetAtAllRequisitesThenFindPartnerArgumentProvider.class)
    @DisplayName("POST /partner/account/get-at-all-requisites получение счета и партнера по всем реквизитам запроса. " +
        "КПП не определен у контрагента и не задан в запросе")
    void testGetAtAllRequisites_thenFindPartner(String dbKppValue, String requestKppValue) {
        var creatingPartner = step("Подготовка данных о партнере", () ->
            getValidLegalEntityPartner().kpp(dbKppValue));
        var partner = step("Создание партнера", () ->
            createValidPartner(creatingPartner));
        Account account = step("Создание счета", () ->
            createValidAccount(partner.getId(), partner.getDigitalId()));

        var request = step("Подготовка тестовых данных", () ->
            new AccountAndPartnerRequest()
                .digitalId(partner.getDigitalId())
                .account(account.getAccount())
                .bic(account.getBank().getBic())
                .bankAccount(account.getBank().getBankAccount().getBankAccount())
                .inn(partner.getInn())
                .kpp(requestKppValue)
                .name(partner.getOrgName()));

        AccountWithPartnerResponse accountWithPartnerActual =
            step("Выполнение post-запроса /partner/account/get-at-all-requisites", () ->
                post(
                    baseRoutePath + "/account/get-at-all-requisites",
                    request,
                    new TypeRef<>() {
                    }));

        step("Проверка корректности ответа", () -> {
            assertThat(accountWithPartnerActual)
                .isNotNull();
            assertThat(accountWithPartnerActual.getId())
                .isEqualTo(partner.getId());
            assertThat(accountWithPartnerActual.getInn())
                .isEqualTo(partner.getInn());
            assertThat(accountWithPartnerActual.getGku())
                .isFalse();
            assertThat(accountWithPartnerActual.getVersion())
                .isNotNull();
            Account actualAccount = accountWithPartnerActual.getAccount();
            assertThat(actualAccount)
                .isNotNull();
            if (dbKppValue == null) {
                assertThat(accountWithPartnerActual.getKpp())
                    .isNull();
            } else {
                assertThat(accountWithPartnerActual.getKpp())
                    .isEmpty();
            }
        });
    }

    @ParameterizedTest
    @ArgumentsSource(GetAtAllRequisitesThenNotFoundExceptionArgumentsProvider.class)
    @DisplayName("POST /partner/account/get-at-all-requisites получение счета и партнера по всем реквизитам запроса. " +
        "КПП определен у контрагента и не задан в запросе")
    void testGetAtAllRequisites_thenNotFindPartner(AccountAndPartnerRequest request) {
        Error error = step("Выполнение post-запроса /partner/account/get-at-all-requisites", () ->
            post(
                baseRoutePath + "/account/get-at-all-requisites",
                HttpStatus.NOT_FOUND,
                request,
                Error.class));

        step("Проверка корректности ответа", () -> {
            assertThat(error)
                .isNotNull();
            assertThat(error.getCode())
                .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
        });
    }

    @Test
    @DisplayName("POST /partner/account/get-at-all-requisites получение счета и партнера когда отсутствует корсчет Банка")
    void testGetAtAllRequisites_whenBankAccountIsNull() {
        var creatingPartner = step("Подготовка данных о партнере",
            (Allure.ThrowableRunnable<PartnerCreate>) PartnerControllerTest::getValidLegalEntityPartner);
        var partner = step("Создание партнера", () ->
            createValidPartner(creatingPartner));
        Account account = step("Создание счета", () -> {
            var creatingAccount = getValidAccount(partner.getId(), partner.getDigitalId());
            creatingAccount.getBank().setBankAccount(null);
            return createValidAccount(creatingAccount);
        });

        var request = step("Подготовка тестовых данных", () ->
            new AccountAndPartnerRequest()
                .digitalId(partner.getDigitalId())
                .account(account.getAccount())
                .bic(account.getBank().getBic())
                .bankAccount(null)
                .inn(partner.getInn())
                .kpp(partner.getKpp())
                .name(partner.getOrgName())
        );

        AccountWithPartnerResponse accountWithPartnerActual =
            step("Выполнение post-запроса /account/get-at-all-requisites", () ->
                post(
                    baseRoutePath + "/account/get-at-all-requisites",
                    request,
                    new TypeRef<>() {
                    }));

        step("Проверка корректности ответа", () -> {
            assertThat(accountWithPartnerActual)
                .isNotNull();
            assertThat(accountWithPartnerActual.getId())
                .isEqualTo(partner.getId());
            assertThat(accountWithPartnerActual.getInn())
                .isEqualTo(partner.getInn());
            assertThat(accountWithPartnerActual.getKpp())
                .isEqualTo(partner.getKpp());
            Account actualAccount = accountWithPartnerActual.getAccount();
            assertThat(actualAccount)
                .isNotNull();
        });
    }

    @Test
    @DisplayName("GET /partner/accounts/get-accountIds-by-externalIds/{digitalId} внутреннего идентификатора счета по внешнему идентификатору")
    void testGetAccountIdsByExternalIds() {
        var account = step("Подготовка тестовых данных о связи внешнего и внутреннего идентификатора", () -> {
            var partner = createValidPartner();
            return createValidAccount(getValidAccount(partner.getId(), partner.getDigitalId()));
        });
        var externalIds = step("Подготовка тестовых данных - создание externalIds", () ->
            List.of(
                account.getId(),
                UUID.randomUUID(),
                UUID.randomUUID()));

        var expectedResponse = step("Подготовка ожидаемого ответа", () ->
            new ExternalInternalIdLinksResponse()
                .idLinks(externalIds.stream()
                    .map(externalId ->
                        new ExternalInternalIdLink()
                            .externalId(externalId)
                            .internalId(
                                externalId.equals(account.getId())
                                    ? account.getId()
                                    : null))
                    .collect(Collectors.toList())));

        var actualResponse =
            step("Выполнение post-запроса /partner/accounts/get-accountIds-by-externalIds", () ->
                get(
                    baseRoutePath + "/accounts/get-accountIds-by-externalIds" + "/{digitalId}",
                    HttpStatus.OK,
                    ExternalInternalIdLinksResponse.class,
                    Map.of("externalIds", externalIds),
                    account.getDigitalId()));

        step("Проверка корректности ответа", () ->
            assertThat(actualResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse));
    }

    @Test
    @DisplayName("GET /partner/accounts/get-accountIds-by-externalIds/{digitalId} внутреннего идентификатора счета по внешнему идентификатору. Невалидный запрос")
    void testGetAccountIdsByExternalIds_whenInvalidRequest() {
        var externalIds = step("Подготовка тестовых данных - создание externalIds", () ->
            List.of(RandomStringUtils.randomAlphabetic(10)));

        var actualResponse =
            step("Выполнение post-запроса /partner/accounts/get-accountIds-by-externalIds", () ->
                get(
                    baseRoutePath + "/accounts/get-accountIds-by-externalIds" + "/{digitalId}",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    Error.class,
                    Map.of("externalIds", externalIds),
                    RandomStringUtils.randomAlphabetic(10)));

        step("Проверка результата", () -> {
            assertThat(actualResponse)
                .isNotNull();
            assertThat(actualResponse.getMessage())
                .contains("Invalid UUID string: " + externalIds.get(0));
        });
    }

    @Test
    @DisplayName("POST /partner/account  Создание счета партнера. Невалидный запрос. Дубль ExternalId")
    void testCreateAccountDuplicateExternalId() {
        var account = step("Подготовка тестовых данных", () -> {
            var partner = createValidPartner();
            var validAccount = getValidAccount(partner.getId(), partner.getDigitalId());
            validAccount.setExternalId(UUID.randomUUID());
            return createValidAccount(validAccount);
        });

        var error = step("Выполнение post-запроса /partner/accounts, дубль externalId", () -> {
            var acc = getValidAccount(account.getPartnerId(), account.getDigitalId());
            var externalId = account.getExternalIds().stream()
                .findFirst()
                .orElseGet(UUID::randomUUID);
            acc.setExternalId(externalId);
                return post(
                    baseRoutePath + "/account",
                    HttpStatus.BAD_REQUEST,
                    acc,
                    Error.class
                );
            });

        step("Проверка результата", () -> {
            assertThat(error)
                .isNotNull();
            assertThat(error.getCode())
                .isEqualTo(EXTERNAL_ID_DUPLICATE_EXCEPTION.getValue());
            assertThat(error.getMessage())
                .isEqualTo(MessagesTranslator.toLocale("external_id.duplicate", account.getExternalIds().stream()
                    .findFirst()
                    .orElse(UUID.randomUUID()))
                );
        });
    }

    private GkuInnEntity saveGkuInn(String inn) {
        if (!org.springframework.util.StringUtils.hasText(inn)) {
            return null;
        }
        gkuInnEntity = new GkuInnEntity();
        gkuInnEntity.setInn(inn);
        return innDictionaryRepository.save(gkuInnEntity);
    }
}
