package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.qameta.allure.AllureId;
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

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountControllerTest.getValidAccount;

@ContextConfiguration(classes = SbbolIntegrationWithSbbolConfiguration.class)
class AccountControllerWithSbbolTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/partner";

    @Test
    @AllureId("34208")
    void testGetAccount() {
        var response = get(
            baseRoutePath + "/accounts" + "/{digitalId}" + "/{id}",
            HttpStatus.NOT_FOUND,
            Error.class,
            randomAlphabetic(10), UUID.randomUUID()
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getCode())
            .isEqualTo(HttpStatus.NOT_FOUND.name());
    }

    @Test
    @AllureId("34111")
    void testViewAccount() {
        var filter = new AccountsFilter()
            .digitalId(randomAlphabetic(10))
            .partnerIds(List.of(UUID.randomUUID().toString()))
            .pagination(new Pagination()
                .count(4)
                .offset(0));

        var response = post(baseRoutePath + "/accounts/view", HttpStatus.NOT_FOUND, filter, Error.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getCode())
            .isEqualTo(HttpStatus.NOT_FOUND.name());
    }

    @Test
    @AllureId("34122")
    void testCreateAccount() {
        var account = getValidAccount("bcd979a0-47ab-4337-84b8-8b4160448391", randomAlphabetic(10));
        var response = post(baseRoutePath + "/account", HttpStatus.NOT_FOUND, account, Error.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getCode())
            .isEqualTo(HttpStatus.NOT_FOUND.name());
    }

    @Test
    @AllureId("34152")
    void testUpdateAccount() {
        var account = new AccountChange()
            .id(UUID.randomUUID().toString())
            .digitalId(randomAlphabetic(10))
            .partnerId(UUID.randomUUID().toString())
            .comment("111111")
            .version(0L)
            .account("40802810500490014206")
            .bank(new Bank()
                .bic("044525411")
                .name("222222")
                .bankAccount(
                    new BankAccount()
                        .bankAccount("30101810145250000411"))
            );
        var response = put(baseRoutePath + "/account", HttpStatus.NOT_FOUND, account, Error.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getCode())
            .isEqualTo(HttpStatus.NOT_FOUND.name());
    }

    @Test
    @AllureId("34159")
    void testDeleteAccount() {
        var response = delete(
            baseRoutePath + "/accounts" + "/{digitalId}",
            HttpStatus.NOT_FOUND,
            Map.of("ids", randomAlphabetic(10)),
            randomAlphabetic(10)
        ).as(Error.class);
        assertThat(response)
            .isNotNull();
        assertThat(response.getCode())
            .isEqualTo(HttpStatus.NOT_FOUND.name());
    }
}
