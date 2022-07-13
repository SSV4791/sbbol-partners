package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.BankAccountAccountFullModelValidation;
import ru.sberbank.pprb.sbbol.partners.validation.common.AccountValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BankAccountAttributeAccountFullModelDtoValidation
    implements ConstraintValidator<BankAccountAccountFullModelValidation, AccountCreateFullModel> {

    @Override
    public boolean isValid(AccountCreateFullModel value, ConstraintValidatorContext context) {
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
        return AccountValidation.validateBankAccount(bankAccount.getBankAccount(), bank.getBic());
    }
}
