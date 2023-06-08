package ru.sberbank.pprb.sbbol.partners.validation.partner.ogrn;

import org.apache.commons.lang3.ObjectUtils;
import ru.sberbank.pprb.sbbol.partners.model.OgrnLengthValidation;
import ru.sberbank.pprb.sbbol.partners.model.PartnerChangeFullModel;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OgrnAttributeLengthPartnerChangeFullModelDtoValidator extends BaseOgrnLengthValidator
    implements ConstraintValidator<OgrnLengthValidation, PartnerChangeFullModel> {

    @Override
    public boolean isValid(PartnerChangeFullModel value, ConstraintValidatorContext context) {
        if (ObjectUtils.isEmpty(value)) {
            return true;
        }
        return isValid(context, value.getOgrn(), value.getLegalForm());
    }
}
