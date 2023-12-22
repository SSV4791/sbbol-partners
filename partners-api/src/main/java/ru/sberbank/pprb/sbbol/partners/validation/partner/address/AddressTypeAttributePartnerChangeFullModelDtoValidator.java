package ru.sberbank.pprb.sbbol.partners.validation.partner.address;

import ru.sberbank.pprb.sbbol.partners.model.AddressTypeValidation;
import ru.sberbank.pprb.sbbol.partners.model.PartnerChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.validation.address.BaseAddressTypeValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.util.Objects.isNull;

public class AddressTypeAttributePartnerChangeFullModelDtoValidator extends BaseAddressTypeValidator
    implements ConstraintValidator<AddressTypeValidation, PartnerChangeFullModel> {

    @Override
    public boolean isValid(PartnerChangeFullModel value, ConstraintValidatorContext context) {
        if (isNull(value)) {
            return true;
        }

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
