package ru.sberbank.pprb.sbbol.partners.validation.partner.name;

import ru.sberbank.pprb.sbbol.partners.model.PartnerNameValidation;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.service.legalform.LegalFormInspector;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

public class NameAttributePartnerCreateFullModelDtoValidator extends BaseNameCreateAttributeValidator
    implements ConstraintValidator<PartnerNameValidation, PartnerCreateFullModel> {

    private final LegalFormInspector legalFormInspector;

    public NameAttributePartnerCreateFullModelDtoValidator(LegalFormInspector legalFormInspector) {
        this.legalFormInspector = legalFormInspector;
    }

    @Override
    public boolean isValid(PartnerCreateFullModel value, ConstraintValidatorContext context) {
        if (isEmpty(value)) {
            return true;
        }
        legalFormInspector.setLegalFormAndPartnerName(value);
        return isValid(context, value.getLegalForm(), value.getOrgName(), value.getFirstName(), value.getSecondName(), value.getMiddleName());
    }
}
