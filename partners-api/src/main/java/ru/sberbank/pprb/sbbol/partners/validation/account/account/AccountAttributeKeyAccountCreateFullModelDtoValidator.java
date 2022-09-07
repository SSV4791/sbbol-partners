package ru.sberbank.pprb.sbbol.partners.validation.account.account;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.partners.model.AccountAttributeKeyValidation;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.validation.account.BaseAccountKeyValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AccountAttributeKeyAccountCreateFullModelDtoValidator extends BaseAccountKeyValidator
    implements ConstraintValidator<AccountAttributeKeyValidation, AccountCreateFullModel> {

    private String message;

    @Override
    public void initialize(AccountAttributeKeyValidation constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(AccountCreateFullModel value, ConstraintValidatorContext context) {
        buildMessage(context, "account", message);
        if (ObjectUtils.isEmpty(value)) {
            return true;
        }
        var bank = value.getBank();
        if (StringUtils.isEmpty(bank.getBic())) {
            return true;
        }
        var account = value.getAccount();
        return isValidAccount(account, bank.getBic());
    }
}
