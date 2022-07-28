package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.util.StringUtils;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.InnLengthAttributePartnerFullModelDtoValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class InnAttributeLengthPartnerFullModelDtoValidator
    implements ConstraintValidator<InnLengthAttributePartnerFullModelDtoValidation, PartnerCreateFullModel> {

    @Override
    public boolean isValid(PartnerCreateFullModel value, ConstraintValidatorContext context) {
        if (ObjectUtils.isEmpty(value)) {
            return true;
        }
        var inn = value.getInn();
        if (!StringUtils.hasText(inn)) {
            return true;
        }
        if (value.getLegalForm() == LegalForm.LEGAL_ENTITY) {
            return inn.length() == 10 || inn.length() == 5;
        }
        return inn.length() == 12 || inn.length() == 5;
    }
}
