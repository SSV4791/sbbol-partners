package ru.sberbank.pprb.sbbol.partners.validation.account.account;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.model.AccountChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.BankAccountKeyValidation;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.PartnerChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerService;
import ru.sberbank.pprb.sbbol.partners.validation.account.BaseTreasuryAccountValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

public class BankAccountAttributeKeyBankAccountForNotLegalEntityAccountChangeFullModelDtoValidator extends BaseTreasuryAccountValidator
    implements ConstraintValidator<BankAccountKeyValidation, PartnerChangeFullModel> {

    private String message;

    public BankAccountAttributeKeyBankAccountForNotLegalEntityAccountChangeFullModelDtoValidator(PartnerService partnerService) {
        super(partnerService);
    }

    @Override
    public void initialize(BankAccountKeyValidation constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(PartnerChangeFullModel value, ConstraintValidatorContext context) throws EntryNotFoundException {
        if (value == null) {
            return true;
        }

        Set<AccountChangeFullModel> accounts = value.getAccounts();
        if (CollectionUtils.isEmpty(accounts)) {
            return true;
        }
        if (value.getLegalForm() == LegalForm.PHYSICAL_PERSON ||
            value.getLegalForm() == LegalForm.ENTREPRENEUR) {
            var result = true;
            var accountCounter = 0;
            for (var account: accounts) {
                var bank = account.getBank();
                if (ObjectUtils.isEmpty(bank)) {
                    break;
                }
                var bankAccount = bank.getBankAccount();
                if (bankAccount == null) {
                    break;
                }
                if (!isBudgetCorrAccount(bankAccount.getBankAccount())) {
                    buildMessage(context, String.format("accounts[%s].bank.bankAccount.bankAccount", accountCounter), message);
                    result = false;
                }
                accountCounter++;
            }
            return result;
        }
        return true;
    }
}
