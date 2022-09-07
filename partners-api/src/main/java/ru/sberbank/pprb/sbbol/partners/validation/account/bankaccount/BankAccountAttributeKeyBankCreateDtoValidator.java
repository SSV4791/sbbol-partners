package ru.sberbank.pprb.sbbol.partners.validation.account.bankaccount;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.partners.model.BankAccountAttributeValidation;
import ru.sberbank.pprb.sbbol.partners.model.BankCreate;
import ru.sberbank.pprb.sbbol.partners.validation.account.BaseAccountKeyValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BankAccountAttributeKeyBankCreateDtoValidator extends BaseAccountKeyValidator
    implements ConstraintValidator<BankAccountAttributeValidation, BankCreate> {

    private String message;

    @Override
    public void initialize(BankAccountAttributeValidation constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(BankCreate value, ConstraintValidatorContext context) {
        buildMessage(context, "bankAccount.bankAccount", message);
        if (ObjectUtils.isEmpty(value)) {
            return true;
        }
        var bankAccountCreate = value.getBankAccount();
        if (ObjectUtils.isEmpty(bankAccountCreate)) {
            return true;
        }
        var bankAccount = bankAccountCreate.getBankAccount();
        if (StringUtils.isEmpty(bankAccount)) {
            return true;
        }
        var bic = value.getBic();
        if (StringUtils.isEmpty(bic)) {
            return true;
        }
        return validateBankAccount(bankAccount, bic);
    }
}
