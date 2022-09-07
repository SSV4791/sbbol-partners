package ru.sberbank.pprb.sbbol.partners.validation.partner.inn;

import org.apache.commons.lang3.ObjectUtils;
import ru.sberbank.pprb.sbbol.partners.model.InnAttributeExistPartnerValidation;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class InnAttributeExistPartnerCreateFullModelDtoValidator extends BaseInnExistValidator
    implements ConstraintValidator<InnAttributeExistPartnerValidation, PartnerCreateFullModel> {

    @Override
    public boolean isValid(PartnerCreateFullModel value, ConstraintValidatorContext context) {
        if (ObjectUtils.isEmpty(value)) {
            return true;
        }
        return isValid(context, value.getInn(), value.getLegalForm());
    }
}
