package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.partners.model.AccountAttributeAccountChangeDtoValidation;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.validation.common.AccountValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AccountAttributeAccountChangeDtoValidator
    implements ConstraintValidator<AccountAttributeAccountChangeDtoValidation, AccountChange> {


    @Override
    public boolean isValid(AccountChange value, ConstraintValidatorContext context) {
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
