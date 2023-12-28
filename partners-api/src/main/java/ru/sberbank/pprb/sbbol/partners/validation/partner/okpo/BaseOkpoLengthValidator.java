package ru.sberbank.pprb.sbbol.partners.validation.partner.okpo;

import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidator;

import javax.validation.ConstraintValidatorContext;

import static java.util.Objects.isNull;
import static org.springframework.util.StringUtils.hasText;

public class BaseOkpoLengthValidator extends BaseValidator {

    private static final int OKPO_8_VALID_LENGTH = 8;
    private static final int OKPO_10_VALID_LENGTH = 10;
    private static final String MESSAGE_LEGAL_ENTITY = "{validation.partner.legal_entity.okpo.length}";
    private static final String MESSAGE_ENTREPRENEUR = "{validation.partner.entrepreneur.okpo.length}";

    public boolean isValid(ConstraintValidatorContext context, String okpo, LegalForm legalForm) {
        if (!hasText(okpo) || isNull(legalForm)) {
            return true;
        }
        if (legalForm == LegalForm.PHYSICAL_PERSON) {
            return true;
        }
        if (legalForm == LegalForm.LEGAL_ENTITY) {
            buildMessage(context, "okpo", MESSAGE_LEGAL_ENTITY);
            return OKPO_8_VALID_LENGTH == okpo.length();
        }
        buildMessage(context, "okpo", MESSAGE_ENTREPRENEUR);
        return OKPO_10_VALID_LENGTH == okpo.length();
    }
}
