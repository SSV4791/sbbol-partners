package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.restassured.response.ResponseBody;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.http.HttpStatus;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignDetail;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfo;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfoResponse;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("java:S2187")
public class BaseAccountSignControllerTest extends AbstractIntegrationTest {

    public static final String baseRoutePath = "/partner/accounts/sign";

    public static AccountsSignInfoResponse createValidAccountsSign(String digitalId, String accountId) {
        var createAccountSign = post(
            baseRoutePath,
            HttpStatus.OK,
            getValidAccountsSign(digitalId, accountId),
            AccountsSignInfoResponse.class
        );
        assertThat(createAccountSign)
            .isNotNull();
        return createAccountSign;
    }

    public static ResponseBody deleteAccountSign(String digitalId, String accountId) {
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

    public static AccountsSignInfoResponse createValidAccountsSign(String digitalId, List<String> accountsId) {
        var createAccountSign = post(
            baseRoutePath,
            HttpStatus.OK,
            getValidAccountsSign(digitalId, accountsId),
            AccountsSignInfoResponse.class
        );
        assertThat(createAccountSign)
            .isNotNull();
        return createAccountSign;
    }

    public static AccountsSignInfo getValidAccountsSign(String digitalId, String accountId) {
        return new AccountsSignInfo()
            .digitalId(digitalId)
            .accountsSignDetail(
                List.of(
                    new AccountSignDetail()
                        .entityId(UUID.randomUUID().toString())
                        .accountId(accountId)
                        .digest(RandomStringUtils.randomAlphabetic(10))
                        .sign(RandomStringUtils.randomAlphabetic(10))
                        .signProfileId(String.valueOf(RandomUtils.nextLong()))
                        .externalDataFileId(RandomStringUtils.randomAlphabetic(10))
                        .externalDataSignFileId(RandomStringUtils.randomAlphabetic(10))
                        .dateTimeOfSign(OffsetDateTime.now())
                )
            );
    }

    public static AccountsSignInfo getValidAccountsSign(String digitalId, List<String> accountsId) {
        List<AccountSignDetail> accountSignDetails = new ArrayList<>();
        for (var accountId : accountsId) {
            accountSignDetails.add(
                new AccountSignDetail()
                    .entityId(UUID.randomUUID().toString())
                    .accountId(accountId)
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
            .accountsSignDetail(accountSignDetails);
    }
}
