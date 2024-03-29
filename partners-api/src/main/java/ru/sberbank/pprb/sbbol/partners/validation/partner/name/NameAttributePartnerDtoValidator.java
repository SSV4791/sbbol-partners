package ru.sberbank.pprb.sbbol.partners.validation.partner.name;

import ru.sberbank.pprb.sbbol.partners.model.PartnerNameValidation;
import ru.sberbank.pprb.sbbol.partners.model.Partner;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.util.Objects.isNull;

public class NameAttributePartnerDtoValidator extends BaseNameAttributeValidator
    implements ConstraintValidator<PartnerNameValidation, Partner> {

    @Override
    public boolean isValid(Partner value, ConstraintValidatorContext context) {
        if (isNull(value)) {
            return true;
        }
        return isValid(context, value.getLegalForm(), value.getOrgName(), value.getFirstName(), value.getSecondName());
    }
}
