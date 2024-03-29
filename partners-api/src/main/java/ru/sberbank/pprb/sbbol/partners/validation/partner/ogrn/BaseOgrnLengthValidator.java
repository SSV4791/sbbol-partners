package ru.sberbank.pprb.sbbol.partners.validation.partner.ogrn;

import org.springframework.util.StringUtils;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidator;

import javax.validation.ConstraintValidatorContext;

import static java.util.Objects.isNull;
import static org.springframework.util.StringUtils.hasText;

public class BaseOgrnLengthValidator extends BaseValidator {

    private static final int OGRN_13_VALID_LENGTH = 13;
    private static final int OGRN_15_VALID_LENGTH = 15;
    private static final String MESSAGE_LEGAL_ENTITY = "{validation.partner.legal_entity.ogrn.length}";
    private static final String MESSAGE_ENTREPRENEUR = "{validation.partner.physical_person_or_entrepreneur.ogrn.length}";

    public boolean isValid(ConstraintValidatorContext context, String ogrn, LegalForm legalForm) {
        if (!hasText(ogrn) || isNull(legalForm)) {
            return true;
        }
        if (legalForm == LegalForm.PHYSICAL_PERSON) {
            return true;
        }
        if (legalForm == LegalForm.LEGAL_ENTITY) {
            buildMessage(context, "ogrn", MESSAGE_LEGAL_ENTITY);
            return OGRN_13_VALID_LENGTH == ogrn.length();
        }
        buildMessage(context, "ogrn", MESSAGE_ENTREPRENEUR);
        return OGRN_15_VALID_LENGTH == ogrn.length();
    }
}
