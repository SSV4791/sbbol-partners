package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.Bank;
import ru.sberbank.pprb.sbbol.partners.model.BankAccount;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.rest.config.SbbolIntegrationWithSbbolConfiguration;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.qameta.allure.Allure.step;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.exception.common.ErrorCode.MODEL_NOT_FOUND_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountControllerTest.getValidAccount;

@ContextConfiguration(classes = SbbolIntegrationWithSbbolConfiguration.class)
class AccountControllerWithSbbolTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/partner";


    @Test
    void testGetAccount() {
        var response = step("Выполнение get-звапоса partner/accounts/{digitalId}/{id}",
            () -> get(
                baseRoutePath + "/accounts" + "/{digitalId}" + "/{id}",
                HttpStatus.NOT_FOUND,
                Error.class,
                randomAlphabetic(10), UUID.randomUUID()
            ));

        step("Проверка корректности ответа",
            () -> Assertions.assertAll(
                () -> assertThat(response)
                    .isNotNull(),
                () -> assertThat(response.getCode())
                    .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue())));
    }

    @Test
    void testViewAccount() {
        var filter = step("Подготовка тестовых данных",
            () -> new AccountsFilter()
                .digitalId(randomAlphabetic(10))
                .partnerIds(List.of(UUID.randomUUID()))
                .pagination(new Pagination()
                    .count(4)
                    .offset(0)));

        var response = step("Выполнение post-запроса /partner/accounts/view",
            () -> post(
                baseRoutePath + "/accounts/view",
                HttpStatus.NOT_FOUND,
                filter,
                Error.class));

        step("Проверка корректности ответа",
            () -> Assertions.assertAll(
                () -> assertThat(response)
                    .isNotNull(),
                () -> assertThat(response.getCode())
                    .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue())));
    }

    @Test
    void testCreateAccount() {
        var account = step("Подготовка тестовых данных",
            () -> getValidAccount(UUID.fromString("bcd979a0-47ab-4337-84b8-8b4160448391"),
                randomAlphabetic(10)));

        var response = step("Выполнение post-запроса /partner/account",
            () -> post(baseRoutePath + "/account",
                HttpStatus.NOT_FOUND,
                account,
                Error.class));

        step("Проверка корректности ответа",
            () -> Assertions.assertAll(
                () -> assertThat(response)
                    .isNotNull(),
                () -> assertThat(response.getCode())
                    .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue())));
    }

    @Test
    void testUpdateAccount() {
        var account = step("Подготовка тестовых данных",
            () -> new AccountChange()
                .id(UUID.randomUUID())
                .digitalId(randomAlphabetic(10))
                .partnerId(UUID.randomUUID())
                .comment("111111")
                .version(0L)
                .account("40802810500490014206")
                .bank(new Bank()
                    .bic("044525411")
                    .name("222222")
                    .bankAccount(
                        new BankAccount()
                            .bankAccount("30101810145250000411"))));

        var response = step("Выполнение put-запроса /partner/account",
            () -> put(
                baseRoutePath + "/account",
                HttpStatus.NOT_FOUND,
                account,
                Error.class));

        step("Проверка корректности ответа",
            () -> Assertions.assertAll(
                () -> assertThat(response)
                    .isNotNull(),
                () -> assertThat(response.getCode())
                    .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue())));
    }

    @Test
    void testDeleteAccount() {
        var response = step("Выполнение delete-запроса /partner/accounts/{digitalId}",
            () -> delete(
                baseRoutePath + "/accounts" + "/{digitalId}",
                HttpStatus.NOT_FOUND,
                Map.of("ids", UUID.randomUUID()),
                randomAlphabetic(10)
            ).as(Error.class));

        step("Проверка корректности ответа",
            () -> Assertions.assertAll(
                () -> assertThat(response)
                    .isNotNull(),
                () -> assertThat(response.getCode())
                    .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue())));
    }
}
