package ru.sberbank.pprb.sbbol.partners.validation.partner.inn;

import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidator;

import javax.validation.ConstraintValidatorContext;

public class BaseInnExistValidator extends BaseValidator {
    private static final String MESSAGE_INN_EXIST = "{javax.validation.constraints.NotEmpty.message}";

    public boolean isValid(ConstraintValidatorContext context, String inn, LegalForm legalForm) {
        if (StringUtils.isEmpty(inn) && legalForm != LegalForm.PHYSICAL_PERSON) {
            buildMessage(context, "inn", MESSAGE_INN_EXIST);
            return false;
        }
        return true;
    }
}
