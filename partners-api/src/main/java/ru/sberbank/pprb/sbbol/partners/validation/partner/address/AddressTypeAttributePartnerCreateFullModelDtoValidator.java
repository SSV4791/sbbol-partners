package ru.sberbank.pprb.sbbol.partners.validation.partner.address;

import ru.sberbank.pprb.sbbol.partners.model.AddressTypeValidation;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.service.legalform.LegalFormInspector;
import ru.sberbank.pprb.sbbol.partners.validation.address.BaseAddressTypeValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.util.Objects.isNull;

public class AddressTypeAttributePartnerCreateFullModelDtoValidator extends BaseAddressTypeValidator
    implements ConstraintValidator<AddressTypeValidation, PartnerCreateFullModel> {

    private final LegalFormInspector legalFormInspector;

    public AddressTypeAttributePartnerCreateFullModelDtoValidator(LegalFormInspector legalFormInspector) {
        this.legalFormInspector = legalFormInspector;
    }

    @Override
    public boolean isValid(PartnerCreateFullModel value, ConstraintValidatorContext context) {
        if (isNull(value)) {
            return true;
        }

        legalFormInspector.setLegalFormAndPartnerName(value);

        var addresses = value.getAddress();
        var legalForm = value.getLegalForm();

        if (isNull(addresses) || isNull(legalForm)) {
            return true;
        }

        var result = true;
        var addressCounter = 0;
        for (var address : addresses) {
            if (isNull(address)) {
                continue;
            }
            var addressType = address.getType();
            if (!isValid(context, addressType, legalForm, String.format("address[%s].type", addressCounter))) {
                result = false;
            }
            addressCounter++;
        }

        return result;
    }
}
