package ru.sberbank.pprb.sbbol.partners.validation.account.account;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.model.AccountChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.PartnerChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.TreasureAccountCodeCurrencyValidation;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerService;
import ru.sberbank.pprb.sbbol.partners.validation.account.BaseTreasuryAccountValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

public class AccountAttributeTreasureCodeCurrencyAccountChangeFullModelDtoValidator extends BaseTreasuryAccountValidator
    implements ConstraintValidator<TreasureAccountCodeCurrencyValidation, PartnerChangeFullModel> {
    private String message;

    public AccountAttributeTreasureCodeCurrencyAccountChangeFullModelDtoValidator(PartnerService partnerService) {
        super(partnerService);
    }

    @Override
    public void initialize(TreasureAccountCodeCurrencyValidation constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(PartnerChangeFullModel value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        Set<AccountChangeFullModel> accounts = value.getAccounts();
        if (CollectionUtils.isEmpty(accounts)) {
            return true;
        }
        var result = true;
        var accountCounter = 0;
        for (var account : accounts) {
            var bank = account.getBank();
            if (ObjectUtils.isEmpty(bank)) {
                break;
            }
            var bankAccount = bank.getBankAccount();
            if (bankAccount == null) {
                break;
            }
            if (!validateCodeCurrency(account.getAccount(), bankAccount.getBankAccount())) {
                buildMessage(context, String.format("accounts[%s].account", accountCounter), message);
                result = false;
            }
            accountCounter++;
        }
        return result;
    }
}
