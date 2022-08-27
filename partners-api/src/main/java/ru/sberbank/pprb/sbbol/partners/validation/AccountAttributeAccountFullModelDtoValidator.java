package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.partners.model.AccountAttributeValidation;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.validation.common.AccountValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AccountAttributeAccountFullModelDtoValidator
    implements ConstraintValidator<AccountAttributeValidation, AccountCreateFullModel> {

    @Override
    public boolean isValid(AccountCreateFullModel value, ConstraintValidatorContext context) {
        if (ObjectUtils.isEmpty(value)) {
            return true;
        }
        var bank = value.getBank();
        if (StringUtils.isEmpty(bank.getBic())) {
            return true;
        }
        var account = value.getAccount();
        return AccountValidator.isValidAccount(account, bank.getBic());
    }
}
