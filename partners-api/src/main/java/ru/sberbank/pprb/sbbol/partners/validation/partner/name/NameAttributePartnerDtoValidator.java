package ru.sberbank.pprb.sbbol.partners.validation.partner.name;

import org.apache.commons.lang3.ObjectUtils;
import ru.sberbank.pprb.sbbol.partners.model.NameAttributePartnerValidation;
import ru.sberbank.pprb.sbbol.partners.model.Partner;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NameAttributePartnerDtoValidator extends BaseNameAttributeValidator
    implements ConstraintValidator<NameAttributePartnerValidation, Partner> {

    @Override
    public boolean isValid(Partner value, ConstraintValidatorContext context) {
        if (!ObjectUtils.isEmpty(value)) {
            return isValid(context, value.getLegalForm(), value.getOrgName(), value.getFirstName());
        }
        return true;
    }
}
