package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.NamePatternPartnerCreateFullModelDtoValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NamePatternPartnerCreateFullModelDtoValidator extends BasePatternPartnerValidator
    implements ConstraintValidator<NamePatternPartnerCreateFullModelDtoValidation, PartnerCreateFullModel> {

    @Override
    public boolean isValid(PartnerCreateFullModel value, ConstraintValidatorContext context) {
        if (value != null) {
            return isValid(context, value.getOrgName(), value.getFirstName(), value.getSecondName(), value.getMiddleName());
        }
        return true;
    }
}
