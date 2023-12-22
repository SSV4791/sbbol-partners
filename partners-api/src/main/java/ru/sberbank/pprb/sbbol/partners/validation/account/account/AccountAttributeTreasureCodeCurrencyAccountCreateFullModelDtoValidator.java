package ru.sberbank.pprb.sbbol.partners.validation.account.account;

import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.TreasureAccountCodeCurrencyValidation;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerService;
import ru.sberbank.pprb.sbbol.partners.validation.account.BaseTreasuryAccountValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Iterator;
import java.util.Set;

import static java.util.Objects.isNull;

public class AccountAttributeTreasureCodeCurrencyAccountCreateFullModelDtoValidator extends BaseTreasuryAccountValidator
    implements ConstraintValidator<TreasureAccountCodeCurrencyValidation, PartnerCreateFullModel> {
    private String message;

    public AccountAttributeTreasureCodeCurrencyAccountCreateFullModelDtoValidator(PartnerService partnerService) {
        super(partnerService);
    }

    @Override
    public void initialize(TreasureAccountCodeCurrencyValidation constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(PartnerCreateFullModel value, ConstraintValidatorContext context) {
        if (isNull(value)) {
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
            if (isNull(bank)) {
                return true;
            }
            var bankAccount = bank.getBankAccount();
            if (isNull(bankAccount)) {
                return true;
            }
            buildMessage(context, String.format("accounts[%s].account", i), message);
            return validateCodeCurrency(next.getAccount(), bankAccount.getBankAccount());
        }
        return false;
    }
}
