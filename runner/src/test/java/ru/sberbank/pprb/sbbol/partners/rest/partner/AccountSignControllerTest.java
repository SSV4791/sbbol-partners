package ru.sberbank.pprb.sbbol.partners.rest.partner;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import ru.sberbank.pprb.sbbol.partners.exception.OptimisticLockException;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignDetail;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignInfo;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignInfoRequisites;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignInfoRequisitesResponse;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfo;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfoResponse;
import ru.sberbank.pprb.sbbol.partners.model.Descriptions;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.FraudMetaData;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.rest.config.SbbolIntegrationWithOutSbbolConfiguration;
import ru.sberbank.pprb.sbbol.partners.service.partner.AccountSignService;

import java.util.List;
import java.util.Map;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.ACCOUNT_ALREADY_SIGNED_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.MODEL_NOT_FOUND_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.MODEL_VALIDATION_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.model.Error.TypeEnum.BUSINESS;
import static ru.sberbank.pprb.sbbol.partners.model.SignType.NOT_SIGNED;
import static ru.sberbank.pprb.sbbol.partners.model.SignType.SIGNED;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountControllerTest.createValidAccount;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.BaseAccountControllerTest.getValidAccount;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

@ContextConfiguration(classes = SbbolIntegrationWithOutSbbolConfiguration.class)
class AccountSignControllerTest extends BaseAccountSignControllerTest {
    @Autowired
    AccountSignService accountSignService;

    @Test
    @DisplayName("GET /partner/accounts/{digitalId}/{id} Подписание счёта")
    void testCreateSignAccount() {
        var account = step("Создание счёта для партнера", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });

        var savedSign = step("Подписание счёта", () ->
            createValidAccountsSign(
                account.getDigitalId(),
                account.getId(),
                account.getVersion(),
                getBase64FraudMetaData()));

        var getAccount = step("Выполнение get-запроса /partner/accounts/{digitalId}/{id}, код ответа 200", () ->
            get(
                "/partner" + "/accounts" + "/{digitalId}" + "/{id}",
                HttpStatus.OK,
                Account.class,
                account.getDigitalId(), account.getId()
            ));

        step("Проверка корректности ответа", () -> {
            assertThat(savedSign)
                .isNotNull();
            assertThat(getAccount.getState())
                .isEqualTo(SIGNED);
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/sign/info Проверка подписания счёта по реквизитам. Счета не подписаны")
    void testGetSignByRequisites() {
        var account = step("Создание счётов с одинаковыми реквизитами для партнеров", () -> {
            var partner = createValidPartner();
            var acc = getValidAccount(partner.getId(), partner.getDigitalId());
            var partner2 = createValidPartner(partner.getDigitalId());
            var acc2 = getValidAccount(partner2.getId(), partner2.getDigitalId());
            acc2.account(acc.getAccount());
            acc2.getBank().bic(acc.getBank().getBic());
            createValidAccount(acc2);
            return createValidAccount(acc);
        });

        var response = step("Выполнение запроса. Оба счета не подписаны", () -> {
            var request = new AccountSignInfoRequisites()
                .digitalId(account.getDigitalId())
                .account(account.getAccount())
                .bic(account.getBank().getBic());
            return post(
                baseRoutePath + "/info",
                HttpStatus.OK,
                request,
                AccountSignInfoRequisitesResponse.class);
        });
        step("Проверка результата", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getStatus())
                .isEqualTo(NOT_SIGNED);
            assertThat(response.getAccountId())
                .isNotNull();
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/sign/info Проверка подписания счёта по реквизитам. Счет не найден")
    void testGetSignByRequisitesAccountNotFound() {
        var response = step("Запрос с несуществующими реквизитами", () -> {
            var request = new AccountSignInfoRequisites()
                .digitalId(RandomStringUtils.randomAlphabetic(10))
                .account(RandomStringUtils.randomNumeric(20))
                .bic(RandomStringUtils.randomNumeric(9));
            return post(
                baseRoutePath + "/info",
                HttpStatus.NOT_FOUND,
                request,
                Error.class);
        });
        step("Проверка результата", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getType())
                .isEqualTo(BUSINESS);
            assertThat(response.getCode())
                .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/sign/info Проверка подписания счёта по реквизитам. Один счет подписан")
    void testGetSignByRequisites2() {
        var account = step("Создание счётов с одинаковыми реквизитами для партнеров", () -> {
            var partner = createValidPartner();
            var acc = getValidAccount(partner.getId(), partner.getDigitalId());
            var partner2 = createValidPartner(partner.getDigitalId());
            var acc2 = getValidAccount(partner2.getId(), partner2.getDigitalId());
            acc2.account(acc.getAccount());
            acc2.getBank().bic(acc.getBank().getBic());
            createValidAccount(acc2);
            return createValidAccount(acc);
        });

        step("Подписание счёта", () ->
            createValidAccountsSign(
                account.getDigitalId(),
                account.getId(),
                account.getVersion(),
                getBase64FraudMetaData()));

        var response3 = step("Выполнение запроса. Один счет подписан", () -> {
            var request = new AccountSignInfoRequisites()
                .digitalId(account.getDigitalId())
                .account(account.getAccount())
                .bic(account.getBank().getBic());
            return post(
                baseRoutePath + "/info",
                HttpStatus.OK,
                request,
                AccountSignInfoRequisitesResponse.class);
        });
        step("Проверка результата", () -> {
            assertThat(response3)
                .isNotNull();
            assertThat(response3.getStatus())
                .isEqualTo(SIGNED);
            assertThat(response3.getAccountId())
                .isEqualTo(account.getId());
        });
    }


    @Test
    @DisplayName("POST /partner/accounts/sign Ошибка валидации FraudMetaData")
    void testCreateSignAccount_whenInvalidFraudMetaData() {
        var account = step("Создание счёта для партнера", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });

        var errorSign = step("Первое подписание счёта", () ->
            createInvalidAccountsSignWithInvalidFraudMetaData(
                account.getDigitalId(),
                account.getId(),
                account.getVersion(),
                getBase64InvalidFraudMetaData()));

        step("Проверка корректности ответа", () -> {
            assertThat(errorSign)
                .isNotNull();
            assertThat(errorSign.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
            assertThat(errorSign.getDescriptions())
                .hasSize(1);
            assertThat(errorSign.getDescriptions().get(0).getField())
                .isEqualTo("fraudMetaData.clientData.digitalId");
            assertThat(errorSign.getDescriptions().get(0).getMessage())
                .contains("Поле обязательно для заполнения");
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/sign Повторное подписание счёта")
    void testCreateDuplicateSignAccount() throws JsonProcessingException {
        var account = step("Создание счёта для партнера", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });

        var base64FraudMetaData = getBase64FraudMetaData();
        var savedSign = step("Первое подписание счёта", () ->
            createValidAccountsSign(
                account.getDigitalId(),
                account.getId(),
                account.getVersion(),
                base64FraudMetaData));

        var savedDuplicateSign = step("Повторное подписание счёта", () ->
            createAccountSignWithBadRequest(
                account.getDigitalId(),
                account.getId(),
                account.getVersion(),
                base64FraudMetaData));

        List<Descriptions> errorText = step("Подготовка текста ошибки для проверки", () ->
            List.of(
                new Descriptions()
                    .field("account")
                    .message(
                        List.of("Ошибка обновления счёта: " + account.getAccount() +
                            " запрещено повторно подписывать или изменять подписанные счета"))));

        step("Проверка корректности ответа", () -> {
            assertThat(savedSign)
                .isNotNull();
            assertThat(savedDuplicateSign)
                .isNotNull();
            assertThat(savedDuplicateSign.getCode())
                .isEqualTo(ACCOUNT_ALREADY_SIGNED_EXCEPTION.getValue());
            for (var text : savedDuplicateSign.getDescriptions()) {
                assertThat(errorText).contains(text);
            }
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/sign Попытка подписания счёта неверной версии")
    void testCreateSignAccountInvalidVersion() {
        var account = step("Создание счёта для партнера", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });

        var accountsSignInfo = step("Создание AccountsSignInfo для запроса", () -> {
            var digitalId = account.getDigitalId();
            var accountId = account.getId();
            var actualVersion = account.getVersion();
            var invalidVersion = actualVersion + 1;
            var accountSignDetail = podamFactory.manufacturePojo(AccountSignDetail.class);
            accountSignDetail.accountVersion(invalidVersion);
            accountSignDetail.accountId(accountId);
            var signInfo = podamFactory.manufacturePojo(AccountsSignInfo.class);
            signInfo.digitalId(digitalId);
            signInfo.accountsSignDetail(List.of(accountSignDetail));
            return signInfo;
        });

        var fraudMetaData = step("Создание FraudMetaData для запроса", () ->
            podamFactory.manufacturePojo(FraudMetaData.class));

        step("Проверка корректности ответа", () ->
            assertThrows(OptimisticLockException.class, () ->
                accountSignService.createAccountsSign(accountsSignInfo, fraudMetaData)));
    }

    @Test
    @DisplayName("POST /partner/accounts/sign Попытка подписания счёта без DigitalId")
    void testCreateSignAccountWithoutDigitalId() {
        var account = step("Создание счёта для партнера", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });
        var errorText = step("Подготовка текста ошибки", () ->
            "Искомая сущность account с id: " + account.getId() + ", digitalId:  не найдена");
        var response = step("Попытка подписания счёта без DigitalId", () ->
            createAccountSignWithNotFound("", account.getId(), account.getVersion(), getBase64FraudMetaData()));

        step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getCode())
                .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
            assertThat(response.getMessage())
                .isEqualTo(errorText);
        });
    }

    @Test
    @DisplayName("GET /partner/accounts/sign/{digitalId}/{accountId} Получение подписанного счёта")
    void testGetSignAccount() {
        var account = step("Создание счёта для партнера", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });

        step("Выполнение get-запроса /partner/accounts/sign/{digitalId}/{accountId}, код ответа 404", () -> get(
            baseRoutePath + "/{digitalId}" + "/{accountId}",
            HttpStatus.NOT_FOUND,
            Error.class,
            account.getDigitalId(), account.getId()));

        var savedSign = step("Выполнение post-запроса /partner/accounts/sign, код ответа 200", () -> post(
            baseRoutePath,
            HttpStatus.OK,
            getValidAccountsSign(account.getDigitalId(), account.getId(), account.getVersion()),
            getFraudMetaDataHeaders(getBase64FraudMetaData()),
            AccountsSignInfoResponse.class));

        step("Проверка корректности ответа", () ->
            assertThat(savedSign)
                .isNotNull());

        var signInfo = step("Выполнение get-запроса /partner/accounts/sign/{digitalId}/{accountId}, код ответа 200", () -> get(
            baseRoutePath + "/{digitalId}" + "/{accountId}",
            HttpStatus.OK,
            AccountSignInfo.class,
            account.getDigitalId(), account.getId()));

        step("Проверка корректности ответа", () -> {
            assertThat(signInfo)
                .isNotNull();
            assertThat(signInfo.getDigitalId())
                .isEqualTo(savedSign.getDigitalId());
        });
    }

    @Test
    @DisplayName("GET /partner/accounts/sign/{digitalId}/{accountId} Попытка получения подписанного счёта которого нет")
    void testGetWhenNotFoundSignAccount() {
        var partner = step("Создание партнера",
            (Allure.ThrowableRunnable<Partner>) PartnerControllerTest::createValidPartner);
        var account = step("Создание счёта для партнера",
            () -> createValidAccount(partner.getId(), partner.getDigitalId()));
        var savedSign = step("Выполнение post-запроса /partner/accounts/sign, код ответа 200", () -> post(
            baseRoutePath,
            HttpStatus.OK,
            getValidAccountsSign(account.getDigitalId(), account.getId(), account.getVersion()),
            getFraudMetaDataHeaders(getBase64FraudMetaData()),
            AccountsSignInfoResponse.class));

        step("Проверка корректности ответа", () ->
            assertThat(savedSign)
                .isNotNull());

        var notSignedAccount = step("Создание не подписанного счёта для партнера", () ->
            createValidAccount(partner.getId(), partner.getDigitalId()));

        var signInfo = step("Выполнение get-запроса /partner/accounts/sign/{digitalId}/{accountId}, код ответа 404", () -> get(
            baseRoutePath + "/{digitalId}" + "/{accountId}",
            HttpStatus.NOT_FOUND,
            Error.class,
            notSignedAccount.getDigitalId(), notSignedAccount.getId()));

        var errorText = step("Подготовка текста ошибки", () ->
            "Искомая сущность sign с id: " + notSignedAccount.getId() + ", digitalId: " + notSignedAccount.getDigitalId() + " не найдена");

        step("Проверка корректности ответа", () -> {
            assertThat(signInfo)
                .isNotNull();
            assertThat(signInfo.getCode())
                .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
            assertThat(signInfo.getMessage())
                .isEqualTo(errorText);
        });
    }

    @Test
    @DisplayName("GET /partner/accounts/sign/{digitalId}/{accountId} Попытка получения подписанного счёта без DigitalId")
    void testGetSignAccountWithoutDigitalId() {
        var account = step("Создание счёта для партнера", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });

        var savedSign = step("Выполнение post-запроса /partner/accounts/sign/{digitalId}/{accountId}, код ответа 200", () -> post(
            baseRoutePath,
            HttpStatus.OK,
            getValidAccountsSign(account.getDigitalId(), account.getId(), account.getVersion()),
            getFraudMetaDataHeaders(getBase64FraudMetaData()),
            AccountsSignInfoResponse.class));

        step("Проверка корректности ответа", () ->
            assertThat(savedSign)
                .isNotNull());

        var signInfo = step("Выполнение get-запроса /partner/accounts/sign/{digitalId}/{accountId}, код ответа 404", () -> get(
            baseRoutePath + "/{digitalId}" + "/{accountId}",
            HttpStatus.NOT_FOUND,
            Error.class,
            "", account.getId()));

        var errorText = step("Подготовка текста ошибки", () ->
            "Искомая сущность account с id: " + account.getId() + ", digitalId: sign не найдена");

        step("Проверка корректности ответа", () -> {
            assertThat(signInfo)
                .isNotNull();
            assertThat(signInfo.getCode())
                .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
            assertThat(signInfo.getMessage())
                .isEqualTo(errorText);
        });
    }

    @Test
    @DisplayName("GET /partner/accounts/sign/{digitalId}/{accountId} Попытка получения подписанного счёта без AccountId")
    void testGetSignAccountWithoutAccountId() {
        var account = step("Создание счёта для партнера", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });

        var savedSign = step("Выполнение post-запроса /partner/accounts/sign/{digitalId}/{accountId}, код ответа 200", () -> post(
            baseRoutePath,
            HttpStatus.OK,
            getValidAccountsSign(account.getDigitalId(), account.getId(), account.getVersion()),
            getFraudMetaDataHeaders(getBase64FraudMetaData()),
            AccountsSignInfoResponse.class));

        step("Проверка корректности ответа", () ->
            assertThat(savedSign)
                .isNotNull());

        var signInfo = step("Выполнение get-запроса /partner/accounts/sign/{digitalId}/{accountId}, код ответа 500", () -> get(
            baseRoutePath + "/{digitalId}" + "/{accountId}",
            HttpStatus.INTERNAL_SERVER_ERROR,
            Error.class,
            account.getDigitalId(), ""));

        step("Проверка корректности ответа", () ->
            assertThat(signInfo)
                .isNotNull());

        var errorText = step("Подготовка текста ошибки", () ->
            "Invalid UUID string: " + account.getDigitalId());

        step("Проверка корректности ответа", () -> {
            assertThat(signInfo.getCode())
                .isEqualTo(EXCEPTION.getValue());
            assertThat(signInfo.getMessage())
                .contains(errorText);
        });
    }

    @Test
    @DisplayName("GET /partner/accounts/sign/{digitalId}/{accountId} Попытка получения подписанного счёта без AccountId и без DigitalId")
    void testGetSignAccountWithoutDigitalIdAndAccountId() {
        var account = step("Создание счёта для партнера", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });

        var savedSign = step("Выполнение post-запроса /partner/accounts/sign/{digitalId}/{accountId}, код ответа 200", () -> post(
            baseRoutePath,
            HttpStatus.OK,
            getValidAccountsSign(account.getDigitalId(), account.getId(), account.getVersion()),
            getFraudMetaDataHeaders(getBase64FraudMetaData()),
            AccountsSignInfoResponse.class));

        step("Проверка корректности ответа", () -> {
            assertThat(savedSign)
                .isNotNull();
        });

        var signInfo = step("Выполнение get-запроса /partner/accounts/sign/{digitalId}/{accountId}, код ответа 405", () -> get(
            baseRoutePath + "/{digitalId}" + "/{accountId}",
            HttpStatus.METHOD_NOT_ALLOWED,
            Error.class,
            "", ""));

        var errorText = step("Подготовка текста ошибки", () ->
            "Request method 'GET' not supported");

        step("Проверка корректности ответа", () -> {
            assertThat(signInfo)
                .isNotNull();
            assertThat(signInfo.getMessage())
                .isEqualTo(errorText);
        });
    }

    @Test
    @DisplayName("DELETE /partner/accounts/sign/{digitalId} Удаление подписи счёта")
    void testDeleteSignAccount() {
        var account = step("Создание счёта для партнера", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });

        var savedSign = step("Создание подписанного счёта", () ->
            createValidAccountsSign(account.getDigitalId(), account.getId(), account.getVersion(), getBase64FraudMetaData()));
        var accountSignDetail = step("Получение деталей подписи", () ->
            savedSign.getAccountsSignDetail().get(0));

        var actualAccountSign = step("Выполнение get-запроса /partner/accounts/sign/{digitalId}/{accountId}, код ответа 200", () -> get(
            baseRoutePath + "/{digitalId}" + "/{accountId}",
            HttpStatus.OK,
            AccountSignInfo.class,
            savedSign.getDigitalId(), accountSignDetail.getAccountId()));

        step("Проверка корректности подписи", () -> {
            assertThat(actualAccountSign)
                .isNotNull();
            assertThat(actualAccountSign.getAccountSignDetail())
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("dateTimeOfSign")
                .isEqualTo(accountSignDetail);
        });

        var deleteAccountSign = step("Выполнение delete-запроса /partner/accounts/sign/{digitalId}, код ответа 204 (удаление подписи)", () -> delete(
            baseRoutePath + "/{digitalId}",
            HttpStatus.NO_CONTENT,
            Map.of("accountIds", accountSignDetail.getAccountId()),
            savedSign.getDigitalId()
        ).getBody());

        step("Проверка корректности запроса", () ->
            assertThat(deleteAccountSign)
                .isNotNull());

        var searchAccountSign = step("Выполнение get-запроса /partner/accounts/sign/{digitalId}/{accountId}, код ответа 404", () -> get(
            baseRoutePath + "/{digitalId}" + "/{accountId}",
            HttpStatus.NOT_FOUND,
            Error.class,
            savedSign.getDigitalId(), accountSignDetail.getAccountId()));

        step("Проверка корректности запроса", () -> {
            assertThat(searchAccountSign)
                .isNotNull();
            assertThat(searchAccountSign.getCode())
                .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
        });
    }

    @Test
    @DisplayName("DELETE /partner/accounts/sign/{digitalId} Попытка удаления не сущестущей подписи счёта")
    void testDeleteNotExistedSignAccount() {
        var partner = step("Создание партнера",
            (Allure.ThrowableRunnable<Partner>) PartnerControllerTest::createValidPartner);
        var account = step("Создание счёта для партнера",
            () -> createValidAccount(partner.getId(), partner.getDigitalId()));

        step("Подписание счёта партнера", () ->
            createValidAccountsSign(account.getDigitalId(), account.getId(), account.getVersion(), getBase64FraudMetaData()));

        var accountWithoutSign = step("Создание счёта без подписи", () ->
            createValidAccount(partner.getId(), partner.getDigitalId()));

        var deleteAccountSign = step("Выполнение delete-запроса /partner/accounts/sign/{digitalId}, код ответа 204 (удаление подписи)", () -> delete(
            baseRoutePath + "/{digitalId}",
            HttpStatus.NO_CONTENT,
            Map.of("accountIds", accountWithoutSign.getId()),
            accountWithoutSign.getDigitalId()
        ).getBody());

        step("Проверка корректности запроса", () ->
            assertThat(deleteAccountSign)
                .isNotNull());
    }
}
