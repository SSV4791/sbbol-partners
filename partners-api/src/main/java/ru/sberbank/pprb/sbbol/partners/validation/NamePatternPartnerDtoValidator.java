package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.NamePatternPartnerDtoValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NamePatternPartnerDtoValidator extends BasePatternPartnerValidator
    implements ConstraintValidator<NamePatternPartnerDtoValidation, Partner> {

    @Override
    public boolean isValid(Partner value, ConstraintValidatorContext context) {
        if (value != null) {
            return isValid(context, value.getOrgName(), value.getFirstName(), value.getSecondName(), value.getMiddleName());
        }
        return true;
    }
}
