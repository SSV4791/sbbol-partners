package ru.sberbank.pprb.sbbol.partners.validation.account.account;

import org.apache.commons.lang3.ObjectUtils;
import ru.sberbank.pprb.sbbol.partners.model.AccountAttributeBalanceValidation;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AccountAttributeBalancePhysicalPersonAccountChangeDtoValidator extends BaseBalanceAccountValidator
    implements ConstraintValidator<AccountAttributeBalanceValidation, AccountChange> {

    public AccountAttributeBalancePhysicalPersonAccountChangeDtoValidator(PartnerService partnerService) {
        super(partnerService);
    }

    @Override
    public boolean isValid(AccountChange value, ConstraintValidatorContext context) {
        if (ObjectUtils.isEmpty(value)) {
            return true;
        }
        return isValid(context, value.getAccount(), value.getDigitalId(), value.getPartnerId());
    }
}
