package ru.sberbank.pprb.sbbol.partners.validation.partner.ogrn;

import org.apache.commons.lang3.ObjectUtils;
import ru.sberbank.pprb.sbbol.partners.model.OgrnAttributeLengthPartnerValidation;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OgrnAttributeLengthPartnerCreateFullModelDtoValidator extends BaseOgrnLengthValidator
    implements ConstraintValidator<OgrnAttributeLengthPartnerValidation, PartnerCreateFullModel> {

    @Override
    public boolean isValid(PartnerCreateFullModel value, ConstraintValidatorContext context) {
        if (ObjectUtils.isEmpty(value)) {
            return true;
        }
        return isValid(context, value.getOgrn(), value.getLegalForm());
    }
}
