package ru.sberbank.pprb.sbbol.partners.validation.account.account;

import org.apache.commons.lang3.ObjectUtils;
import ru.sberbank.pprb.sbbol.partners.model.AccountBalanceValidation;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AccountAttributeBalancePhysicalPersonAccountCreateDtoValidator extends BaseBalanceAccountValidator
    implements ConstraintValidator<AccountBalanceValidation, AccountCreate> {

    public AccountAttributeBalancePhysicalPersonAccountCreateDtoValidator(PartnerService partnerService) {
        super(partnerService);
    }

    @Override
    public boolean isValid(AccountCreate value, ConstraintValidatorContext context) {
        if (ObjectUtils.isEmpty(value)) {
            return true;
        }
        return isValid(context, value.getAccount(), value.getDigitalId(), value.getPartnerId());
    }
}
