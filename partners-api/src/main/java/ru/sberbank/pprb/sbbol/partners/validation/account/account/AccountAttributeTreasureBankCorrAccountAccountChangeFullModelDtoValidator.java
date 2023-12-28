package ru.sberbank.pprb.sbbol.partners.validation.account.account;

import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.model.AccountChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.PartnerChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.TreasureBankCorrAccountCodeCurrencyAndTreasureBalanceValidation;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerService;
import ru.sberbank.pprb.sbbol.partners.validation.account.BaseTreasuryAccountValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

import static java.util.Objects.isNull;

public class AccountAttributeTreasureBankCorrAccountAccountChangeFullModelDtoValidator extends BaseTreasuryAccountValidator
    implements ConstraintValidator<TreasureBankCorrAccountCodeCurrencyAndTreasureBalanceValidation, PartnerChangeFullModel> {
    private String message;

    public AccountAttributeTreasureBankCorrAccountAccountChangeFullModelDtoValidator(PartnerService partnerService) {
        super(partnerService);
    }

    @Override
    public void initialize(TreasureBankCorrAccountCodeCurrencyAndTreasureBalanceValidation constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(PartnerChangeFullModel value, ConstraintValidatorContext context) {
        if (isNull(value)) {
            return true;
        }
        Set<AccountChangeFullModel> accounts = value.getAccounts();
        if (CollectionUtils.isEmpty(accounts)) {
            return true;
        }
        var result = true;
        var accountCounter = 0;
        for (var account: accounts) {
            var bank = account.getBank();
            if (isNull(bank)) {
                break;
            }
            var bankAccount = bank.getBankAccount();
            if (isNull(bankAccount)) {
                break;
            }
            if (!validateCorrAccount(account.getAccount(), bankAccount.getBankAccount())) {
                buildMessage(context, String.format("accounts[%s].bank.bankAccount.bankAccount", accountCounter), message);
                result = false;
            }
            accountCounter++;
        }
        return result;
    }
}
