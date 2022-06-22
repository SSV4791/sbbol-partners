package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang.StringUtils;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;
import ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerValidation;

import java.util.List;

import static ru.sberbank.pprb.sbbol.partners.validation.common.BaseEmailValidation.commonValidationChildEmail;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePhoneValidation.commonValidationChildPhone;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerValidation.checkInn;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerValidation.checkOgrn;

public class PartnerCreateValidatorImpl extends AbstractValidatorImpl<PartnerCreate> {

    private static final String LEGAL_FORM_LEGAL_ENTITY = "legalForm.LEGAL_ENTITY";
    private static final String LEGAL_FORM_ENTREPRENEUR = "legalForm.ENTREPRENEUR";
    private static final String LEGAL_FORM_PHYSICAL_PERSON = "legalForm.PHYSICAL_PERSON";
    private static final String DEFAULT_MESSAGE_INN_LENGTH = "partner.inn_length";
    private static final String DEFAULT_MESSAGE_KPP_LENGTH = "partner.kpp.length";

    @Override
    public void validator(List<String> errors, PartnerCreate entity) {
        commonValidationDigitalId(errors, entity.getDigitalId());
        if (StringUtils.isNotEmpty(entity.getComment()) && entity.getComment().length() > COMMENT_PARTNER_MAX_LENGTH_VALIDATION) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_LENGTH, "comment", "1", "255"));
        }
        if (StringUtils.isNotEmpty(entity.getOkpo())) {
            if (entity.getOkpo().length() > OKPO_PARTNER_MAX_LENGTH_VALIDATION && entity.getOkpo().length() < OKPO_PARTNER_MIN_LENGTH_VALIDATION) {
                errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_LENGTH, "okpo", "8", "14"));
            }
        }
        if (StringUtils.isNotEmpty(entity.getOgrn())) {
            if (entity.getOgrn().length() != OGRN_PARTNER_MAX_LENGTH_VALIDATION && entity.getOgrn().length() != OGRN_PARTNER_MIN_LENGTH_VALIDATION) {
                errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_LENGTH, "ogrn", "13", "15"));
            }
        }
        if (StringUtils.isNotEmpty(entity.getKpp()) && entity.getKpp().length() != BasePartnerValidation.KPP_VALID_LENGTH) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_KPP_LENGTH, "9"));
        }
        if (entity.getLegalForm() == null) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "legalForm"));
        } else {
            checkLegalFormProperty(entity, errors);
        }
        if (entity.getEmails() != null) {
            for (var email : entity.getEmails()) {
                commonValidationChildEmail(errors, email);
            }
        }
        if (entity.getPhones() != null) {
            for (var phone : entity.getPhones()) {
                commonValidationChildPhone(errors, phone);
            }
        }
    }

    private void checkLegalFormProperty(PartnerCreate entity, List<String> errors) {
        if (entity.getLegalForm() == LegalForm.LEGAL_ENTITY) {
            if (StringUtils.isEmpty(entity.getOrgName())) {
                errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "orgName", LEGAL_FORM_LEGAL_ENTITY));
            }
            if (StringUtils.isNotEmpty(entity.getInn()) && !checkInn(entity.getInn())) {
                errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER, "inn"));
            }
            if (StringUtils.isNotEmpty(entity.getOgrn()) && !checkOgrn(entity.getOgrn())) {
                errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER, "ogrn"));
            }
        } else if (entity.getLegalForm() == LegalForm.ENTREPRENEUR) {
            if (StringUtils.isEmpty(entity.getOrgName())) {
                errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "orgName", LEGAL_FORM_ENTREPRENEUR));
            }
            if (StringUtils.isNotEmpty(entity.getInn()) && !checkInn(entity.getInn())) {
                errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER, "inn"));
            }
            if (StringUtils.isNotEmpty(entity.getOgrn()) && !checkOgrn(entity.getOgrn())) {
                errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER, "ogrn"));
            }
        } else if (entity.getLegalForm() == LegalForm.PHYSICAL_PERSON) {
            if (StringUtils.isEmpty(entity.getFirstName())) {
                errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "firstName", LEGAL_FORM_PHYSICAL_PERSON));
            }
            if (StringUtils.isNotEmpty(entity.getInn()) && entity.getInn().length() != 12) {
                errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_INN_LENGTH, "inn", LEGAL_FORM_PHYSICAL_PERSON, "12"));
            }
            if (StringUtils.isNotEmpty(entity.getInn()) && !checkInn(entity.getInn())) {
                errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER, "inn"));
            }
        }
    }
}
