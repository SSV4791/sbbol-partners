package ru.sberbank.pprb.sbbol.partners.validation.account.account;

import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.BankAccountKeyValidation;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerService;
import ru.sberbank.pprb.sbbol.partners.validation.account.BaseTreasuryAccountValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class BankAccountAttributeKeyBankAccountForNotLegalEntityAccountCreateDtoValidator extends BaseTreasuryAccountValidator
    implements ConstraintValidator<BankAccountKeyValidation, AccountCreate> {

    private String message;

    public BankAccountAttributeKeyBankAccountForNotLegalEntityAccountCreateDtoValidator(PartnerService partnerService) {
        super(partnerService);
    }

    @Override
    public void initialize(BankAccountKeyValidation constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(AccountCreate value, ConstraintValidatorContext context) throws EntryNotFoundException {
        buildMessage(context, "bank.bankAccount.bankAccount", message);
        if (isNull(value)) {
            return true;
        }
        String account = value.getAccount();
        if (isEmpty(account)) {
            return true;
        }
        var bank = value.getBank();
        if (isNull(bank)) {
            return true;
        }
        var bankAccount = bank.getBankAccount();
        if (isNull(bankAccount)) {
            return true;
        }
        return isBudgetCorrAccount(value.getDigitalId(), value.getPartnerId(), bankAccount.getBankAccount());
    }
}
