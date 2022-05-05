package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.qameta.allure.AllureId;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignDetail;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignInfo;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfo;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfoResponse;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.rest.config.SbbolIntegrationWithOutSbbolConfiguration;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountControllerTest.createValidAccount;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

@ContextConfiguration(classes = SbbolIntegrationWithOutSbbolConfiguration.class)
public class AccountSignControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/partner/accounts/sign";

    @Test
    @AllureId("34175")
    void testViewSignAccount() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var account1 = createValidAccount(partner.getId(), partner.getDigitalId());
        var account2 = createValidAccount(partner.getId(), partner.getDigitalId());
        var account3 = createValidAccount(partner.getId(), partner.getDigitalId());
        var account4 = createValidAccount(partner.getId(), partner.getDigitalId());
        var account5 = createValidAccount(partner.getId(), partner.getDigitalId());

        var filter1 = new AccountsSignFilter()
            .digitalId(partner.getDigitalId())
            .partnerId(partner.getId())
            .accountsId(
                List.of(
                    account1.getId(),
                    account2.getId(),
                    account3.getId(),
                    account4.getId(),
                    account5.getId()
                ))
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
    }

    @Test
    @AllureId("34190")
    void testCreateSignAccount() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        getValidAccountSign(account.getDigitalId(), account.getId());
        var savedSign = createValidAccountSign(account.getDigitalId(), account.getId());
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
        var savedSign = post(baseRoutePath, HttpStatus.OK, getValidAccountSign(account.getDigitalId(), account.getId()), AccountsSignInfoResponse.class);
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
        var savedSign = createValidAccountSign(account.getDigitalId(), account.getId());
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
                baseRoutePath + "/{digitalId}" + "/{accountId}",
                HttpStatus.NO_CONTENT,
                savedSign.getDigitalId(), accountSignDetail.getAccountId()
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

    public static AccountsSignInfoResponse createValidAccountSign(String digitalId, String accountId) {
        var createAccountSign = post(
            baseRoutePath,
            HttpStatus.OK,
            getValidAccountSign(digitalId, accountId),
            AccountsSignInfoResponse.class
        );
        assertThat(createAccountSign)
            .isNotNull();
        return createAccountSign;
    }

    private static AccountsSignInfo getValidAccountSign(String digitalId, String accountId) {
        return new AccountsSignInfo()
            .digitalId(digitalId)
            .accountsSignDetail(
                List.of(
                    new AccountSignDetail()
                        .entityId(UUID.randomUUID().toString())
                        .accountId(accountId)
                        .digest(RandomStringUtils.randomAlphabetic(10))
                        .sign(RandomStringUtils.randomAlphabetic(10))
                        .externalDataFileId(RandomStringUtils.randomAlphabetic(10))
                        .externalDataSignFileId(RandomStringUtils.randomAlphabetic(10))
                        .dateTimeOfSign(OffsetDateTime.now())
                )
            );
    }
}
