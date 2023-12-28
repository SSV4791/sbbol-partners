package ru.sberbank.pprb.sbbol.partners.validation.partner.inn;

import ru.sberbank.pprb.sbbol.partners.model.InnLengthValidation;
import ru.sberbank.pprb.sbbol.partners.model.PartnerChangeFullModel;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.util.Objects.isNull;

public class InnAttributeLengthPartnerChangeFullModelDtoValidator extends BaseInnLengthValidator
    implements ConstraintValidator<InnLengthValidation, PartnerChangeFullModel> {

    @Override
    public boolean isValid(PartnerChangeFullModel value, ConstraintValidatorContext context) {
        if (isNull(value)) {
            return true;
        }
        return isValid(context, value.getInn(), value.getLegalForm());
    }
}
