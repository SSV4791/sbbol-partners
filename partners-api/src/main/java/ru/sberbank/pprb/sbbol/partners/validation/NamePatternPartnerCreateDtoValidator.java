package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;
import ru.sberbank.pprb.sbbol.partners.model.NamePatternPartnerCreateDtoValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NamePatternPartnerCreateDtoValidator extends BasePatternPartnerValidator
    implements ConstraintValidator<NamePatternPartnerCreateDtoValidation, PartnerCreate> {

    @Override
    public boolean isValid(PartnerCreate value, ConstraintValidatorContext context) {
        if (value != null) {
            return isValid(context, value.getOrgName(), value.getFirstName(), value.getSecondName(), value.getMiddleName());
        }
        return true;
    }
}
