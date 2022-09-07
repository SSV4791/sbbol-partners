package ru.sberbank.pprb.sbbol.partners.validation.partner.inn;

import org.apache.commons.lang3.ObjectUtils;
import ru.sberbank.pprb.sbbol.partners.model.InnAttributeLengthPartnerValidation;
import ru.sberbank.pprb.sbbol.partners.model.Partner;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class InnAttributeLengthPartnerDtoValidator extends BaseInnLengthValidator
    implements ConstraintValidator<InnAttributeLengthPartnerValidation, Partner> {

    @Override
    public boolean isValid(Partner value, ConstraintValidatorContext context) {
        if (ObjectUtils.isEmpty(value)) {
            return true;
        }
        return isValid(context, value.getInn(), value.getLegalForm());
    }
}
