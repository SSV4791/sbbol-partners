package ru.sberbank.pprb.sbbol.partners.validation.partner.name;

import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidator;

import javax.validation.ConstraintValidatorContext;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class BaseNameAttributeValidator extends BaseValidator {

    private static final String MESSAGE = "{javax.validation.constraints.NotEmpty.message}";

    protected boolean isValid(ConstraintValidatorContext context, LegalForm legalForm, String orgName, String firstName) {
        if (legalForm != LegalForm.PHYSICAL_PERSON) {
            buildMessage(context, "orgName", MESSAGE);
            return !isEmpty(orgName);
        } else {
            buildMessage(context, "firstName", MESSAGE);
            return !isEmpty(firstName);
        }
    }
}
