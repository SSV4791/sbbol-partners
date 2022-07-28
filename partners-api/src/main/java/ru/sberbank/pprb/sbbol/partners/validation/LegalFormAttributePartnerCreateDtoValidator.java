package ru.sberbank.pprb.sbbol.partners.validation;

import org.springframework.util.StringUtils;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;
import ru.sberbank.pprb.sbbol.partners.model.LegalFormAttributePartnerCreateDtoValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LegalFormAttributePartnerCreateDtoValidator implements ConstraintValidator<LegalFormAttributePartnerCreateDtoValidation, PartnerCreate> {

    @Override
    public boolean isValid(PartnerCreate value, ConstraintValidatorContext context) {
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
