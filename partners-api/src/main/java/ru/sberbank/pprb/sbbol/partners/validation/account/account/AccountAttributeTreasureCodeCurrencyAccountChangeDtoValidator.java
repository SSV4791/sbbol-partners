package ru.sberbank.pprb.sbbol.partners.validation.account.account;

import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.TreasureAccountCodeCurrencyValidation;
import ru.sberbank.pprb.sbbol.partners.validation.account.BaseTreasuryAccountValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AccountAttributeTreasureCodeCurrencyAccountChangeDtoValidator extends BaseTreasuryAccountValidator
    implements ConstraintValidator<TreasureAccountCodeCurrencyValidation, AccountChange> {
    private String message;

    @Override
    public void initialize(TreasureAccountCodeCurrencyValidation constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(AccountChange value, ConstraintValidatorContext context) {
        buildMessage(context, "account", message);
        if (value == null) {
            return true;
        }
        var bank = value.getBank();
        if (bank == null) {
            return true;
        }
        var bankAccount = bank.getBankAccount();
        if (bankAccount == null) {
            return true;
        }
        return validateCodeCurrency(value.getAccount(), bankAccount.getBankAccount());
    }
}