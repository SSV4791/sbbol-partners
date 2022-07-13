package ru.sberbank.pprb.sbbol.partners.validation;

import org.springframework.util.StringUtils;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.LegalFormPartnerValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LegalFormAttributePartnerDtoValidation implements ConstraintValidator<LegalFormPartnerValidation, Partner> {

    @Override
    public boolean isValid(Partner value, ConstraintValidatorContext context) {
        if (value != null) {
            if (value.getLegalForm() != LegalForm.PHYSICAL_PERSON) {
                return StringUtils.hasText(value.getOrgName());
            } else {
                return StringUtils.hasText(value.getFirstName());
            }
        }
        return true;
    }
}
