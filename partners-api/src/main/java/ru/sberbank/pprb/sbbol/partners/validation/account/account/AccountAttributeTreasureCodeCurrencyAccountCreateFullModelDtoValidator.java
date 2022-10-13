package ru.sberbank.pprb.sbbol.partners.validation.account.account;

import org.apache.commons.lang3.ObjectUtils;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.TreasureAccountCodeCurrencyValidation;
import ru.sberbank.pprb.sbbol.partners.validation.account.BaseTreasuryAccountValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AccountAttributeTreasureCodeCurrencyAccountCreateFullModelDtoValidator extends BaseTreasuryAccountValidator
    implements ConstraintValidator<TreasureAccountCodeCurrencyValidation, AccountCreateFullModel> {
    private String message;

    @Override
    public void initialize(TreasureAccountCodeCurrencyValidation constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(AccountCreateFullModel value, ConstraintValidatorContext context) {
        buildMessage(context, "account", message);
        if (value == null) {
            return true;
        }
        var bank = value.getBank();
        if (ObjectUtils.isEmpty(bank)) {
            return true;
        }
        var bankAccount = bank.getBankAccount();
        if (bankAccount == null) {
            return true;
        }
        return validateCodeCurrency(value.getAccount(), bankAccount.getBankAccount());
    }
}
