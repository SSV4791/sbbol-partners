package ru.sberbank.pprb.sbbol.partners.validation.partner.name;

import org.apache.commons.lang3.ObjectUtils;
import ru.sberbank.pprb.sbbol.partners.model.PartnerNameValidation;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NameAttributePartnerCreateDtoValidator extends BaseNameAttributeValidator
    implements ConstraintValidator<PartnerNameValidation, PartnerCreate> {

    @Override
    public boolean isValid(PartnerCreate value, ConstraintValidatorContext context) {
        if (!ObjectUtils.isEmpty(value)) {
            return isValid(context, value.getLegalForm(), value.getOrgName(), value.getFirstName());
        }
        return true;
    }
}
