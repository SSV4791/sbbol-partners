package ru.sberbank.pprb.sbbol.partners.validation;

import ru.sberbank.pprb.sbbol.partners.model.LegalForm;

import javax.validation.ConstraintValidatorContext;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class BaseNameAttributeValidator {

    private static final String MESSAGE_PHYSICAL_PERSON  = "{validation.partner.firstName.is_null}";
    private static final String MESSAGE_LEGAL_ENTITY = "{validation.partner.orgName.is_null}";

    protected boolean isValid(ConstraintValidatorContext context, LegalForm legalForm, String orgName, String firstName) {
        context.disableDefaultConstraintViolation();
        if(legalForm != LegalForm.PHYSICAL_PERSON) {
            buildMessage(context, "orgName", MESSAGE_LEGAL_ENTITY);
            return !isEmpty(orgName);
        } else {
            buildMessage(context, "firstName", MESSAGE_PHYSICAL_PERSON);
            return !isEmpty(firstName);
        }
    }

    private void buildMessage(ConstraintValidatorContext context, String field, String message) {
        var builder = context.buildConstraintViolationWithTemplate(message);
        builder
            .addPropertyNode(field)
            .addConstraintViolation();
    }
}
