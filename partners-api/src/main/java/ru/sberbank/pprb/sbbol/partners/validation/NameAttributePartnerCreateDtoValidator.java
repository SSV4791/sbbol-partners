package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.model.NameAttributePartnerCreateDtoValidation;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NameAttributePartnerCreateDtoValidator extends BaseNameAttributeValidator
    implements ConstraintValidator<NameAttributePartnerCreateDtoValidation, PartnerCreate> {

    @Override
    public boolean isValid(PartnerCreate value, ConstraintValidatorContext context) {
        if (value != null) {
            return isValid(context, value.getLegalForm(), value.getOrgName(), value.getFirstName());
        }
        return true;
    }
}
