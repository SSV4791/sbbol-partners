package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang3.ObjectUtils;
import ru.sberbank.pprb.sbbol.partners.model.OkpoValidataion;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OkpoPartnerCreateDtoValidator extends BaseOkpoValidator
    implements ConstraintValidator<OkpoValidataion, PartnerCreate> {

    @Override
    public boolean isValid(PartnerCreate value, ConstraintValidatorContext context) {
        if (ObjectUtils.isEmpty(value)) {
            return true;
        }
        return isValid(context, value.getOkpo(), value.getLegalForm());
    }
}
