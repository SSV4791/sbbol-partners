package ru.sberbank.pprb.sbbol.partners.rest.partner;

import io.qameta.allure.Allure;
import org.apache.commons.lang3.RandomStringUtils;
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

import java.util.Arrays;
import java.util.Map;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.AccountControllerTest.getBic;

@SuppressWarnings("java:S2187")
public class BaseAccountControllerTest extends AbstractIntegrationTest {

    protected static final String baseRoutePath = "/partner";
    protected static final String BUDGET_ACCOUNT_VALID = "03010643100000000001";
    protected static final String BUDGET_CORR_ACCOUNT_VALID = "40102810300000000001";
    private static final int[] WEIGHT_FACTOR = new int[]{7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1};
    private static final String STATIC_ACCOUNT_PART = "40702810";
    private static final String ACCOUNT_KEY = "0";
    private static final String STATIC_INN_PART = "7707";
    private static final int[] WEIGHT_FACTOR_FOR_LEGAL_ENTITY_INN = new int[]{2, 4, 10, 3, 5, 9, 4, 6, 8};
    private static final int[] WEIGHT_FACTOR_FOR_ELEVEN_KEY = new int[]{7, 2, 4, 10, 3, 5, 9, 4, 6, 8};
    private static final int[] WEIGHT_FACTOR_FOR_TWELVE_KEY = new int[]{3, 7, 2, 4, 10, 3, 5, 9, 4, 6, 8};
    private static final String STATIC_OGRN_PART = "10077";
    private static final int NUMBER_FOR_CHECK = 9;
    private static final int DIVIDER_FOR_LEGAL_ENTITY = 11;
    private static final int DIVIDER_FOR_PHYSICAL_PERSON = 13;

    /**
     * Алгоритм расчета контрольного ключа:
     * Значение контрольного ключа приравнивается нулю (К = 0).
     * Рассчитываются произведения значений разрядов на соответствующие весовые коэффициенты.
     * Рассчитывается сумма значений младших разрядов полученных произведений.
     * Младший разряд вычисленной суммы умножается на 3.
     * Значение контрольного ключа (К) принимается равным младшему разряду полученного произведения.
     */
    public static String getValidAccountNumber() {

        String randomAccountPart = RandomStringUtils.randomNumeric(11);
        String accountForCalculate = STATIC_ACCOUNT_PART + ACCOUNT_KEY + randomAccountPart;
        String stringForCalculate = getBic().substring(6) + accountForCalculate;
        int[] numberForCalculate = Arrays.stream(stringForCalculate.split("")).mapToInt(Integer::parseInt).toArray();
        int[] result = new int[numberForCalculate.length];
        for (int i = 0; i < WEIGHT_FACTOR.length; i++) {
            result[i] = numberForCalculate[i] * WEIGHT_FACTOR[i];
        }
        int resulSumma = 0;
        for (int j : result) {
            resulSumma += j % 10;
        }

        return STATIC_ACCOUNT_PART + resulSumma * 3 % 10 + randomAccountPart;
    }

    /**
     * Для расчета десятого контрольного разряда в 10-ти значном ИНН
     * каждая цифра ИНН (кроме десятой) умножается на соответствующий множитель в соответствии с таблицей,
     * затем все значения суммируются, сумма берется по модулю 11 (остаток деления на 11),
     * затем полученное число берется по модулю 10 это и есть десятый разряд.
     *
     * Для расчета 11-ого контрольного разряда (1-ой контрольной цифры) в 12-ти значном ИНН
     * каждая цифра ИНН (кроме 11-ой и 12-ой) умножается на соответствующий множитель в соответствии с таблицей,
     * затем все значения суммируются, сумма берется по модулю 11,
     * затем полученное число берется по модулю 10 это и есть 11-ый разряд.
     *
     * Для расчета 12-ого контрольного разряда (2-ой контрольной цифры) в 12-ти значном ИНН
     * каждая цифра ИНН (кроме12-ой), 11-ая вычисляется в соотв. с пред. пунктом,
     * умножается на соответствующий множитель в соответствии с таблицей,
     * затем все значения суммируются, сумма берется по модулю 11,
     * затем полученное число берется по модулю 10 это и есть 12-ый разряд.
     */
    public static String getValidInnNumber(LegalForm legalForm) {

        if (legalForm == LegalForm.LEGAL_ENTITY) {
            String randomInnPart = RandomStringUtils.randomNumeric(5);
            String innForCalculate = STATIC_INN_PART + randomInnPart;
            int calculateValidKeyForInn = calculateValidKeyForInn(innForCalculate, WEIGHT_FACTOR_FOR_LEGAL_ENTITY_INN);

            return STATIC_INN_PART + randomInnPart + calculateValidKeyForInn;
        } else {
            String randomInnPart = RandomStringUtils.randomNumeric(6);
            String innForCalculate = STATIC_INN_PART + randomInnPart;
            int calculateElevenKeyForInn = calculateValidKeyForInn(innForCalculate, WEIGHT_FACTOR_FOR_ELEVEN_KEY);

            String innForCalculateTwelveKey = innForCalculate + calculateElevenKeyForInn;
            int calculateTwelveKeyForInn = calculateValidKeyForInn(innForCalculateTwelveKey, WEIGHT_FACTOR_FOR_TWELVE_KEY);

            return STATIC_INN_PART + randomInnPart + calculateElevenKeyForInn + calculateTwelveKeyForInn;
        }
    }

    /**
     * Алгоритм проверки ОГРН/ОГРНИП
     * Выбрать 12-значное число ОГРН (с 1-й по 12-ю цифру) / 14-значное число ОГРНИП (с 1-й по 14-ю цифру).
     * Вычислить остаток от деления выбранного числа на 11 (ОГРН) / на 13 (ОГРНИП).
     * Если остаток больше 9, то контрольная цифра = последней цифре остатка
     * Сравнить младший разряд полученного остатка от деления с 13-й цифрой ОГРН / 15-й цифрой ОГРНИП. Если они равны, то ОГРН/ОГРНИП верный.
     */
    public static String getValidOgrnNumber(LegalForm legalForm) {
        if (legalForm == LegalForm.LEGAL_ENTITY) {
            String randomOgrnPart = RandomStringUtils.randomNumeric(7);
            String ogrnForCalculate = STATIC_OGRN_PART + randomOgrnPart;
            long validKeyForOgrn = calculateValidKeyForOgrn(ogrnForCalculate, legalForm);

            return ogrnForCalculate + validKeyForOgrn;
        } else {
            String randomOgrnPart = RandomStringUtils.randomNumeric(9);
            String ogrnForCalculate = STATIC_OGRN_PART + randomOgrnPart;
            long validKeyForOgrn = calculateValidKeyForOgrn(ogrnForCalculate, legalForm);

            return ogrnForCalculate + validKeyForOgrn;
        }
    }

    private static int calculateValidKeyForInn(String innForCalculate, int[] weightFactorForCalculate) {
        int[] numberForCalculate = Arrays.stream(innForCalculate.split("")).mapToInt(Integer::parseInt).toArray();
        int[] result = new int[numberForCalculate.length];
        for (int i = 0; i < weightFactorForCalculate.length; i++) {
            result[i] = numberForCalculate[i] * weightFactorForCalculate[i];
        }
        int resulSumma = 0;
        for (int numberForCalculatingElevenKey : result) {
            resulSumma += numberForCalculatingElevenKey;
        }

        return (resulSumma % 11) % 10;
    }

    private static long calculateValidKeyForOgrn(String ogrnForCalculate, LegalForm legalForm) {
        long ogrnNumber = Long.parseLong(ogrnForCalculate);
        if (legalForm == LegalForm.LEGAL_ENTITY) {
            long key = ogrnNumber % DIVIDER_FOR_LEGAL_ENTITY;
            if (key > NUMBER_FOR_CHECK) {
                key = key % 10;
            }
            return key;
        } else {
            long key = ogrnNumber % DIVIDER_FOR_PHYSICAL_PERSON;
            if (key > NUMBER_FOR_CHECK) {
                key = key % 10;
            }
            return key;
        }
    }

    public static AccountCreate getValidAccount(String partnerUuid, String digitalId) {
        return new AccountCreate()
            .partnerId(partnerUuid)
            .digitalId(digitalId)
            .account(getValidAccountNumber())
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
        return Allure.step("Создание счета " + getValidAccount(partnerUuid, digitalId).getAccount(), () -> post(
            baseRoutePath + "/account",
            HttpStatus.CREATED,
            getValidAccount(partnerUuid, digitalId),
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
        return new AccountChange()
            .comment(randomAlphabetic(20))
            .version(account.getVersion())
            .digitalId(account.getDigitalId())
            .id(account.getId())
            .partnerId(account.getPartnerId())
            .account(getValidAccountNumber())
            .bank(new Bank()
                .bic("044525411")
                .name(account.getBank().getName())
                .bankAccount(new BankAccount()
                    .bankAccount("30101810145250000411")));
    }

    public static AccountChange updateAccountEntityWhenBankIsNull(Account account) {
        return new AccountChange()
            .comment(randomAlphabetic(20))
            .version(account.getVersion())
            .digitalId(account.getDigitalId())
            .id(account.getId())
            .partnerId(account.getPartnerId())
            .account(getValidAccountNumber())
            .bank(null);
    }

    public static AccountChange updateAccountEntityWhenBankNameIsNull(Account account) {
        return new AccountChange()
            .comment(randomAlphabetic(20))
            .version(account.getVersion())
            .digitalId(account.getDigitalId())
            .id(account.getId())
            .partnerId(account.getPartnerId())
            .account(getValidAccountNumber())
            .bank(new Bank()
                .bic("044525411")
                .name(null)
                .bankAccount(new BankAccount()
                    .bankAccount("30101810145250000411")));
    }

    public static AccountChange updateAccountEntityWhenBankNameIsEmpty(Account account) {
        return new AccountChange()
            .comment(randomAlphabetic(20))
            .version(account.getVersion())
            .digitalId(account.getDigitalId())
            .id(account.getId())
            .partnerId(account.getPartnerId())
            .account(getValidAccountNumber())
            .bank(new Bank()
                .bic("044525411")
                .name("")
                .bankAccount(new BankAccount()
                    .bankAccount("30101810145250000411")));
    }

    public static AccountChange updateAccountEntityWhenBankBicIsNull(Account account) {
        return new AccountChange()
            .comment(randomAlphabetic(20))
            .version(account.getVersion())
            .digitalId(account.getDigitalId())
            .id(account.getId())
            .partnerId(account.getPartnerId())
            .account(getValidAccountNumber())
            .bank(new Bank()
                .bic(null)
                .name(account.getBank().getName())
                .bankAccount(new BankAccount()
                    .bankAccount("30101810145250000411")));
    }

    public static AccountChange updateAccountEntityWhenBankBicIsEmpty(Account account) {
        return new AccountChange()
            .comment(randomAlphabetic(20))
            .version(account.getVersion())
            .digitalId(account.getDigitalId())
            .id(account.getId())
            .partnerId(account.getPartnerId())
            .account(getValidAccountNumber())
            .bank(new Bank()
                .bic("")
                .name(account.getBank().getName())
                .bankAccount(new BankAccount()
                    .bankAccount("30101810145250000411")));
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
            .account("123AS")
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

    protected static AccountCreate getValidBudgetAccount(String partnerUuid, String digitalId) {
        var account = getValidAccount(partnerUuid, digitalId);
        account.setAccount(BUDGET_ACCOUNT_VALID);
        account.getBank().getBankAccount().setBankAccount(BUDGET_CORR_ACCOUNT_VALID);
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
        account.setAccount("222222AS");
        account.getBank().setBic("44444");
        account.getBank().getBankAccount().setBankAccount("2131243255234324123123123AS");
        return post(baseRoutePath + "/account", HttpStatus.BAD_REQUEST, account, Error.class);
    }

    public static Account createAccountEntityWithEmptyAccountAndBankAccount(String partnerUuid, String digitalId) {
        var account = getValidAccount(partnerUuid, digitalId);
        account.setAccount("");
        account.getBank().setBic("044525411");
        account.getBank().getBankAccount().setBankAccount("");
        return post(baseRoutePath + "/account", HttpStatus.CREATED, account, Account.class);
    }

    public static Account createAccountEntityWithNullAccountAndBankAccount(String partnerUuid, String digitalId) {
        var account = getValidAccount(partnerUuid, digitalId);
        account.setAccount(null);
        account.getBank().setBic("044525411");
        account.getBank().getBankAccount().setBankAccount(null);
        return post(baseRoutePath + "/account", HttpStatus.CREATED, account, Account.class);
    }

    public static Error createAccountEntityWhenBankIsNull(String partnerUuid, String digitalId) {
        var account = getValidAccount(partnerUuid, digitalId);
        account.setBank(null);
        return post(baseRoutePath + "/account", HttpStatus.BAD_REQUEST, account, Error.class);
    }

    public static Error createAccountEntityWhenBankNameIsNull(String partnerUuid, String digitalId) {
        var account = getValidAccount(partnerUuid, digitalId);
        var bank = account.getBank();
        bank.setName(null);
        return post(baseRoutePath + "/account", HttpStatus.BAD_REQUEST, account, Error.class);
    }

    public static Error createAccountEntityWhenBankNameIsEmpty(String partnerUuid, String digitalId) {
        var account = getValidAccount(partnerUuid, digitalId);
        var bank = account.getBank();
        bank.setName("");
        return post(baseRoutePath + "/account", HttpStatus.BAD_REQUEST, account, Error.class);
    }

    public static Error createAccountEntityWhenBankBicIsNull(String partnerUuid, String digitalId) {
        var account = getValidAccount(partnerUuid, digitalId);
        var bank = account.getBank();
        bank.setBic(null);
        return post(baseRoutePath + "/account", HttpStatus.BAD_REQUEST, account, Error.class);
    }

    public static Error createAccountEntityWhenBankBicIsEmpty(String partnerUuid, String digitalId) {
        var account = getValidAccount(partnerUuid, digitalId);
        var bank = account.getBank();
        bank.setBic("");
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
