package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.restassured.response.ResponseBody;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.http.HttpStatus;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignDetail;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfo;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfoResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("java:S2187")
public class BaseAccountSignControllerTest extends AbstractIntegrationTest {

    protected static final String baseRoutePath = "/partner/accounts/sign";

    public static AccountsSignInfoResponse createValidAccountsSign(
        String digitalId,
        UUID accountId,
        Long version,
        String base64FraudMetaData
    ) {
        var createAccountSign = post(
            baseRoutePath,
            HttpStatus.OK,
            getValidAccountsSign(digitalId, accountId, version),
            getFraudMetaDataHeaders(base64FraudMetaData),
            AccountsSignInfoResponse.class
        );
        assertThat(createAccountSign)
            .isNotNull();
        return createAccountSign;
    }

    public static Error createInvalidAccountsSignWithInvalidFraudMetaData(
        String digitalId,
        UUID accountId,
        Long version,
        String base64InvalidFraudMetaData
    ) {
        var response = post(
            baseRoutePath,
            HttpStatus.BAD_REQUEST,
            getValidAccountsSign(digitalId, accountId, version),
            getFraudMetaDataHeaders(base64InvalidFraudMetaData),
            Error.class
        );
        assertThat(response)
            .isNotNull();
        return response;
    }

    public static Error createAccountSignWithBadRequest(String digitalId, UUID accountId, Long version, String base64FraudMetaData) {
        var response = post(
            baseRoutePath,
            HttpStatus.BAD_REQUEST,
            getValidAccountsSign(digitalId, accountId, version),
            getFraudMetaDataHeaders(base64FraudMetaData),
            Error.class
        );
        assertThat(response)
            .isNotNull();
        return response;
    }

    public static Error createAccountSignWithNotFound(String digitalId, UUID accountId, Long version, String base64FraudMetaData) {
        var response = post(
            baseRoutePath,
            HttpStatus.NOT_FOUND,
            getValidAccountsSign(digitalId, accountId, version),
            getFraudMetaDataHeaders(base64FraudMetaData),
            Error.class
        );
        assertThat(response)
            .isNotNull();
        return response;
    }

    public static ResponseBody deleteAccountSign(String digitalId, UUID accountId) {
        var deleteAccountSign =
            delete(
                baseRoutePath + "/{digitalId}",
                HttpStatus.NO_CONTENT,
                Map.of("accountIds", accountId),
                digitalId
            ).getBody();
        assertThat(deleteAccountSign)
            .isNotNull();
        return deleteAccountSign;
    }

    public static AccountsSignInfoResponse createValidAccountsSign(String digitalId, List<Account> accounts, String base64FraudMetaData) {
        var createAccountSign = post(
            baseRoutePath,
            HttpStatus.OK,
            getValidAccountsSign(digitalId, accounts),
            getFraudMetaDataHeaders(base64FraudMetaData),
            AccountsSignInfoResponse.class
        );
        assertThat(createAccountSign)
            .isNotNull();
        return createAccountSign;
    }

    public static Map<String, String> getFraudMetaDataHeaders(String base64FraudMetaData) {
        return Map.of("Fraud-Meta-Data", base64FraudMetaData);
    }

    public static AccountsSignInfo getValidAccountsSign(String digitalId, UUID accountId, Long version) {
        return new AccountsSignInfo()
            .digitalId(digitalId)
            .digitalUserId(RandomStringUtils.randomAlphabetic(10))
            .accountsSignDetail(
                List.of(
                    new AccountSignDetail()
                        .entityId(UUID.randomUUID())
                        .accountId(accountId)
                        .accountVersion(version)
                        .digest(RandomStringUtils.randomAlphabetic(10))
                        .sign(RandomStringUtils.randomAlphabetic(10))
                        .signProfileId(String.valueOf(RandomUtils.nextLong()))
                        .externalDataFileId(RandomStringUtils.randomAlphabetic(10))
                        .externalDataSignFileId(RandomStringUtils.randomAlphabetic(10))
                        .dateTimeOfSign(OffsetDateTime.now())
                )
            );
    }

    public static AccountsSignInfo getValidAccountsSign(String digitalId, List<Account> accounts) {
        List<AccountSignDetail> accountSignDetails = new ArrayList<>();
        for (var account : accounts) {
            accountSignDetails.add(
                new AccountSignDetail()
                    .entityId(UUID.randomUUID())
                    .accountId(account.getId())
                    .accountVersion(account.getVersion())
                    .digest(RandomStringUtils.randomAlphabetic(10))
                    .sign(RandomStringUtils.randomAlphabetic(10))
                    .signProfileId(String.valueOf(RandomUtils.nextLong()))
                    .externalDataFileId(RandomStringUtils.randomAlphabetic(10))
                    .externalDataSignFileId(RandomStringUtils.randomAlphabetic(10))
                    .dateTimeOfSign(OffsetDateTime.now())
            );
        }
        return new AccountsSignInfo()
            .digitalId(digitalId)
            .digitalUserId(RandomStringUtils.randomAlphabetic(10))
            .accountsSignDetail(accountSignDetails);
    }
}
