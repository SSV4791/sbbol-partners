package ru.sberbank.pprb.sbbol.partners.rest.partner;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignInfo;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfoResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.rest.config.SbbolIntegrationWithOutSbbolConfiguration;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.partners.handler.ErrorCode.MODEL_NOT_FOUND_EXCEPTION;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountControllerTest.createValidAccount;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

@ContextConfiguration(classes = SbbolIntegrationWithOutSbbolConfiguration.class)
public class AccountSignControllerTest extends BaseAccountSignControllerTest {

    @Test
    void testCreateSignAccount() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        var savedSign = createValidAccountsSign(account.getDigitalId(), account.getId());
        assertThat(savedSign)
            .isNotNull();
    }

    @Test
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
            .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
    }
}
