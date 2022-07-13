package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountAccountChangeValidation;
import ru.sberbank.pprb.sbbol.partners.validation.common.AccountValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AccountAttributeAccountChangeDtoValidation
    implements ConstraintValidator<AccountAccountChangeValidation, AccountChange> {


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
        return AccountValidation.isValidAccount(account, bank.getBic());
    }
}
