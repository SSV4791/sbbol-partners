package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.qameta.allure.AllureId;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignInfo;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfoResponse;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.rest.config.SbbolIntegrationWithOutSbbolConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountControllerTest.createValidAccount;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

@ContextConfiguration(classes = SbbolIntegrationWithOutSbbolConfiguration.class)
public class AccountSignControllerTest extends BaseAccountSignControllerTest {

    @Test
    @AllureId("34175")
    void testViewSignAccount() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        List<String> account = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            account.add(createValidAccount(partner.getId(), partner.getDigitalId()).getId());
        }
        createValidAccountsSign(partner.getDigitalId(), account);

        var filter1 = new AccountsSignFilter()
            .digitalId(partner.getDigitalId())
            .partnerId(partner.getId())
            .accountsId(account)
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var response = post(
            baseRoutePath + "/view",
            HttpStatus.OK,
            filter1,
            AccountsSignResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getAccountsSign().size())
            .isEqualTo(4);
        assertThat(response.getPagination().getHasNextPage())
            .isEqualTo(Boolean.TRUE);

        var partner2 = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        List<String> account2 = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            account2.add(createValidAccount(partner2.getId(), partner2.getDigitalId()).getId());
        }
        createValidAccountsSign(partner2.getDigitalId(), account2);
        account2.add(createValidAccount(partner2.getId(), partner2.getDigitalId()).getId());
        var filter2 = new AccountsSignFilter()
            .digitalId(partner2.getDigitalId())
            .partnerId(partner2.getId())
            .accountsId(account2)
            .pagination(new Pagination()
                .count(5)
                .offset(0));
        var response2 = post(
            baseRoutePath + "/view",
            HttpStatus.OK,
            filter2,
            AccountsSignResponse.class
        );
        assertThat(response2)
            .isNotNull();
        assertThat(response2.getAccountsSign().size())
            .isEqualTo(4);
        assertThat(response2.getPagination().getHasNextPage())
            .isEqualTo(Boolean.FALSE);
    }

    @Test
    @AllureId("34190")
    void testCreateSignAccount() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        var savedSign = createValidAccountsSign(account.getDigitalId(), account.getId());
        assertThat(savedSign)
            .isNotNull();
    }

    @Test
    @AllureId("34203")
    void testGetSignAccount() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        get(
            baseRoutePath + "/{digitalId}" + "/{accountId}",
            HttpStatus.NOT_FOUND,
            Error.class,
            account.getDigitalId(), account.getId()
        );
        var savedSign = post(baseRoutePath, HttpStatus.OK, getValidAccountsSign(account.getDigitalId(), account.getId()), AccountsSignInfoResponse.class);
        assertThat(savedSign)
            .isNotNull();
        var signInfo = get(
            baseRoutePath + "/{digitalId}" + "/{accountId}",
            HttpStatus.OK,
            AccountSignInfo.class,
            account.getDigitalId(), account.getId()
        );
        assertThat(signInfo)
            .isNotNull();
        assertThat(signInfo.getDigitalId())
            .isEqualTo(savedSign.getDigitalId());
    }

    @Test
    @AllureId("34135")
    void testDeleteSignAccount() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        var savedSign = createValidAccountsSign(account.getDigitalId(), account.getId());
        var accountSignDetail = savedSign.getAccountsSignDetail().get(0);
        var actualAccountSign =
            get(
                baseRoutePath + "/{digitalId}" + "/{accountId}",
                HttpStatus.OK,
                AccountSignInfo.class,
                savedSign.getDigitalId(), accountSignDetail.getAccountId()
            );
        assertThat(actualAccountSign)
            .isNotNull();
        assertThat(actualAccountSign.getAccountSignDetail())
            .isNotNull()
            .usingRecursiveComparison()
            .ignoringFields("dateTimeOfSign")
            .isEqualTo(accountSignDetail);

        var deleteAccountSign =
            delete(
                baseRoutePath + "/{digitalId}",
                HttpStatus.NO_CONTENT,
                Map.of("accountIds", accountSignDetail.getAccountId()),
                savedSign.getDigitalId()
            ).getBody();
        assertThat(deleteAccountSign)
            .isNotNull();

        var searchAccountSign =
            get(
                baseRoutePath + "/{digitalId}" + "/{accountId}",
                HttpStatus.NOT_FOUND,
                Error.class,
                savedSign.getDigitalId(), accountSignDetail.getAccountId()
            );
        assertThat(searchAccountSign)
            .isNotNull();
        assertThat(searchAccountSign.getCode())
            .isEqualTo(HttpStatus.NOT_FOUND.name());
    }
}
