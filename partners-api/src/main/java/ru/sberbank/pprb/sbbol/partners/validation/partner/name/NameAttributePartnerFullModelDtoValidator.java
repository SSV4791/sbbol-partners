package ru.sberbank.pprb.sbbol.partners.validation.partner.name;

import org.apache.commons.lang3.ObjectUtils;
import ru.sberbank.pprb.sbbol.partners.model.PartnerNameValidation;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NameAttributePartnerFullModelDtoValidator extends BaseNameAttributeValidator
    implements ConstraintValidator<PartnerNameValidation, PartnerCreateFullModel> {

    @Override
    public boolean isValid(PartnerCreateFullModel value, ConstraintValidatorContext context) {
        if (!ObjectUtils.isEmpty(value)) {
            return isValid(context, value.getLegalForm(), value.getOrgName(), value.getFirstName());
        }
        return true;
    }
}
