package ru.sberbank.pprb.sbbol.partners.validation.account.account;

import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountRubCodeCurrencyValidation;
import ru.sberbank.pprb.sbbol.partners.validation.account.BaseAccountRubCodeCurrencyValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

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
        if (value == null) {
            return true;
        }
        return validate(value.getAccount());
    }
}