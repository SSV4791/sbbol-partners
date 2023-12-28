package ru.sberbank.pprb.sbbol.partners.validation.partner.name;

import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidator;

import javax.validation.ConstraintValidatorContext;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;


public class BaseNameCreateAttributeValidator extends BaseValidator {

    private static final String NOT_EMPTY_MESSAGE = "{javax.validation.constraints.NotEmpty.message}";

    private static final String OGRN_WITH_NAME_MESSAGE = "{validation.partner.ogrn.and.name}";

    protected boolean isValid(
        ConstraintValidatorContext context,
        LegalForm legalForm,
        String orgName,
        String firstName,
        String secondName,
        String middleName
    ) {
        if (isNull(legalForm)) {
            if (isEmpty(orgName)) {
                buildMessage(context, "orgName", NOT_EMPTY_MESSAGE);
                return false;
            }
            if (isNotEmpty(firstName) ||
                isNotEmpty(secondName) ||
                isNotEmpty(middleName)) {
                buildMessage(context, "orgName", OGRN_WITH_NAME_MESSAGE);
                return false;
            }
        }
        if (legalForm != LegalForm.PHYSICAL_PERSON) {
            buildMessage(context, "orgName", NOT_EMPTY_MESSAGE);
            return isNotEmpty(orgName);
        } else {
            buildMessage(context, "firstName", NOT_EMPTY_MESSAGE);
            return isNotEmpty(firstName);
        }
    }
}
