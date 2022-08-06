package ru.sberbank.pprb.sbbol.partners.rest.partner;

import org.springframework.http.HttpStatus;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.AccountPriority;
import ru.sberbank.pprb.sbbol.partners.model.Bank;
import ru.sberbank.pprb.sbbol.partners.model.BankAccount;
import ru.sberbank.pprb.sbbol.partners.model.BankAccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.BankCreate;
import ru.sberbank.pprb.sbbol.partners.model.Error;

import java.util.Map;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountControllerTest.getBic;

@SuppressWarnings("java:S2187")
public class BaseAccountControllerTest extends AbstractIntegrationTest {

    protected static final String baseRoutePath = "/partner";
    protected static final String ACCOUNT_FOR_TEST_PARTNER = "40802810500490014206";

    public static AccountCreate getValidAccount(String partnerUuid, String digitalId) {
        return new AccountCreate()
            .partnerId(partnerUuid)
            .digitalId(digitalId)
            .account(ACCOUNT_FOR_TEST_PARTNER)
            .comment("Это тестовый комментарий")
            .bank(new BankCreate()
                .bic(getBic())
                .name(randomAlphabetic(10))
                .bankAccount(
                    new BankAccountCreate()
                        .bankAccount("30101810145250000411"))
            );
    }

    public static Account createValidAccount(String partnerUuid, String digitalId) {
        return post(
            baseRoutePath + "/account",
            HttpStatus.CREATED,
            getValidAccount(partnerUuid, digitalId),
            Account.class
        );
    }

    public static Account createValidAccount(AccountCreate account) {
        return post(
            baseRoutePath + "/account",
            HttpStatus.CREATED,
            account,
            Account.class
        );
    }

    public static AccountChange updateAccount(Account account) {
        return new AccountChange()
            .comment(randomAlphabetic(20))
            .version(account.getVersion())
            .digitalId(account.getDigitalId())
            .id(account.getId())
            .partnerId(account.getPartnerId())
            .account(ACCOUNT_FOR_TEST_PARTNER)
            .bank(new Bank()
                .bic("044525411")
                .name(account.getBank().getName())
                .bankAccount(new BankAccount()
                    .bankAccount("30101810145250000411")));
    }

    private static AccountCreate getValidBudgetAccount(String partnerUuid, String digitalId) {
        var account = getValidAccount(partnerUuid, digitalId);
        account.setAccount("40601810300490014209");
        return account;
    }

    public static void createValidBudgetAccount(String partnerUuid, String digitalId) {
        var createAccount = post(
            baseRoutePath + "/account",
            HttpStatus.CREATED,
            getValidBudgetAccount(partnerUuid, digitalId),
            Account.class
        );
        assertThat(createAccount)
            .isNotNull();
    }

    public static Error createNotValidAccount(String partnerUuid, String digitalId) {
        var account = getValidAccount(partnerUuid, digitalId);
        account.setAccount("222222");
        account.getBank().setBic("44444");
        account.getBank().getBankAccount().setBankAccount("2131243255234324123123123");
        return post(baseRoutePath + "/account", HttpStatus.BAD_REQUEST, account, Error.class);
    }

    private static AccountPriority getValidPriorityAccount(String accountId, String digitalId) {
        return new AccountPriority()
            .digitalId(digitalId)
            .id(accountId)
            .priorityAccount(true);
    }

    public static void createValidPriorityAccount(String accountId, String digitalId) {
        var createAccount = put(
            baseRoutePath + "/account/priority",
            HttpStatus.OK,
            getValidPriorityAccount(accountId, digitalId),
            Account.class);
        assertThat(createAccount)
            .isNotNull();
    }

    public static Error notCreatePriorityAccount(String accountId, String digitalId) {
        return put(
            baseRoutePath + "/account/priority",
            HttpStatus.BAD_REQUEST,
            getValidPriorityAccount(accountId, digitalId),
            Error.class
        );
    }

    public static void changeAccount(AccountChange account) {
        var updatedAccount = put(
            baseRoutePath + "/account",
            HttpStatus.OK,
            account,
            Account.class
        );
        assertThat(updatedAccount)
            .isNotNull();
    }

    public static void deleteAccount(String digitalId, String accountId) {
        var deleteAccount =
            delete(
                baseRoutePath + "/accounts" + "/{digitalId}",
                HttpStatus.NO_CONTENT,
                Map.of("ids", accountId),
                digitalId
            ).getBody();
        assertThat(deleteAccount)
            .isNotNull();
    }
}
