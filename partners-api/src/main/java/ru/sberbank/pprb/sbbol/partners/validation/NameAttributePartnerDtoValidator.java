package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.model.NameAttributePartnerDtoValidation;
import ru.sberbank.pprb.sbbol.partners.model.Partner;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NameAttributePartnerDtoValidator extends BaseNameAttributeValidator
    implements ConstraintValidator<NameAttributePartnerDtoValidation, Partner> {

    @Override
    public boolean isValid(Partner value, ConstraintValidatorContext context) {
        if (value != null) {
            return isValid(context, value.getLegalForm(), value.getOrgName(), value.getFirstName());
        }
        return true;
    }
}
