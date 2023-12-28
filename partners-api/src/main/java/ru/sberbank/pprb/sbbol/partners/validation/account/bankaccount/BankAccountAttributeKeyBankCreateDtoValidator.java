package ru.sberbank.pprb.sbbol.partners.validation.account.bankaccount;

import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.partners.model.BankAccountValidation;
import ru.sberbank.pprb.sbbol.partners.model.BankCreate;
import ru.sberbank.pprb.sbbol.partners.validation.account.BaseAccountKeyValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.util.Objects.isNull;

public class BankAccountAttributeKeyBankCreateDtoValidator extends BaseAccountKeyValidator
    implements ConstraintValidator<BankAccountValidation, BankCreate> {

    private String message;

    @Override
    public void initialize(BankAccountValidation constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(BankCreate value, ConstraintValidatorContext context) {
        buildMessage(context, "bankAccount.bankAccount", message);
        if (isNull(value)) {
            return true;
        }
        var bankAccountCreate = value.getBankAccount();
        if (isNull(bankAccountCreate)) {
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
