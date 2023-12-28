package ru.sberbank.pprb.sbbol.partners.validation.address;

import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.model.Address;
import ru.sberbank.pprb.sbbol.partners.model.AddressTypeValidation;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.util.Objects.isNull;

public class AddressTypeAttributeAddressDtoValidator extends BaseAddressTypeValidator
    implements ConstraintValidator<AddressTypeValidation, Address> {

    private final PartnerService partnerService;

    public AddressTypeAttributeAddressDtoValidator(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    @Override
    public boolean isValid(Address value, ConstraintValidatorContext context) {
        if (isNull(value)) {
            return true;
        }
        LegalForm legalForm;

        try {
            legalForm = partnerService.getPartnerLegalForm(value.getDigitalId(), value.getUnifiedId());
        } catch (EntryNotFoundException ex) {
            return true;
        }

        return isValid(context, value.getType(), legalForm, "type");
    }
}
