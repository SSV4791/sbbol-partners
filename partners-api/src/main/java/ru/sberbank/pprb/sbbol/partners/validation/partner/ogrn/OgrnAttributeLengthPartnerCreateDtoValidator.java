package ru.sberbank.pprb.sbbol.partners.validation.partner.ogrn;

import ru.sberbank.pprb.sbbol.partners.model.OgrnLengthValidation;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;
import ru.sberbank.pprb.sbbol.partners.service.legalform.LegalFormInspector;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

public class OgrnAttributeLengthPartnerCreateDtoValidator extends BaseOgrnLengthValidator
    implements ConstraintValidator<OgrnLengthValidation, PartnerCreate> {

    private final LegalFormInspector legalFormInspector;

    public OgrnAttributeLengthPartnerCreateDtoValidator(LegalFormInspector legalFormInspector) {
        this.legalFormInspector = legalFormInspector;
    }

    @Override
    public boolean isValid(PartnerCreate value, ConstraintValidatorContext context) {
        if (isEmpty(value)) {
            return true;
        }
        legalFormInspector.setLegalFormAndPartnerName(value);
        return isValid(context, value.getOgrn(), value.getLegalForm());
    }
}
