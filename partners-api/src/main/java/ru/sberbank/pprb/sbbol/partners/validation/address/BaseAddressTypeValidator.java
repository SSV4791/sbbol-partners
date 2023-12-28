package ru.sberbank.pprb.sbbol.partners.validation.address;

import ru.sberbank.pprb.sbbol.partners.model.AddressType;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidator;

import javax.validation.ConstraintValidatorContext;

import static org.apache.commons.lang3.ObjectUtils.notEqual;
import static ru.sberbank.pprb.sbbol.partners.model.AddressType.LEGAL_ADDRESS;
import static ru.sberbank.pprb.sbbol.partners.model.AddressType.PHYSICAL_ADDRESS;
import static ru.sberbank.pprb.sbbol.partners.model.AddressType.REGISTRATION_ADDRESS;
import static ru.sberbank.pprb.sbbol.partners.model.AddressType.RESIDENTIAL_ADDRESS;
import static ru.sberbank.pprb.sbbol.partners.model.LegalForm.ENTREPRENEUR;
import static ru.sberbank.pprb.sbbol.partners.model.LegalForm.LEGAL_ENTITY;
import static ru.sberbank.pprb.sbbol.partners.model.LegalForm.PHYSICAL_PERSON;

public class BaseAddressTypeValidator extends BaseValidator {

    private static final String MESSAGE_LEGAL_ENTITY_OR_ENTREPRENEUR = "{validation.partner.address.legal_entity_or_entrepreneur.type}";
    private static final String MESSAGE_PHYSICAL_PERSON = "{validation.partner.address.physical_person.type}";

    public boolean isValid(ConstraintValidatorContext context, AddressType addressType, LegalForm legalForm, String field) {

        if (PHYSICAL_PERSON.equals(legalForm)) {
            if (notEqual(addressType, REGISTRATION_ADDRESS) && notEqual(addressType, RESIDENTIAL_ADDRESS)) {
                buildMessage(context, field, MESSAGE_PHYSICAL_PERSON);
                return false;
            }
        }

        if (LEGAL_ENTITY.equals(legalForm) || ENTREPRENEUR.equals(legalForm)) {
            if (notEqual(addressType, PHYSICAL_ADDRESS) && notEqual(addressType, LEGAL_ADDRESS)) {
                buildMessage(context, field, MESSAGE_LEGAL_ENTITY_OR_ENTREPRENEUR);
                return false;
            }
        }

        return true;
    }
}
