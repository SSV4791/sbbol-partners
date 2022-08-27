package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.partners.model.AccountAttributeValidation;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.validation.common.AccountValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AccountAttributeAccountCreateDtoValidator
    implements ConstraintValidator<AccountAttributeValidation, AccountCreate> {


    @Override
    public boolean isValid(AccountCreate value, ConstraintValidatorContext context) {
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
