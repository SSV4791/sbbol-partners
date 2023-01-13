package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.qameta.allure.Allure;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.ACCOUNT_ALREADY_SIGNED_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.MODEL_VALIDATION_EXCEPTION;

import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignInfo;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfoResponse;
import ru.sberbank.pprb.sbbol.partners.model.Descriptions;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.FraudMetaData;
import ru.sberbank.pprb.sbbol.partners.model.SignType;
import ru.sberbank.pprb.sbbol.partners.rest.config.SbbolIntegrationWithOutSbbolConfiguration;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.MODEL_NOT_FOUND_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountControllerTest.createValidAccount;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

@ContextConfiguration(classes = SbbolIntegrationWithOutSbbolConfiguration.class)
public class AccountSignControllerTest extends BaseAccountSignControllerTest {

    @Test
    @DisplayName("GET /partner/accounts/{digitalId}/{id} Подписание счёта")
    void testCreateSignAccount() {
        var account = Allure.step("Создание счёта для партнера", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });
        var savedSign = Allure.step("Подписание счёта", () -> {
            return createValidAccountsSign(account.getDigitalId(), account.getId(), podamFactory.manufacturePojo(FraudMetaData.class));
        });
        var getAccount = Allure.step("Выполнение get-запроса /partner/accounts/{digitalId}/{id}, код ответа 200", () -> get(
            "/partner" + "/accounts" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            Account.class,
            account.getDigitalId(), account.getId()
        ));
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(savedSign)
                .isNotNull();
            assertThat(getAccount.getState())
                .isEqualTo(SignType.SIGNED);
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/sign Ошибка валидации FraudMetaData")
    void testCreateSignAccount_whenInvalidFraudMetaData() {
        var account = Allure.step("Создание счёта для партнера", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });
        var errorSign = Allure.step("Первое подписание счёта", () -> {
            var invalidFraudMetaData = podamFactory.manufacturePojo(FraudMetaData.class);
            invalidFraudMetaData.getClientData().setTerBankNumber(null);
            return createInvalidAccountsSignWithInvalidFraudMetaData(account.getDigitalId(), account.getId(), invalidFraudMetaData);
        });
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(errorSign)
                .isNotNull();
            assertThat(errorSign.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
            assertThat(errorSign.getDescriptions())
                .hasSize(1);
            assertThat(errorSign.getDescriptions().get(0).getField())
                .isEqualTo("fraudMetaData.clientData.terBankNumber");
            assertThat(errorSign.getDescriptions().get(0).getMessage())
                .contains("Поле обязательно для заполнения");
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/sign Повторное подписание счёта")
    void testCreateDuplicateSignAccount() {
        var account = Allure.step("Создание счёта для партнера", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });
        var fraudMetaData = podamFactory.manufacturePojo(FraudMetaData.class);
        var savedSign = Allure.step("Первое подписание счёта", () -> {
            return createValidAccountsSign(account.getDigitalId(), account.getId(), fraudMetaData);
        });
        var savedDuplicateSign = Allure.step("Повторное подписание счёта", () -> {
            return createAccountSignWithBadRequest(account.getDigitalId(), account.getId(), fraudMetaData);
        });
        List<Descriptions> errorText = Allure.step("Подготовка текста ошибки для проверки", () -> {
            return List.of(
                new Descriptions()
                    .field("account")
                    .message(
                        List.of("Ошибка обновления счёта: " + account.getAccount() + " запрещено повторно подписывать или изменять подписанные счета")
                    ));
        });
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(savedSign)
                .isNotNull();
            assertThat(savedDuplicateSign)
                .isNotNull();
            assertThat(savedDuplicateSign.getCode())
                .isEqualTo(ACCOUNT_ALREADY_SIGNED_EXCEPTION.getValue());
            for (var text : savedDuplicateSign.getDescriptions()) {
                assertThat(errorText.contains(text)).isTrue();
            }
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/sign Попытка подписания счёта без AccountId")
    void testCreateSignAccountWithoutAccountId() {
        var errorText = Allure.step("Подготовка текста ошибки", () -> {
            return List.of("Поле обязательно для заполнения", "размер должен находиться в диапазоне от 36 до 36");
        });
        var account = Allure.step("Создание счёта для партнера", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });
        var response = Allure.step("Попытка подписания счёта без AccountId", () -> {
            return createAccountSignWithBadRequest(account.getDigitalId(), "", podamFactory.manufacturePojo(FraudMetaData.class));
        });
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
            for (var description : response.getDescriptions()) {
                assertTrue(description.getMessage().containsAll(errorText));
            }
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/sign Попытка подписания счёта без DigitalId")
    void testCreateSignAccountWithoutDigitalId() {
        var account = Allure.step("Создание счёта для партнера", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });
        var errorText = Allure.step("Подготовка текста ошибки", () -> {
            return "Искомая сущность account с id: " + account.getId() + ", digitalId:  не найдена";
        });
        var response = Allure.step("Попытка подписания счёта без DigitalId", () -> {
            return createAccountSignWithNotFound("", account.getId(), podamFactory.manufacturePojo(FraudMetaData.class));
        });
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getCode())
                .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
            assertThat(response.getMessage())
                .isEqualTo(errorText);
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/sign Попытка подписания счёта с не корректным AccountId")
    void testCreateSignAccountWithBadAccountId() {
        List<Descriptions> errorText = Allure.step("Подготовка текста ошибки", () -> {
            return List.of(
                new Descriptions()
                    .field("accountsSignDetail[0].accountId")
                    .message(
                        List.of("размер должен находиться в диапазоне от 36 до 36")
                    )
            );
        });
        var account = Allure.step("Создание счёта для партнера", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });
        var response = Allure.step("Попытка подписания счёта с не корректным AccountId", () -> {
            return createAccountSignWithBadRequest(
                account.getDigitalId(),
                RandomStringUtils.randomAlphabetic(37),
                podamFactory.manufacturePojo(FraudMetaData.class)
            );
        });
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
            for (var text : response.getDescriptions()) {
                assertThat(errorText.contains(text)).isTrue();
            }
        });
    }

    @Test
    @DisplayName("POST /partner/accounts/sign Попытка подписания счёта без Id")
    void testCreateSignAccountWithoutIds() {
        var errorText = Allure.step("Подготовка текста ошибки", () -> {
            return List.of("Поле обязательно для заполнения", "размер должен находиться в диапазоне от 36 до 36");
        });
        var response = Allure.step("Попытка подписания счёта с не корректным AccountId", () -> {
            return createAccountSignWithBadRequest("", "", podamFactory.manufacturePojo(FraudMetaData.class));
        });
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getCode())
                .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
            for (var description : response.getDescriptions()) {
                assertTrue(description.getMessage().containsAll(errorText));
            }
        });
    }

    @Test
    @DisplayName("GET /partner/accounts/sign/{digitalId}/{accountId} Получение подписанного счёта")
    void testGetSignAccount() {
        var account = Allure.step("Создание счёта для партнера", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });
        Allure.step("Выполнение get-запроса /partner/accounts/sign/{digitalId}/{accountId}, код ответа 404", () -> get(
            baseRoutePath + "/{digitalId}" + "/{accountId}",
            HttpStatus.NOT_FOUND,
            Error.class,
            account.getDigitalId(), account.getId()
        ));
        var savedSign = Allure.step("Выполнение post-запроса /partner/accounts/sign, код ответа 200", () -> post(
            baseRoutePath,
            HttpStatus.OK,
            getValidAccountsSign(account.getDigitalId(), account.getId()),
            getFraudMetaDataHeaders(podamFactory.manufacturePojo(FraudMetaData.class)),
            AccountsSignInfoResponse.class
        ));
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(savedSign)
                .isNotNull();
        });
        var signInfo = Allure.step("Выполнение get-запроса /partner/accounts/sign/{digitalId}/{accountId}, код ответа 200", () -> get(
            baseRoutePath + "/{digitalId}" + "/{accountId}",
            HttpStatus.OK,
            AccountSignInfo.class,
            account.getDigitalId(), account.getId()
        ));
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(signInfo)
                .isNotNull();
            assertThat(signInfo.getDigitalId())
                .isEqualTo(savedSign.getDigitalId());
        });
    }

    @Test
    @DisplayName("GET /partner/accounts/sign/{digitalId}/{accountId} Попытка получения подписанного счёта которого нет")
    void testGetWhenNotFoundSignAccount() {
        var partner = Allure.step("Создание партнера", () -> {
            return createValidPartner();
        });
        var account = Allure.step("Создание счёта для партнера", () -> {
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });
        var savedSign = Allure.step("Выполнение post-запроса /partner/accounts/sign, код ответа 200", () -> post(
            baseRoutePath,
            HttpStatus.OK,
            getValidAccountsSign(account.getDigitalId(), account.getId()),
            getFraudMetaDataHeaders(podamFactory.manufacturePojo(FraudMetaData.class)),
            AccountsSignInfoResponse.class
        ));
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(savedSign)
                .isNotNull();
        });
        var notSignedAccount = Allure.step("Создание не подписанного счёта для партнера", () -> {
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });
        var signInfo = Allure.step("Выполнение get-запроса /partner/accounts/sign/{digitalId}/{accountId}, код ответа 404", () -> get(
            baseRoutePath + "/{digitalId}" + "/{accountId}",
            HttpStatus.NOT_FOUND,
            Error.class,
            notSignedAccount.getDigitalId(), notSignedAccount.getId()
        ));
        var errorText = Allure.step("Подготовка текста ошибки", () -> {
            return "Искомая сущность sign с id: " + notSignedAccount.getId() + ", digitalId: " + notSignedAccount.getDigitalId() + " не найдена";
        });
        Allure.step("Проверка корректности ответа", () -> {
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
        var account = Allure.step("Создание счёта для партнера", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });
        var savedSign = Allure.step("Выполнение post-запроса /partner/accounts/sign/{digitalId}/{accountId}, код ответа 200", () -> post(
            baseRoutePath,
            HttpStatus.OK,
            getValidAccountsSign(account.getDigitalId(), account.getId()),
            getFraudMetaDataHeaders(podamFactory.manufacturePojo(FraudMetaData.class)),
            AccountsSignInfoResponse.class
        ));
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(savedSign)
                .isNotNull();
        });
        var signInfo = Allure.step("Выполнение get-запроса /partner/accounts/sign/{digitalId}/{accountId}, код ответа 404", () -> get(
            baseRoutePath + "/{digitalId}" + "/{accountId}",
            HttpStatus.NOT_FOUND,
            Error.class,
            "", account.getId()
        ));
        var errorText = Allure.step("Подготовка текста ошибки", () -> {
            return "Искомая сущность account с id: " + account.getId() + ", digitalId: sign не найдена";
        });
        Allure.step("Проверка корректности ответа", () -> {
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
        var account = Allure.step("Создание счёта для партнера", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });
        var savedSign = Allure.step("Выполнение post-запроса /partner/accounts/sign/{digitalId}/{accountId}, код ответа 200", () -> post(
            baseRoutePath,
            HttpStatus.OK,
            getValidAccountsSign(account.getDigitalId(), account.getId()),
            getFraudMetaDataHeaders(podamFactory.manufacturePojo(FraudMetaData.class)),
            AccountsSignInfoResponse.class
        ));
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(savedSign)
                .isNotNull();
        });
        var signInfo = Allure.step("Выполнение get-запроса /partner/accounts/sign/{digitalId}/{accountId}, код ответа 500", () -> get(
            baseRoutePath + "/{digitalId}" + "/{accountId}",
            HttpStatus.INTERNAL_SERVER_ERROR,
            Error.class,
            account.getDigitalId(), ""
        ));
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(signInfo)
                .isNotNull();
        });
        var errorText = Allure.step("Подготовка текста ошибки", () -> {
            return "Invalid UUID string: " + account.getDigitalId();
        });
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(signInfo.getCode())
                .isEqualTo(EXCEPTION.getValue());
            assertThat(signInfo.getMessage())
                .isEqualTo(errorText);
        });
    }

    @Test
    @DisplayName("GET /partner/accounts/sign/{digitalId}/{accountId} Попытка получения подписанного счёта без AccountId и без DigitalId")
    void testGetSignAccountWithoutDigitalIdAndAccountId() {
        var account = Allure.step("Создание счёта для партнера", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });
        var savedSign = Allure.step("Выполнение post-запроса /partner/accounts/sign/{digitalId}/{accountId}, код ответа 200", () -> post(
            baseRoutePath,
            HttpStatus.OK,
            getValidAccountsSign(account.getDigitalId(), account.getId()),
            getFraudMetaDataHeaders(podamFactory.manufacturePojo(FraudMetaData.class)),
            AccountsSignInfoResponse.class
        ));
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(savedSign)
                .isNotNull();
        });
        var signInfo = Allure.step("Выполнение get-запроса /partner/accounts/sign/{digitalId}/{accountId}, код ответа 405", () -> get(
            baseRoutePath + "/{digitalId}" + "/{accountId}",
            HttpStatus.METHOD_NOT_ALLOWED,
            Error.class,
            "", ""
        ));
        var errorText = Allure.step("Подготовка текста ошибки", () -> {
            return "Request method 'GET' not supported";
        });
        Allure.step("Проверка корректности ответа", () -> {
            assertThat(signInfo)
                .isNotNull();
            assertThat(signInfo.getMessage())
                .isEqualTo(errorText);
        });
    }

    @Test
    @DisplayName("DELETE /partner/accounts/sign/{digitalId} Удаление подписи счёта")
    void testDeleteSignAccount() {
        var account = Allure.step("Создание счёта для партнера", () -> {
            var partner = createValidPartner();
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });
        var savedSign = Allure.step("Создание подписанного счёта", () -> {
            var fraudMetaData = podamFactory.manufacturePojo(FraudMetaData.class);
            return createValidAccountsSign(account.getDigitalId(), account.getId(), fraudMetaData);
        });
        var accountSignDetail = Allure.step("Получение деталей подписи", () -> {
            return savedSign.getAccountsSignDetail().get(0);
        });
        var actualAccountSign = Allure.step("Выполнение get-запроса /partner/accounts/sign/{digitalId}/{accountId}, код ответа 200", () -> get(
            baseRoutePath + "/{digitalId}" + "/{accountId}",
            HttpStatus.OK,
            AccountSignInfo.class,
            savedSign.getDigitalId(), accountSignDetail.getAccountId()
        ));
        Allure.step("Проверка корректности подписи", () -> {
            assertThat(actualAccountSign)
                .isNotNull();
            assertThat(actualAccountSign.getAccountSignDetail())
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("dateTimeOfSign")
                .isEqualTo(accountSignDetail);
        });
        var deleteAccountSign = Allure.step("Выполнение delete-запроса /partner/accounts/sign/{digitalId}, код ответа 204 (удаление подписи)", () -> delete(
            baseRoutePath + "/{digitalId}",
            HttpStatus.NO_CONTENT,
            Map.of("accountIds", accountSignDetail.getAccountId()),
            savedSign.getDigitalId()
        ).getBody());
        Allure.step("Проверка корректности запроса", () -> {
            assertThat(deleteAccountSign)
                .isNotNull();
        });
        var searchAccountSign = Allure.step("Выполнение get-запроса /partner/accounts/sign/{digitalId}/{accountId}, код ответа 404", () -> get(
            baseRoutePath + "/{digitalId}" + "/{accountId}",
            HttpStatus.NOT_FOUND,
            Error.class,
            savedSign.getDigitalId(), accountSignDetail.getAccountId()
        ));
        Allure.step("Проверка корректности запроса", () -> {
            assertThat(searchAccountSign)
                .isNotNull();
            assertThat(searchAccountSign.getCode())
                .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
        });
    }

    @Test
    @DisplayName("DELETE /partner/accounts/sign/{digitalId} Попытка удаления не сущестущей подписи счёта")
    void testDeleteNotExistedSignAccount() {
        var partner = Allure.step("Создание партнера", () -> {
            return createValidPartner();
        });
        var account = Allure.step("Создание счёта для партнера", () -> {
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });
        Allure.step("Подписание счёта партнера", () -> {
            var fraudMetaData = podamFactory.manufacturePojo(FraudMetaData.class);
            createValidAccountsSign(account.getDigitalId(), account.getId(), fraudMetaData);
        });
        var accountWithoutSign = Allure.step("Создание счёта без подписи", () -> {
            return createValidAccount(partner.getId(), partner.getDigitalId());
        });
        var deleteAccountSign = Allure.step("Выполнение delete-запроса /partner/accounts/sign/{digitalId}, код ответа 204 (удаление подписи)", () -> delete(
            baseRoutePath + "/{digitalId}",
            HttpStatus.NO_CONTENT,
            Map.of("accountIds", accountWithoutSign.getId()),
            accountWithoutSign.getDigitalId()
        ).getBody());
        Allure.step("Проверка корректности запроса", () -> {
            assertThat(deleteAccountSign)
                .isNotNull();
        });
    }
}
