package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.AccountAccountFullModelValidation;
import ru.sberbank.pprb.sbbol.partners.validation.common.AccountValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AccountAttributeAccountFullModelDtoValidation
    implements ConstraintValidator<AccountAccountFullModelValidation, AccountCreateFullModel> {

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
        return AccountValidation.isValidAccount(account, bank.getBic());
    }
}
