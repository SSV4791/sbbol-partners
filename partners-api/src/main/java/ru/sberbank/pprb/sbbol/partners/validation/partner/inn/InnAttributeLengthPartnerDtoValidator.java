package ru.sberbank.pprb.sbbol.partners.validation.partner.inn;

import ru.sberbank.pprb.sbbol.partners.model.InnLengthValidation;
import ru.sberbank.pprb.sbbol.partners.model.Partner;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.util.Objects.isNull;

public class InnAttributeLengthPartnerDtoValidator extends BaseInnLengthValidator
    implements ConstraintValidator<InnLengthValidation, Partner> {

    @Override
    public boolean isValid(Partner value, ConstraintValidatorContext context) {
        if (isNull(value)) {
            return true;
        }
        return isValid(context, value.getInn(), value.getLegalForm());
    }
}
