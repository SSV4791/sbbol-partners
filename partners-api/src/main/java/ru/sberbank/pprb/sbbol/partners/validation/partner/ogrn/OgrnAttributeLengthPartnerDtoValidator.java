package ru.sberbank.pprb.sbbol.partners.validation.partner.ogrn;

import ru.sberbank.pprb.sbbol.partners.model.OgrnLengthValidation;
import ru.sberbank.pprb.sbbol.partners.model.Partner;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.util.Objects.isNull;

public class OgrnAttributeLengthPartnerDtoValidator extends BaseOgrnLengthValidator
    implements ConstraintValidator<OgrnLengthValidation, Partner> {

    @Override
    public boolean isValid(Partner value, ConstraintValidatorContext context) {
        if (isNull(value)) {
            return true;
        }
        return isValid(context, value.getOgrn(), value.getLegalForm());
    }
}
