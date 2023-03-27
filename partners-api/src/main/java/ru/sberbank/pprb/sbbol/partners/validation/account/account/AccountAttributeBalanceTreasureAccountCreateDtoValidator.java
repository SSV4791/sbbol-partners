package ru.sberbank.pprb.sbbol.partners.validation.account.account;

import org.apache.commons.lang3.ObjectUtils;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.BalanceTreasureAccountValidation;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerService;
import ru.sberbank.pprb.sbbol.partners.validation.account.BaseTreasuryAccountValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

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
        if (value == null) {
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
        return validateBalance(value.getAccount(), bankAccount.getBankAccount());
    }
}
