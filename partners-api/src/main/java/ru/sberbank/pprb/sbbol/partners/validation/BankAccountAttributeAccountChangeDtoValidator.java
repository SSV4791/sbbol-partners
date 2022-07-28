package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.BankAccountAttributeAccountChangeDtoValidation;
import ru.sberbank.pprb.sbbol.partners.validation.common.AccountValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BankAccountAttributeAccountChangeDtoValidator
    implements ConstraintValidator<BankAccountAttributeAccountChangeDtoValidation, AccountChange> {

    @Override
    public boolean isValid(AccountChange value, ConstraintValidatorContext context) {
        if (ObjectUtils.isEmpty(value)) {
            return true;
        }
        var bank = value.getBank();
        if (StringUtils.isEmpty(bank.getBic())) {
            return true;
        }
        var bankAccount = bank.getBankAccount();
        if (bankAccount == null || StringUtils.isEmpty(bankAccount.getBankAccount())) {
            return true;
        }
        return AccountValidator.validateBankAccount(bankAccount.getBankAccount(), bank.getBic());
    }
}
