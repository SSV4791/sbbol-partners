package ru.sberbank.pprb.sbbol.partners.validation.partner.inn;

import ru.sberbank.pprb.sbbol.partners.model.InnLengthValidation;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.service.legalform.LegalFormInspector;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

public class InnAttributeLengthPartnerCreateFullModelDtoValidator extends BaseInnLengthValidator
    implements ConstraintValidator<InnLengthValidation, PartnerCreateFullModel> {

    private final LegalFormInspector legalFormInspector;

    public InnAttributeLengthPartnerCreateFullModelDtoValidator(LegalFormInspector legalFormInspector) {
        this.legalFormInspector = legalFormInspector;
    }

    @Override
    public boolean isValid(PartnerCreateFullModel value, ConstraintValidatorContext context) {
        if (isEmpty(value)) {
            return true;
        }
        legalFormInspector.setLegalFormAndPartnerName(value);
        return isValid(context, value.getInn(), value.getLegalForm());
    }
}
