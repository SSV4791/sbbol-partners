package ru.sberbank.pprb.sbbol.partners.validation.partner.inn;

import org.springframework.util.StringUtils;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidator;

import javax.validation.ConstraintValidatorContext;

public class BaseInnLengthValidator extends BaseValidator {

    private static final int INN_5_VALID_LENGTH = 5;
    private static final int INN_10_VALID_LENGTH = 10;
    private static final int INN_12_VALID_LENGTH = 12;
    private static final String MESSAGE_LEGAL_ENTITY = "{validation.partner.legal_entity.inn.length}";
    private static final String MESSAGE_PHYSICAL_PERSON_OR_ENTREPRENEUR = "{validation.partner.physical_person_or_entrepreneur.inn.length}";

    public boolean isValid(ConstraintValidatorContext context, String inn, LegalForm legalForm) {
        if (!StringUtils.hasText(inn) || legalForm == null) {
            return true;
        }
        var innLength = inn.length();
        if (legalForm != LegalForm.LEGAL_ENTITY && innLength != INN_12_VALID_LENGTH) {
            buildMessage(context, "inn", MESSAGE_PHYSICAL_PERSON_OR_ENTREPRENEUR);
            return false;
        }
        if (legalForm == LegalForm.LEGAL_ENTITY && innLength != INN_10_VALID_LENGTH && innLength != INN_5_VALID_LENGTH) {
            buildMessage(context, "inn", MESSAGE_LEGAL_ENTITY);
            return false;
        }
        return true;
    }
}
