package ru.sberbank.pprb.sbbol.partners.validator;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.Bank;
import ru.sberbank.pprb.sbbol.partners.model.BankCreate;
import ru.sberbank.pprb.sbbol.partners.validation.account.account.AccountAttributeRurCodeCurrencyAccountChangeDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.account.account.AccountAttributeRurCodeCurrencyAccountCreateDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.account.account.AccountAttributeRurCodeCurrencyAccountCreateFullModelDtoValidator;
import ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidator;

import javax.validation.ConstraintValidatorContext;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

class AccountRubCodeCurrencyValidationTest extends BaseUnitConfiguration {
    private static final String BUDGET_CORR_ACCOUNT = "40102810045370000002";
    private static final String VALID_ACCOUNT = "40702810643810010294";
    private static final String INVALID_ACCOUNT = "00000000000000000000";

    private static AccountAttributeRurCodeCurrencyAccountCreateDtoValidator validatorAccountCreate;
    private static AccountAttributeRurCodeCurrencyAccountChangeDtoValidator validatorAccountChange;
    private static AccountAttributeRurCodeCurrencyAccountCreateFullModelDtoValidator validatorAccountFullModelCreate;

    @Mock
    private ConstraintValidatorContext context;

    @BeforeAll
    static void init() {
        validatorAccountCreate = spy(new AccountAttributeRurCodeCurrencyAccountCreateDtoValidator());
        doNothing()
            .when((BaseValidator) validatorAccountCreate).buildMessage(any(), any(), any());
        validatorAccountChange = spy(new AccountAttributeRurCodeCurrencyAccountChangeDtoValidator());
        doNothing()
            .when((BaseValidator) validatorAccountChange).buildMessage(any(), any(), any());
        validatorAccountFullModelCreate = spy(new AccountAttributeRurCodeCurrencyAccountCreateFullModelDtoValidator());
        doNothing()
            .when((BaseValidator) validatorAccountFullModelCreate).buildMessage(any(), any(), any());
    }

    @ParameterizedTest
    @MethodSource("positiveArguments")
    void accountAttributeRurCodeCurrencyAccountCreateDtoValidatorPositiveTest(String account, String corrAccount) {
        var accountCreate =
            Allure.step("Подготовка тестовых данных", () -> {
                var bank = factory.manufacturePojo(BankCreate.class);
                bank.getBankAccount().bankAccount(corrAccount);
                var accountPojo = factory.manufacturePojo(AccountCreate.class);
                accountPojo.account(account);
                accountPojo.bank(bank);
                return accountPojo;
            });
        var result =
            Allure.step("Произведение валидации", () ->
                validatorAccountCreate.isValid(accountCreate, context));
        Allure.step("Проверка полученного результата", () -> assertTrue(result));
    }

    @ParameterizedTest
    @MethodSource("positiveArguments")
    void accountAttributeRurCodeCurrencyAccountChangeDtoValidatorPositiveTest(String account, String corrAccount) {
        var accountCreate =
            Allure.step("Подготовка тестовых данных", () -> {
                var bank = factory.manufacturePojo(Bank.class);
                bank.getBankAccount().bankAccount(corrAccount);
                var accountPojo = factory.manufacturePojo(AccountChange.class);
                accountPojo.account(account);
                accountPojo.bank(bank);
                return accountPojo;
            });
        var result =
            Allure.step("Произведение валидации", () -> validatorAccountChange.isValid(accountCreate, context));
        Allure.step("Проверка полученного результата", () -> assertTrue(result));
    }

    @ParameterizedTest
    @MethodSource("positiveArguments")
    void accountAttributeRurCodeCurrencyAccountCreateFullModelDtoValidatorPositiveTest(String account, String corrAccount) {
        var accountCreate =
            Allure.step("Подготовка тестовых данных", () -> {
                var bank = factory.manufacturePojo(BankCreate.class);
                bank.getBankAccount().bankAccount(corrAccount);
                var accountPojo = factory.manufacturePojo(AccountCreateFullModel.class);
                accountPojo.account(account);
                accountPojo.bank(bank);
                return accountPojo;
            });
        var result =
            Allure.step("Произведение валидации", () -> validatorAccountFullModelCreate.isValid(accountCreate, context));
        Allure.step("Проверка полученного результата", () -> assertTrue(result));
    }

    static Stream<? extends Arguments> positiveArguments() {
        return Stream.of(
            Arguments.of(
                VALID_ACCOUNT, null
            ),
            Arguments.of(
                INVALID_ACCOUNT, BUDGET_CORR_ACCOUNT
            ),
            Arguments.of(
                null, null
            ),
            Arguments.of(
                "", null
            )
        );
    }

    @ParameterizedTest
    @MethodSource("negativeArguments")
    void accountAttributeRurCodeCurrencyAccountCreateDtoValidatorNegativeTest(String account, String corrAccount) {
        var accountCreate =
            Allure.step("Подготовка тестовых данных", () -> {
                var bank = factory.manufacturePojo(BankCreate.class);
                bank.getBankAccount().bankAccount(corrAccount);
                var accountPojo = factory.manufacturePojo(AccountCreate.class);
                accountPojo.account(account);
                accountPojo.bank(bank);
                return accountPojo;
            });
        var result =
            Allure.step("Произведение валидации", () ->
                validatorAccountCreate.isValid(accountCreate, context));
        Allure.step("Проверка полученного результата", () -> assertFalse(result));
    }

    @ParameterizedTest
    @MethodSource("negativeArguments")
    void accountAttributeRurCodeCurrencyAccountChangeDtoValidatorNegativeTest(String account, String corrAccount) {
        var accountCreate =
            Allure.step("Подготовка тестовых данных", () -> {
                var bank = factory.manufacturePojo(Bank.class);
                bank.getBankAccount().bankAccount(corrAccount);
                var accountPojo = factory.manufacturePojo(AccountChange.class);
                accountPojo.account(account);
                accountPojo.bank(bank);
                return accountPojo;
            });
        var result =
            Allure.step("Произведение валидации", () -> validatorAccountChange.isValid(accountCreate, context));
        Allure.step("Проверка полученного результата", () -> assertFalse(result));
    }

    @ParameterizedTest
    @MethodSource("negativeArguments")
    void accountAttributeRurCodeCurrencyAccountCreateFullModelDtoValidatorNegativeTest(String account, String corrAccount) {
        var accountCreate =
            Allure.step("Подготовка тестовых данных", () -> {
                var bank = factory.manufacturePojo(BankCreate.class);
                bank.getBankAccount().bankAccount(corrAccount);
                var accountPojo = factory.manufacturePojo(AccountCreateFullModel.class);
                accountPojo.account(account);
                accountPojo.bank(bank);
                return accountPojo;
            });
        var result =
            Allure.step("Произведение валидации", () -> validatorAccountFullModelCreate.isValid(accountCreate, context));
        Allure.step("Проверка полученного результата", () -> assertFalse(result));
    }

    static Stream<? extends Arguments> negativeArguments() {
        return Stream.of(
            Arguments.of(
                INVALID_ACCOUNT, INVALID_ACCOUNT
            ),
            Arguments.of(
                INVALID_ACCOUNT, null
            ),
            Arguments.of(
                INVALID_ACCOUNT, ""
            )
        );
    }
}
