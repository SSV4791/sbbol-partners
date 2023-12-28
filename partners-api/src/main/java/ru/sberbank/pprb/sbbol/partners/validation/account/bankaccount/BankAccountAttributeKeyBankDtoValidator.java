package ru.sberbank.pprb.sbbol.partners.validation.account.bankaccount;

import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.partners.model.Bank;
import ru.sberbank.pprb.sbbol.partners.model.BankAccountValidation;
import ru.sberbank.pprb.sbbol.partners.validation.account.BaseAccountKeyValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.util.Objects.isNull;

public class BankAccountAttributeKeyBankDtoValidator extends BaseAccountKeyValidator
    implements ConstraintValidator<BankAccountValidation, Bank> {

    private String message;

    @Override
    public void initialize(BankAccountValidation constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Bank value, ConstraintValidatorContext context) {
        buildMessage(context, "bankAccount.bankAccount", message);
        if (isNull(value)) {
            return true;
        }
        var bankAccount = value.getBankAccount();
        if (isNull(bankAccount)) {
            return true;
        }
        var account = bankAccount.getBankAccount();
        if (StringUtils.isEmpty(account)) {
            return true;
        }
        var bic = value.getBic();
        if (StringUtils.isEmpty(bic)) {
            return true;
        }
        return validateBankAccount(account, bic);
    }
}
