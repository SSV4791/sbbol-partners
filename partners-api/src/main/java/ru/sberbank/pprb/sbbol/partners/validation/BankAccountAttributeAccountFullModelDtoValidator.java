package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.BankAccountAttributeAccountFullModelDtoValidation;
import ru.sberbank.pprb.sbbol.partners.validation.common.AccountValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BankAccountAttributeAccountFullModelDtoValidator
    implements ConstraintValidator<BankAccountAttributeAccountFullModelDtoValidation, AccountCreateFullModel> {

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
        return AccountValidator.validateBankAccount(bankAccount.getBankAccount(), bank.getBic());
    }
}
