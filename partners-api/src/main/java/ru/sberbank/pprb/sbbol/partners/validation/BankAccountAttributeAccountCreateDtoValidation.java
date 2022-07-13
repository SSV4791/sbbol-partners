package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.BankAccountAccountCreateValidation;
import ru.sberbank.pprb.sbbol.partners.validation.common.AccountValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BankAccountAttributeAccountCreateDtoValidation
    implements ConstraintValidator<BankAccountAccountCreateValidation, AccountCreate> {

    @Override
    public boolean isValid(AccountCreate value, ConstraintValidatorContext context) {
        if (ObjectUtils.isEmpty(value)) {
            return true;
        }
        var bank = value.getBank();
        if (StringUtils.isEmpty(bank.getBic() )) {
            return true;
        }
        var bankAccount = bank.getBankAccount();
        if (bankAccount == null || StringUtils.isEmpty(bankAccount.getBankAccount())) {
            return true;
        }
        return AccountValidation.validateBankAccount(bankAccount.getBankAccount(), bank.getBic());
    }
}
