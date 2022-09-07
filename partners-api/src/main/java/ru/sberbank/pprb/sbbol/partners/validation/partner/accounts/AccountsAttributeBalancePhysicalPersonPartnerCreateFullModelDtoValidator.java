package ru.sberbank.pprb.sbbol.partners.validation.partner.accounts;

import org.apache.commons.lang3.ObjectUtils;
import ru.sberbank.pprb.sbbol.partners.model.AccountAttributeBalanceValidation;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerService;
import ru.sberbank.pprb.sbbol.partners.validation.account.account.BaseBalanceAccountValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

public class AccountsAttributeBalancePhysicalPersonPartnerCreateFullModelDtoValidator extends BaseBalanceAccountValidator
    implements ConstraintValidator<AccountAttributeBalanceValidation, PartnerCreateFullModel> {

    public AccountsAttributeBalancePhysicalPersonPartnerCreateFullModelDtoValidator(PartnerService partnerService) {
        super(partnerService);
    }

    @Override
    public boolean isValid(PartnerCreateFullModel value, ConstraintValidatorContext context) {
        if (ObjectUtils.isEmpty(value)) {
            return true;
        }
        Set<AccountCreateFullModel> accounts = value.getAccounts();
        if (accounts == null || accounts.isEmpty()) {
            return true;
        }
        for (AccountCreateFullModel account : accounts) {
            var bank = account.getBank();
            if (!ObjectUtils.isEmpty(bank)) {
                var acc = account.getAccount();
                if (!isValid(context, "accounts", acc, value.getLegalForm())) {
                    return false;
                }
            }
        }
        return true;
    }
}
