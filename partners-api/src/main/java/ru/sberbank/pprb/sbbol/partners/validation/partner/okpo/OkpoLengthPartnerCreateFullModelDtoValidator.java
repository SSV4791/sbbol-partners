package ru.sberbank.pprb.sbbol.partners.validation.partner.okpo;

import ru.sberbank.pprb.sbbol.partners.model.OkpoValidataion;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.service.legalform.LegalFormInspector;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

public class OkpoLengthPartnerCreateFullModelDtoValidator extends BaseOkpoLengthValidator
    implements ConstraintValidator<OkpoValidataion, PartnerCreateFullModel> {

    private final LegalFormInspector legalFormInspector;

    public OkpoLengthPartnerCreateFullModelDtoValidator(LegalFormInspector legalFormInspector) {
        this.legalFormInspector = legalFormInspector;
    }

    @Override
    public boolean isValid(PartnerCreateFullModel value, ConstraintValidatorContext context) {
        if (isEmpty(value)) {
            return true;
        }
        legalFormInspector.setLegalFormAndPartnerName(value);
        return isValid(context, value.getOkpo(), value.getLegalForm());
    }
}
