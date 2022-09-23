package ru.sberbank.pprb.sbbol.partners.validation.partner.inn;

import org.apache.commons.lang3.ObjectUtils;
import ru.sberbank.pprb.sbbol.partners.model.InnExistValidation;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class InnAttributeExistPartnerCreateDtoValidator extends BaseInnExistValidator
    implements ConstraintValidator<InnExistValidation, PartnerCreate> {

    @Override
    public boolean isValid(PartnerCreate value, ConstraintValidatorContext context) {
        if (ObjectUtils.isEmpty(value)) {
            return true;
        }
        return isValid(context, value.getInn(), value.getLegalForm());
    }
}
