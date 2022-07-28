package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.util.StringUtils;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.InnLengthAttributePartnerDtoValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class InnAttributeLengthPartnerDtoValidator
    implements ConstraintValidator<InnLengthAttributePartnerDtoValidation, Partner> {

    @Override
    public boolean isValid(Partner value, ConstraintValidatorContext context) {
        if (ObjectUtils.isEmpty(value)) {
            return true;
        }
        var inn = value.getInn();
        if (!StringUtils.hasText(inn)) {
            return true;
        }
        var length = inn.length();
        if (value.getLegalForm() == LegalForm.LEGAL_ENTITY) {
            return length == 10 || length == 5;
        }
        return length == 12 || length == 5;
    }
}
