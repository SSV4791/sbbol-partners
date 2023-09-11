package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.qameta.allure.Allure;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.config.PodamConfiguration.getBic;
import static ru.sberbank.pprb.sbbol.partners.config.PodamConfiguration.getValidCorrAccountNumber;
import static ru.sberbank.pprb.sbbol.partners.config.PodamConfiguration.getValidAccountNumber;

@SuppressWarnings("java:S2187")
public class BaseAccountControllerTest extends AbstractIntegrationTest {

    protected static final String baseRoutePath = "/partner";
    protected static final String BUDGET_ACCOUNT_VALID = "03010643100000000001";
    protected static final String BUDGET_40101_ACCOUNT_VALID = "40101810822805200005";
    protected static final String BUDGET_CORR_ACCOUNT_VALID = "40102810300000000001";

    public static AccountCreate getValidAccountWithEmptyBankAccount(UUID partnerUuid, String digitalId) {
        var bic = getBic();
        return new AccountCreate()
            .partnerId(partnerUuid)
            .digitalId(digitalId)
            .account(getValidAccountNumber(bic))
            .comment("Это тестовый комментарий")
            .bank(new BankCreate()
                .bic(bic)
                .name(randomAlphabetic(10))
            );
    }

    public static AccountCreate getValidAccount(UUID partnerUuid, String digitalId) {
        var bic = getBic();
        return new AccountCreate()
            .partnerId(partnerUuid)
            .digitalId(digitalId)
            .account(getValidAccountNumber(bic))
            .comment("Это тестовый комментарий")
            .bank(new BankCreate()
                .bic(bic)
                .name(randomAlphabetic(10))
                .bankAccount(
                    new BankAccountCreate()
                        .bankAccount(getValidCorrAccountNumber(getBic())))
            );
    }

    public static Account createValidAccount(UUID partnerUuid, String digitalId) {
        return Allure.step("Создание счета ", () -> post(
            baseRoutePath + "/account",
            HttpStatus.CREATED,
            getValidAccount(partnerUuid, digitalId),
            Account.class
        ));
    }

    public static Account createValidAccountWithEmptyBankAccount(UUID partnerUuid, String digitalId) {
        return Allure.step("Создание счета ", () -> post(
            baseRoutePath + "/account",
            HttpStatus.CREATED,
            getValidAccountWithEmptyBankAccount(partnerUuid, digitalId),
            Account.class
        ));
    }

    public static Account createValidAccount(AccountCreate account) {
        return post(
            baseRoutePath + "/account",
            HttpStatus.CREATED,
            account,
            Account.class
        );
    }

    public static Error createInvalidAccount(AccountCreate account) {
        return post(
            baseRoutePath + "/account",
            HttpStatus.BAD_REQUEST,
            account,
            Error.class
        );
    }

    public static AccountChange updateAccount(Account account) {
        var bic = getBic();
        return new AccountChange()
            .comment(randomAlphabetic(20))
            .version(account.getVersion())
            .digitalId(account.getDigitalId())
            .id(account.getId())
            .partnerId(account.getPartnerId())
            .account(getValidAccountNumber(bic))
            .bank(new Bank()
                .bic(bic)
                .name(account.getBank().getName())
                .bankAccount(new BankAccount()
                    .bankAccount(getValidCorrAccountNumber(bic))));
    }

    public static AccountChange updateAccountEntityWhenBankIsNull(Account account) {
        return new AccountChange()
            .comment(randomAlphabetic(20))
            .version(account.getVersion())
            .digitalId(account.getDigitalId())
            .id(account.getId())
            .partnerId(account.getPartnerId())
            .account(getValidAccountNumber(getBic()))
            .bank(null);
    }

    public static AccountChange updateAccountEntityWhenBankNameIsNull(Account account) {
        var bic = getBic();
        return new AccountChange()
            .comment(randomAlphabetic(20))
            .version(account.getVersion())
            .digitalId(account.getDigitalId())
            .id(account.getId())
            .partnerId(account.getPartnerId())
            .account(getValidAccountNumber(bic))
            .bank(new Bank()
                .bic(bic)
                .name(null)
                .bankAccount(new BankAccount()
                    .bankAccount(getValidCorrAccountNumber(bic))));
    }

    public static AccountChange updateAccountEntityWhenBankNameIsEmpty(Account account) {
        var bic = getBic();
        return new AccountChange()
            .comment(randomAlphabetic(20))
            .version(account.getVersion())
            .digitalId(account.getDigitalId())
            .id(account.getId())
            .partnerId(account.getPartnerId())
            .account(getValidAccountNumber(bic))
            .bank(new Bank()
                .bic(bic)
                .name("")
                .bankAccount(new BankAccount()
                    .bankAccount(getValidCorrAccountNumber(bic))));
    }

    public static AccountChange updateAccountEntityWhenBankBicIsNull(Account account) {
        var bic = getBic();
        return new AccountChange()
            .comment(randomAlphabetic(20))
            .version(account.getVersion())
            .digitalId(account.getDigitalId())
            .id(account.getId())
            .partnerId(account.getPartnerId())
            .account(getValidAccountNumber(bic))
            .bank(new Bank()
                .bic(null)
                .name(account.getBank().getName())
                .bankAccount(new BankAccount()
                    .bankAccount(getValidCorrAccountNumber(bic))));
    }

    public static AccountChange updateAccountEntityWhenBankBicIsEmpty(Account account) {
        var bic = getBic();
        return new AccountChange()
            .comment(randomAlphabetic(20))
            .version(account.getVersion())
            .digitalId(account.getDigitalId())
            .id(account.getId())
            .partnerId(account.getPartnerId())
            .account(getValidAccountNumber(bic))
            .bank(new Bank()
                .bic("")
                .name(account.getBank().getName())
                .bankAccount(new BankAccount()
                    .bankAccount(getValidCorrAccountNumber(bic))));
    }

    public static AccountChange updateAccountEntityWithEmptyAccountAndBankAccount(Account account) {
        return new AccountChange()
            .comment(randomAlphabetic(20))
            .version(account.getVersion())
            .digitalId(account.getDigitalId())
            .id(account.getId())
            .partnerId(account.getPartnerId())
            .account("")
            .bank(new Bank()
                .bic("044525411")
                .name(account.getBank().getName())
                .bankAccount(new BankAccount()
                    .bankAccount("")));
    }

    public static AccountChange updateAccountEntityWithInvalidAccountAndBankAccount(Account account) {
        return new AccountChange()
            .comment(randomAlphabetic(20))
            .version(account.getVersion())
            .digitalId(account.getDigitalId())
            .id(account.getId())
            .partnerId(account.getPartnerId())
            .account("123AS810")
            .bank(new Bank()
                .bic("044525411")
                .name(account.getBank().getName())
                .bankAccount(new BankAccount()
                    .bankAccount("123AS")));
    }

    public static AccountChange updateAccountEntityWithNullAccountAndBankAccount(Account account) {
        return new AccountChange()
            .comment(randomAlphabetic(20))
            .version(account.getVersion())
            .digitalId(account.getDigitalId())
            .id(account.getId())
            .partnerId(account.getPartnerId())
            .account(null)
            .bank(new Bank()
                .bic("044525411")
                .name(account.getBank().getName())
                .bankAccount(new BankAccount()
                    .bankAccount(null)));
    }

    protected static AccountCreate getValidBudgetAccount(UUID partnerUuid, String digitalId) {
        var account = getValidAccount(partnerUuid, digitalId);
        account.setAccount(BUDGET_ACCOUNT_VALID);
        account.getBank().getBankAccount().setBankAccount(BUDGET_CORR_ACCOUNT_VALID);
        return account;
    }

    protected static AccountCreate getValidBudgetAccountWith40101Balance(UUID partnerUuid, String digitalId) {
        var account = getValidAccount(partnerUuid, digitalId);
        account.setAccount(BUDGET_40101_ACCOUNT_VALID);
        account.getBank().setBic("044525000");
        account.getBank().getBankAccount().setBankAccount("");
        return account;
    }

    public static Account createValidBudgetAccount(UUID partnerUuid, String digitalId) {
        var createAccount = post(
            baseRoutePath + "/account",
            HttpStatus.CREATED,
            getValidBudgetAccount(partnerUuid, digitalId),
            Account.class
        );
        assertThat(createAccount)
            .isNotNull();
        return createAccount;
    }

    public static void createValidBudgetAccountWith40101Balance(UUID partnerUuid, String digitalId) {
        var createAccount = post(
            baseRoutePath + "/account",
            HttpStatus.CREATED,
            getValidBudgetAccountWith40101Balance(partnerUuid, digitalId),
            Account.class
        );
        assertThat(createAccount)
            .isNotNull();
    }

    public static Error createNotValidAccount(UUID partnerUuid, String digitalId) {
        var account = getValidAccount(partnerUuid, digitalId);
        account.setAccount("222222AS");
        account.getBank().setBic("44444");
        account.getBank().getBankAccount().setBankAccount("2131243255234324123123123AS");
        return post(baseRoutePath + "/account", HttpStatus.BAD_REQUEST, account, Error.class);
    }

    public static Account createAccountEntityWithEmptyAccountAndBankAccount(UUID partnerUuid, String digitalId) {
        var account = getValidAccount(partnerUuid, digitalId);
        account.setAccount("");
        account.getBank().setBic("044525411");
        account.getBank().getBankAccount().setBankAccount("");
        return post(baseRoutePath + "/account", HttpStatus.CREATED, account, Account.class);
    }

    public static Account createAccountEntityWithNullAccountAndBankAccount(UUID partnerUuid, String digitalId) {
        var account = getValidAccount(partnerUuid, digitalId);
        account.setAccount(null);
        account.getBank().setBic("044525411");
        account.getBank().getBankAccount().setBankAccount(null);
        return post(baseRoutePath + "/account", HttpStatus.CREATED, account, Account.class);
    }

    public static Error createAccountEntityWhenBankIsNull(UUID partnerUuid, String digitalId) {
        var account = getValidAccount(partnerUuid, digitalId);
        account.setBank(null);
        return post(baseRoutePath + "/account", HttpStatus.BAD_REQUEST, account, Error.class);
    }

    public static Error createAccountEntityWhenBankNameIsNull(UUID partnerUuid, String digitalId) {
        var account = getValidAccount(partnerUuid, digitalId);
        var bank = account.getBank();
        bank.setName(null);
        return post(baseRoutePath + "/account", HttpStatus.BAD_REQUEST, account, Error.class);
    }

    public static Error createAccountEntityWhenBankNameIsEmpty(UUID partnerUuid, String digitalId) {
        var account = getValidAccount(partnerUuid, digitalId);
        var bank = account.getBank();
        bank.setName("");
        return post(baseRoutePath + "/account", HttpStatus.BAD_REQUEST, account, Error.class);
    }

    public static Error createAccountEntityWhenBankBicIsNull(UUID partnerUuid, String digitalId) {
        var account = getValidAccount(partnerUuid, digitalId);
        var bank = account.getBank();
        bank.setBic(null);
        return post(baseRoutePath + "/account", HttpStatus.BAD_REQUEST, account, Error.class);
    }

    public static Error createAccountEntityWhenBankBicIsEmpty(UUID partnerUuid, String digitalId) {
        var account = getValidAccount(partnerUuid, digitalId);
        var bank = account.getBank();
        bank.setBic("");
        return post(baseRoutePath + "/account", HttpStatus.BAD_REQUEST, account, Error.class);
    }

    private static AccountPriority getValidPriorityAccount(UUID accountId, String digitalId) {
        return new AccountPriority()
            .digitalId(digitalId)
            .id(accountId)
            .priorityAccount(true);
    }

    public static void createValidPriorityAccount(UUID accountId, String digitalId) {
        var createAccount = put(
            baseRoutePath + "/account/priority",
            HttpStatus.OK,
            getValidPriorityAccount(accountId, digitalId),
            Account.class);
        assertThat(createAccount)
            .isNotNull();
    }

    public static Error notCreatePriorityAccount(UUID accountId, String digitalId) {
        return put(
            baseRoutePath + "/account/priority",
            HttpStatus.BAD_REQUEST,
            getValidPriorityAccount(accountId, digitalId),
            Error.class
        );
    }

    public static Account changeAccount(AccountChange account) {
        var updatedAccount = put(
            baseRoutePath + "/account",
            HttpStatus.OK,
            account,
            Account.class
        );
        assertThat(updatedAccount)
            .isNotNull();
        return updatedAccount;
    }

    public static void deleteAccount(String digitalId, UUID accountId) {
        var deleteAccount =
            delete(
                baseRoutePath + "/accounts" + "/{digitalId}",
                HttpStatus.NO_CONTENT,
                Map.of("ids", List.of(accountId)),
                digitalId
            ).getBody();
        assertThat(deleteAccount)
            .isNotNull();
    }
}
