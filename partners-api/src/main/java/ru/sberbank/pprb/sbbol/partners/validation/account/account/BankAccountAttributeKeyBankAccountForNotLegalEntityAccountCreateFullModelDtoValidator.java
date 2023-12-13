package ru.sberbank.pprb.sbbol.partners.validation.account.account;

import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.model.BankAccountKeyValidation;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerService;
import ru.sberbank.pprb.sbbol.partners.service.legalform.LegalFormInspector;
import ru.sberbank.pprb.sbbol.partners.validation.account.BaseTreasuryAccountValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

public class BankAccountAttributeKeyBankAccountForNotLegalEntityAccountCreateFullModelDtoValidator extends BaseTreasuryAccountValidator
    implements ConstraintValidator<BankAccountKeyValidation, PartnerCreateFullModel> {

    private final LegalFormInspector legalFormInspector;

    private String message;

    public BankAccountAttributeKeyBankAccountForNotLegalEntityAccountCreateFullModelDtoValidator(PartnerService partnerService, LegalFormInspector legalFormInspector) {
        super(partnerService);
        this.legalFormInspector = legalFormInspector;
    }

    @Override
    public void initialize(BankAccountKeyValidation constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(PartnerCreateFullModel value, ConstraintValidatorContext context) throws EntryNotFoundException {
        if (isEmpty(value)) {
            return true;
        }

        var accounts = value.getAccounts();
        if (isEmpty(accounts)) {
            return true;
        }

        legalFormInspector.setLegalFormAndPartnerName(value);

        if (value.getLegalForm() == LegalForm.PHYSICAL_PERSON ||
            value.getLegalForm() == LegalForm.ENTREPRENEUR) {
            var iterator = accounts.iterator();
            var result = true;
            for (var i = 0; i < accounts.size(); i++) {
                var next = iterator.next();
                var bank = next.getBank();
                if (isEmpty(bank)) {
                    return true;
                }
                var bankAccount = bank.getBankAccount();
                if (isEmpty(bankAccount)) {
                    return true;
                }
                if (isBudgetCorrAccount(bankAccount.getBankAccount())) {
                    buildMessage(context, String.format("accounts[%s].bank.bankAccount.bankAccount", i), message);
                    result = false;
                }
            }
            return result;
        }
        return true;
    }
}
