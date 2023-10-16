package ru.sberbank.pprb.sbbol.partners.validation.account.account;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.BankAccountKeyValidation;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerService;
import ru.sberbank.pprb.sbbol.partners.validation.account.BaseTreasuryAccountValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Iterator;
import java.util.Set;

public class BankAccountAttributeKeyBankAccountForNotLegalEntityAccountCreateFullModelDtoValidator extends BaseTreasuryAccountValidator
    implements ConstraintValidator<BankAccountKeyValidation, PartnerCreateFullModel> {

    private String message;

    public BankAccountAttributeKeyBankAccountForNotLegalEntityAccountCreateFullModelDtoValidator(PartnerService partnerService) {
        super(partnerService);
    }

    @Override
    public void initialize(BankAccountKeyValidation constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(PartnerCreateFullModel value, ConstraintValidatorContext context) throws EntryNotFoundException {
        if (value == null) {
            return true;
        }

        Set<AccountCreateFullModel> accounts = value.getAccounts();
        if (CollectionUtils.isEmpty(accounts)) {
            return true;
        }
        if (value.getLegalForm() == LegalForm.PHYSICAL_PERSON ||
            value.getLegalForm() == LegalForm.ENTREPRENEUR) {
            Iterator<AccountCreateFullModel> iterator = accounts.iterator();
            var result = true;
            for (var i = 0; i < accounts.size(); i++) {
                AccountCreateFullModel next = iterator.next();
                var bank = next.getBank();
                if (ObjectUtils.isEmpty(bank)) {
                    return true;
                }
                var bankAccount = bank.getBankAccount();
                if (bankAccount == null) {
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
