package ru.sberbank.pprb.sbbol.partners.validation.account.account;

import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountRubCodeCurrencyValidation;
import ru.sberbank.pprb.sbbol.partners.validation.account.BaseAccountRubCodeCurrencyValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.util.Objects.isNull;

public class AccountAttributeRurCodeCurrencyAccountChangeDtoValidator extends BaseAccountRubCodeCurrencyValidator
    implements ConstraintValidator<AccountRubCodeCurrencyValidation, AccountChange> {
    private String message;

    @Override
    public void initialize(AccountRubCodeCurrencyValidation constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(AccountChange value, ConstraintValidatorContext context) {
        buildMessage(context, "account", message);
        if (isNull(value)) {
            return true;
        }
        var bank = value.getBank();
        if (isNull(bank)) {
            return validate(value.getAccount(), null);
        }
        var bankAccount = bank.getBankAccount();
        if (isNull(bankAccount)) {
            return validate(value.getAccount(), null);
        }
        return validate(value.getAccount(), bankAccount.getBankAccount());
    }
}
