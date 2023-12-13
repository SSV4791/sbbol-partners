package ru.sberbank.pprb.sbbol.partners.validation.partner.inn;

import ru.sberbank.pprb.sbbol.partners.model.InnLengthValidation;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;
import ru.sberbank.pprb.sbbol.partners.service.legalform.LegalFormInspector;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

public class InnAttributeLengthPartnerCreateDtoValidator extends BaseInnLengthValidator
    implements ConstraintValidator<InnLengthValidation, PartnerCreate> {

    private final LegalFormInspector legalFormInspector;

    public InnAttributeLengthPartnerCreateDtoValidator(LegalFormInspector legalFormInspector) {
        this.legalFormInspector = legalFormInspector;
    }

    @Override
    public boolean isValid(PartnerCreate value, ConstraintValidatorContext context) {
        if (isEmpty(value)) {
            return true;
        }
        legalFormInspector.setLegalFormAndPartnerName(value);
        return isValid(context, value.getInn(), value.getLegalForm());
    }
}
