package ru.sberbank.pprb.sbbol.partners.rest.partner;

import org.apache.commons.lang3.RandomStringUtils;
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
    void testCreateSignAccount() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        var savedSign = createValidAccountsSign(account.getDigitalId(), account.getId());
        var updatedAccount = get(
            "/partner" + "/accounts" + "/{digitalId}" + "/{id}",
            HttpStatus.OK,
            Account.class,
            account.getDigitalId(), account.getId()
        );
        assertThat(savedSign)
            .isNotNull();
        assertThat(updatedAccount.getState())
            .isEqualTo(SignType.SIGNED);
    }

    @Test
    void testCreateDuplicateSignAccount() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        var savedSign = createValidAccountsSign(account.getDigitalId(), account.getId());
        var savedDuplicateSign = createAccountSignWithBadRequest(account.getDigitalId(), account.getId());
        List<Descriptions> errorTexts = List.of(
            new Descriptions()
                .field("account")
                .message(
                    List.of("Ошибка обновления счёта: " + account.getAccount() + " запрещено повторно подписывать или изменять подписанные счета")
                )
        );
        assertThat(savedSign)
            .isNotNull();
        assertThat(savedDuplicateSign)
            .isNotNull();
        assertThat(savedDuplicateSign.getCode())
            .isEqualTo(ACCOUNT_ALREADY_SIGNED_EXCEPTION.getValue());
        for (var text : savedDuplicateSign.getDescriptions()) {
            assertThat(errorTexts.contains(text)).isTrue();
        }
    }

    @Test
    void testCreateSignAccountWithoutAccountId() {
        var errorText = List.of("Поле обязательно для заполнения", "размер должен находиться в диапазоне от 36 до 36");
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        var response = createAccountSignWithBadRequest(account.getDigitalId(), "");
        assertThat(response)
            .isNotNull();
        assertThat(response.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
        for (var description : response.getDescriptions()) {
            assertTrue(description.getMessage().containsAll(errorText));
        }
    }

    @Test
    void testCreateSignAccountWithoutDigitalId() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        var response = createAccountSignWithNotFound("", account.getId());
        var errorText = "Искомая сущность account с id: " + account.getId() + ", digitalId:  не найдена";
        assertThat(response)
            .isNotNull();
        assertThat(response.getCode())
            .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
        assertThat(response.getMessage())
            .isEqualTo(errorText);
    }

    @Test
    void testCreateSignAccountWithBadAccountId() {
        List<Descriptions> errorTexts = List.of(
            new Descriptions()
                .field("accountsSignDetail[0].accountId")
                .message(
                    List.of("размер должен находиться в диапазоне от 36 до 36")
                )
        );
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        var response = createAccountSignWithBadRequest(account.getDigitalId(), RandomStringUtils.randomAlphabetic(37));
        assertThat(response)
            .isNotNull();
        assertThat(response.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
        for (var text : response.getDescriptions()) {
            assertThat(errorTexts.contains(text)).isTrue();
        }
    }

    @Test
    void testCreateSignAccountWithoutIds() {
        var errorText = List.of("Поле обязательно для заполнения", "размер должен находиться в диапазоне от 36 до 36");
        var response = createAccountSignWithBadRequest("", "");
        assertThat(response)
            .isNotNull();
        assertThat(response.getCode())
            .isEqualTo(MODEL_VALIDATION_EXCEPTION.getValue());
        for (var description : response.getDescriptions()) {
            assertTrue(description.getMessage().containsAll(errorText));
        }
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
    void testGetWhenNotFoundSignAccount() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        var savedSign = post(baseRoutePath, HttpStatus.OK, getValidAccountsSign(account.getDigitalId(), account.getId()), AccountsSignInfoResponse.class);
        var notSignedAccount = createValidAccount(partner.getId(), partner.getDigitalId());
        assertThat(savedSign)
            .isNotNull();
        var signInfo = get(
            baseRoutePath + "/{digitalId}" + "/{accountId}",
            HttpStatus.NOT_FOUND,
            Error.class,
            notSignedAccount.getDigitalId(), notSignedAccount.getId()
        );
        assertThat(signInfo)
            .isNotNull();
        assertThat(signInfo.getCode())
            .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
        var errorText = "Искомая сущность sign с id: " + notSignedAccount.getId() + ", digitalId: " + notSignedAccount.getDigitalId() + " не найдена";
        assertThat(signInfo.getMessage())
            .isEqualTo(errorText);
    }

    @Test
    void testGetSignAccountWithoutDigitalId() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        var savedSign = post(baseRoutePath, HttpStatus.OK, getValidAccountsSign(account.getDigitalId(), account.getId()), AccountsSignInfoResponse.class);
        assertThat(savedSign)
            .isNotNull();
        var signInfo = get(
            baseRoutePath + "/{digitalId}" + "/{accountId}",
            HttpStatus.NOT_FOUND,
            Error.class,
            "", account.getId()
        );
        assertThat(signInfo)
            .isNotNull();
        assertThat(signInfo.getCode())
            .isEqualTo(MODEL_NOT_FOUND_EXCEPTION.getValue());
        var errorText = "Искомая сущность account с id: " + account.getId() + ", digitalId: sign не найдена";
        assertThat(signInfo.getMessage())
            .isEqualTo(errorText);
    }

    @Test
    void testGetSignAccountWithoutAccountId() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        var savedSign = post(baseRoutePath, HttpStatus.OK, getValidAccountsSign(account.getDigitalId(), account.getId()), AccountsSignInfoResponse.class);
        assertThat(savedSign)
            .isNotNull();
        var signInfo = get(
            baseRoutePath + "/{digitalId}" + "/{accountId}",
            HttpStatus.INTERNAL_SERVER_ERROR,
            Error.class,
            account.getDigitalId(), ""
        );
        assertThat(signInfo)
            .isNotNull();
        assertThat(signInfo.getCode())
            .isEqualTo(EXCEPTION.getValue());
        var errorText = "Invalid UUID string: " + account.getDigitalId();
        assertThat(signInfo.getMessage())
            .isEqualTo(errorText);
    }

    @Test
    void testGetSignAccountWithoutDigitalIdAndAccountId() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        var savedSign = post(baseRoutePath, HttpStatus.OK, getValidAccountsSign(account.getDigitalId(), account.getId()), AccountsSignInfoResponse.class);
        assertThat(savedSign)
            .isNotNull();
        var signInfo = get(
            baseRoutePath + "/{digitalId}" + "/{accountId}",
            HttpStatus.METHOD_NOT_ALLOWED,
            Error.class,
            "", ""
        );
        assertThat(signInfo)
            .isNotNull();
        var errorText = "Request method 'GET' not supported";
        assertThat(signInfo.getMessage())
            .isEqualTo(errorText);
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

    @Test
    void testDeleteNotExistedSignAccount() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        var accountWithoutSign = createValidAccount(partner.getId(), partner.getDigitalId());
        createValidAccountsSign(account.getDigitalId(), account.getId());
        var deleteAccountSign =
            delete(
                baseRoutePath + "/{digitalId}",
                HttpStatus.NO_CONTENT,
                Map.of("accountIds", accountWithoutSign.getId()),
                accountWithoutSign.getDigitalId()
            ).getBody();
        assertThat(deleteAccountSign)
            .isNotNull();
    }
}
