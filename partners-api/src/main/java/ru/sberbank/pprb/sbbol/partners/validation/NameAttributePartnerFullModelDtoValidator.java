package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.model.NameAttributePartnerFullModelDtoValidation;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NameAttributePartnerFullModelDtoValidator extends BaseNameAttributeValidator
    implements ConstraintValidator<NameAttributePartnerFullModelDtoValidation, PartnerCreateFullModel> {

    @Override
    public boolean isValid(PartnerCreateFullModel value, ConstraintValidatorContext context) {
        if (value != null) {
            return isValid(context, value.getLegalForm(), value.getOrgName(), value.getFirstName());
        }
        return true;
    }
}
