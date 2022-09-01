package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.model.LegalForm;

import javax.validation.ConstraintValidatorContext;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class BaseNameAttributeValidator extends BaseValidator {

    private static final String MESSAGE_PHYSICAL_PERSON = "{validation.partner.firstName.is_null}";
    private static final String MESSAGE_LEGAL_ENTITY = "{validation.partner.orgName.is_null}";

    protected boolean isValid(ConstraintValidatorContext context, LegalForm legalForm, String orgName, String firstName) {
        if (legalForm != LegalForm.PHYSICAL_PERSON) {
            buildMessage(context, "orgName", MESSAGE_LEGAL_ENTITY);
            return !isEmpty(orgName);
        } else {
            buildMessage(context, "firstName", MESSAGE_PHYSICAL_PERSON);
            return !isEmpty(firstName);
        }
    }
}
