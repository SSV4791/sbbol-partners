package ru.sberbank.pprb.sbbol.partners.validation.partner.okpo;

import org.apache.commons.lang3.ObjectUtils;
import ru.sberbank.pprb.sbbol.partners.model.OkpoValidataion;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OkpoLengthPartnerCreateFullModelDtoValidator extends BaseOkpoLengthValidator
    implements ConstraintValidator<OkpoValidataion, PartnerCreateFullModel> {

    @Override
    public boolean isValid(PartnerCreateFullModel value, ConstraintValidatorContext context) {
        if (ObjectUtils.isEmpty(value)) {
            return true;
        }
        return isValid(context, value.getOkpo(), value.getLegalForm());
    }
}
