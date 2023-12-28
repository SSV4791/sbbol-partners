package ru.sberbank.pprb.sbbol.partners.validation.account.account;

import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.BalanceTreasureAccountValidation;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerService;
import ru.sberbank.pprb.sbbol.partners.validation.account.BaseTreasuryAccountValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.util.Objects.isNull;

public class AccountAttributeBalanceTreasureAccountCreateDtoValidator extends BaseTreasuryAccountValidator
    implements ConstraintValidator<BalanceTreasureAccountValidation, AccountCreate> {
    private String message;

    public AccountAttributeBalanceTreasureAccountCreateDtoValidator(PartnerService partnerService) {
        super(partnerService);
    }

    @Override
    public void initialize(BalanceTreasureAccountValidation constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(AccountCreate value, ConstraintValidatorContext context) {
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
        return validateBalance(value.getAccount(), bankAccount.getBankAccount());
    }
}
