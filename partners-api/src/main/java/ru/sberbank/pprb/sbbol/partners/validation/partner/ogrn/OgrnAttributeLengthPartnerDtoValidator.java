package ru.sberbank.pprb.sbbol.partners.validation.partner.ogrn;

import org.apache.commons.lang3.ObjectUtils;
import ru.sberbank.pprb.sbbol.partners.model.OgrnAttributeLengthPartnerValidation;
import ru.sberbank.pprb.sbbol.partners.model.Partner;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OgrnAttributeLengthPartnerDtoValidator extends BaseOgrnLengthValidator
    implements ConstraintValidator<OgrnAttributeLengthPartnerValidation, Partner> {

    @Override
    public boolean isValid(Partner value, ConstraintValidatorContext context) {
        if (ObjectUtils.isEmpty(value)) {
            return true;
        }
        return isValid(context, value.getOgrn(), value.getLegalForm());
    }
}
