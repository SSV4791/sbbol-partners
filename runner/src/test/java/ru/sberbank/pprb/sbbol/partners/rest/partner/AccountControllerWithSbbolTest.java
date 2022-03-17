package ru.sberbank.pprb.sbbol.partners.rest.partner;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationWithSbbolTest;
import ru.sberbank.pprb.sbbol.partners.model.AccountResponse;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.service.partner.BudgetMaskService;

import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountControllerTest.createNotValidAccount;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountControllerTest.createValidAccount;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

class AccountControllerWithSbbolTest extends AbstractIntegrationWithSbbolTest {

    @MockBean
    private BudgetMaskService budgetMaskService;

    public static final String baseRoutePath = "/partner";

    @Test
    void testGetAccount() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        var executeAccount = counterpartyMapper.toAccount(counterparty, partner.getDigitalId(), UUID.fromString(account.getId()), budgetMaskService);
        var actualAccount =
            get(
                baseRoutePath + "/account" + "/{digitalId}" + "/{id}",
                AccountResponse.class,
                account.getDigitalId(), account.getId()
            );
        assertThat(actualAccount)
            .isNotNull();
        assertThat(actualAccount.getAccount())
            .isNotNull()
            .isEqualTo(executeAccount);
    }

    @Test
    void testViewAccount() {
        var partner = createValidPartner(randomAlphabetic(10));
        createValidAccount(partner.getId(), partner.getDigitalId());
        createValidAccount(partner.getId(), partner.getDigitalId());

        var filter = new AccountsFilter()
            .digitalId(partner.getDigitalId())
            .partnerIds(List.of(partner.getId(), counterpartyView.getPprbGuid()))
            .pagination(new Pagination()
                .count(4)
                .offset(0));
        var response = post(
            baseRoutePath + "/accounts/view",
            filter,
            AccountsResponse.class
        );
        assertThat(response)
            .isNotNull();
        assertThat(response.getAccounts().size())
            .isEqualTo(1);
    }

    @Test
    void testCreateAccount() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        assertThat(account)
            .usingRecursiveComparison()
            .ignoringFields(
                "uuid",
                "banks.uuid",
                "banks.accountUuid",
                "banks.bankAccounts.uuid",
                "banks.bankAccounts.bankUuid")
            .isEqualTo(account);
    }

    @Test
    void testCreateNotValidAccount() {
        var partner = createValidPartner();
        var error = createNotValidAccount(partner.getId(), partner.getDigitalId());
        assertThat(error)
            .isNotNull();
        assertThat(error.getCode())
            .isEqualTo(HttpStatus.BAD_REQUEST.name());
    }

    @Test
    void testUpdateAccount() {
        var partner = createValidPartner();
        var executeAccount = counterpartyMapper.toAccount(updatedCounterparty, partner.getDigitalId(), null, budgetMaskService);
        var newUpdateAccount = put(baseRoutePath + "/account", executeAccount, AccountResponse.class);
        assertThat(newUpdateAccount)
            .isNotNull();
        assertThat(newUpdateAccount.getAccount().getAccount())
            .isEqualTo(newAcc);
        assertThat(newUpdateAccount.getErrors())
            .isNull();
    }

    @Test
    void testDeleteAccount() {
        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());
        var executeAccount = counterpartyMapper.toAccount(counterparty, partner.getDigitalId(), UUID.fromString(account.getId()), budgetMaskService);
        var actualAccount =
            get(
                baseRoutePath + "/account" + "/{digitalId}" + "/{id}",
                AccountResponse.class,
                account.getDigitalId(), account.getId()
            );
        assertThat(actualAccount)
            .isNotNull();
        assertThat(actualAccount.getAccount())
            .isNotNull()
            .isEqualTo(executeAccount);

        var deleteAccount =
            delete(
                baseRoutePath + "/account" + "/{digitalId}" + "/{id}",
                actualAccount.getAccount().getDigitalId(), actualAccount.getAccount().getId()
            );
        assertThat(deleteAccount)
            .isNotNull();

        var searchAccount =
            getNotFound(
                baseRoutePath + "/account" + "/{digitalId}" + "/{id}",
                Error.class,
                account.getDigitalId(), account.getId()
            );
        assertThat(searchAccount)
            .isNotNull();
        assertThat(searchAccount.getCode())
            .isEqualTo(HttpStatus.NOT_FOUND.name());
    }
}
