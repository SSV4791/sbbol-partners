package ru.sberbank.pprb.sbbol.partners.rest.partner;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationWithOutSbbolTest;
import ru.sberbank.pprb.sbbol.partners.model.AccountSign;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignResponse;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignStatus;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountControllerTest.createValidAccount;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

public class AccountSignControllerTest extends AbstractIntegrationWithOutSbbolTest {

    public static final String baseRoutePath = "/partner/accounts/sign";

    @Test
    void testViewSignAccount() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var account1 = createValidAccount(partner.getId(), partner.getDigitalId());
        var account2 = createValidAccount(partner.getId(), partner.getDigitalId());
        var account3 = createValidAccount(partner.getId(), partner.getDigitalId());
        var account4 = createValidAccount(partner.getId(), partner.getDigitalId());

        var filter1 = new AccountsSignFilter()
            .digitalId(partner.getDigitalId())
            .partnerId(partner.getId())
            .accountsId(
                List.of(
                    account1.getId(),
                    account2.getId(),
                    account3.getId(),
                    account4.getId()
                ))
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var response1 = post(
            baseRoutePath + "/view",
            filter1,
            AccountsSignResponse.class
        );
        assertThat(response1)
            .isNotNull();
        assertThat(response1.getAccountsSign().size())
            .isEqualTo(4);
    }

    @Test
    void testUpdateSignAccount() {
        var partner = createValidPartner(RandomStringUtils.randomAlphabetic(10));
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        var sign = new AccountsSignStatus()
            .accountsSign(List.of(
                new AccountSign()
                    .accountId(account.getId())
                    .digitalId(account.getDigitalId())
                    .state(AccountSign.StateEnum.SIGNED)
            ));
        var accountsSign = put(baseRoutePath, sign, AccountsSignResponse.class);
        assertThat(accountsSign)
            .isNotNull();
        assertThat(accountsSign.getAccountsSign())
            .contains(sign.getAccountsSign().get(0));
    }
}
