package ru.sberbank.pprb.sbbol.partners.validation.partner.name;

import org.apache.commons.lang3.ObjectUtils;
import ru.sberbank.pprb.sbbol.partners.model.PartnerChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.PartnerNameValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NameAttributePartnerChangeFullModelDtoValidator extends BaseNameAttributeValidator
    implements ConstraintValidator<PartnerNameValidation, PartnerChangeFullModel> {

    @Override
    public boolean isValid(PartnerChangeFullModel value, ConstraintValidatorContext context) {
        if (!ObjectUtils.isEmpty(value)) {
            return isValid(context, value.getLegalForm(), value.getOrgName(), value.getFirstName(), value.getSecondName());
        }
        return true;
    }
}
