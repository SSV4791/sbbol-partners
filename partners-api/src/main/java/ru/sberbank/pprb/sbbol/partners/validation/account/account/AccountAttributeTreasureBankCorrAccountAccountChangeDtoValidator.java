package ru.sberbank.pprb.sbbol.partners.validation.account.account;

import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.TreasureBankCorrAccountCodeCurrencyAndTreasureBalanceValidation;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerService;
import ru.sberbank.pprb.sbbol.partners.validation.account.BaseTreasuryAccountValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.util.Objects.isNull;

public class AccountAttributeTreasureBankCorrAccountAccountChangeDtoValidator extends BaseTreasuryAccountValidator
    implements ConstraintValidator<TreasureBankCorrAccountCodeCurrencyAndTreasureBalanceValidation, AccountChange> {

    private String message;

    public AccountAttributeTreasureBankCorrAccountAccountChangeDtoValidator(PartnerService partnerService) {
        super(partnerService);
    }

    @Override
    public void initialize(TreasureBankCorrAccountCodeCurrencyAndTreasureBalanceValidation constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(AccountChange value, ConstraintValidatorContext context) {
        buildMessage(context, "account", message);
        if (isNull(value)) {
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
        return validateCorrAccount(value.getAccount(), bankAccount.getBankAccount());
    }
}
