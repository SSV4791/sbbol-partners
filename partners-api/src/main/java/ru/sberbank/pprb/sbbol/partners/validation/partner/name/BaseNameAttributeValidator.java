package ru.sberbank.pprb.sbbol.partners.validation.partner.name;

import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidator;

import javax.validation.ConstraintValidatorContext;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class BaseNameAttributeValidator extends BaseValidator {

    private static final String MESSAGE = "{javax.validation.constraints.NotEmpty.message}";

    protected boolean isValid(
        ConstraintValidatorContext context,
        LegalForm legalForm,
        String orgName,
        String firstName,
        String secondName
    ) {
        if (legalForm != LegalForm.PHYSICAL_PERSON) {
            buildMessage(context, "orgName", MESSAGE);
            return isNotEmpty(orgName);
        } else {
            boolean valid = true;
            if (isEmpty(firstName)) {
                buildMessage(context, "firstName", MESSAGE);
                valid = false;
            }
            if (isEmpty(secondName)) {
                buildMessage(context, "secondName", MESSAGE);
                valid = false;
            }
            return valid;
        }
    }
}
