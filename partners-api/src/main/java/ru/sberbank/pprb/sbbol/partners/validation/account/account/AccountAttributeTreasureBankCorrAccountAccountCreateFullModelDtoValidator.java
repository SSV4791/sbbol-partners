package ru.sberbank.pprb.sbbol.partners.validation.account.account;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.TreasureBankCorrAccountCodeCurrencyAndTreasureBalanceValidation;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerService;
import ru.sberbank.pprb.sbbol.partners.validation.account.BaseTreasuryAccountValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Iterator;
import java.util.Set;

public class AccountAttributeTreasureBankCorrAccountAccountCreateFullModelDtoValidator extends BaseTreasuryAccountValidator
    implements ConstraintValidator<TreasureBankCorrAccountCodeCurrencyAndTreasureBalanceValidation, PartnerCreateFullModel> {
    private String message;

    public AccountAttributeTreasureBankCorrAccountAccountCreateFullModelDtoValidator(PartnerService partnerService) {
        super(partnerService);
    }

    @Override
    public void initialize(TreasureBankCorrAccountCodeCurrencyAndTreasureBalanceValidation constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(PartnerCreateFullModel value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        Set<AccountCreateFullModel> accounts = value.getAccounts();
        if (CollectionUtils.isEmpty(accounts)) {
            return true;
        }
        Iterator<AccountCreateFullModel> iterator = accounts.iterator();
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
            buildMessage(context, String.format("accounts[%s].bank.bankAccount.bankAccount", i), message);
            return validateCorrAccount(next.getAccount(), bankAccount.getBankAccount());
        }
        return false;
    }
}
