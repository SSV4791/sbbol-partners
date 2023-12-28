package ru.sberbank.pprb.sbbol.partners.validation.account.account;

import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.partners.model.AccountKeyValidation;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.validation.account.BaseAccountKeyValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.util.Objects.isNull;

public class AccountAttributeKeyAccountCreateFullModelDtoValidator extends BaseAccountKeyValidator
    implements ConstraintValidator<AccountKeyValidation, AccountCreateFullModel> {

    private String message;

    @Override
    public void initialize(AccountKeyValidation constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(AccountCreateFullModel value, ConstraintValidatorContext context) {
        buildMessage(context, "account", message);
        if (isNull(value)) {
            return true;
        }
        var bank = value.getBank();
        if (isNull(bank) || StringUtils.isEmpty(bank.getBic())) {
            return true;
        }
        var account = value.getAccount();
        return isValidAccount(account, bank.getBic());
    }
}
