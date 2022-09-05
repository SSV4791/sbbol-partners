package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang3.ObjectUtils;
import ru.sberbank.pprb.sbbol.partners.model.OkpoValidataion;
import ru.sberbank.pprb.sbbol.partners.model.Partner;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OkpoPartnerDtoValidator extends BaseOkpoValidator
    implements ConstraintValidator<OkpoValidataion, Partner> {

    @Override
    public boolean isValid(Partner value, ConstraintValidatorContext context) {
        if (ObjectUtils.isEmpty(value)) {
            return true;
        }
        return isValid(context, value.getOkpo(), value.getLegalForm());
    }
}
