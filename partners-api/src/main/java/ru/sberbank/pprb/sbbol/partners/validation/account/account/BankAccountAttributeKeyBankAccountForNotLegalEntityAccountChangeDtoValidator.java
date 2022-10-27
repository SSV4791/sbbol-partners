package ru.sberbank.pprb.sbbol.partners.validation.account.account;

import org.apache.commons.lang3.ObjectUtils;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.BankAccountKeyValidation;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerService;
import ru.sberbank.pprb.sbbol.partners.validation.account.BaseTreasuryAccountValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BankAccountAttributeKeyBankAccountForNotLegalEntityAccountChangeDtoValidator extends BaseTreasuryAccountValidator
    implements ConstraintValidator<BankAccountKeyValidation, AccountChange> {

    private String message;

    public BankAccountAttributeKeyBankAccountForNotLegalEntityAccountChangeDtoValidator(PartnerService partnerService) {
        super(partnerService);
    }

    @Override
    public void initialize(BankAccountKeyValidation constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(AccountChange value, ConstraintValidatorContext context) throws EntryNotFoundException {
        buildMessage(context, "bank.bankAccount.bankAccount", message);
        if (ObjectUtils.isEmpty(value)) {
            return true;
        }
        String account = value.getAccount();
        if (ObjectUtils.isEmpty(account)) {
            return true;
        }
        var bank = value.getBank();
        if (ObjectUtils.isEmpty(bank)) {
            return true;
        }
        var bankAccount = bank.getBankAccount();
        if (ObjectUtils.isEmpty(bankAccount)) {
            return true;
        }
        return isBudgetCorrAccount(value.getDigitalId(), value.getPartnerId(), bankAccount.getBankAccount());
    }
}
