package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang.StringUtils;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;
import ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerValidation;

import java.util.List;
import java.util.Map;

import static ru.sberbank.pprb.sbbol.partners.validation.common.BaseEmailValidation.commonValidationChildEmail;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerValidation.checkInn;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerValidation.checkOgrn;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePhoneValidation.commonValidationChildPhone;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidation.setError;

public class PartnerCreateValidatorImpl extends AbstractValidatorImpl<PartnerCreate> {

    public static final String DEFAULT_MESSAGE_INN_LENGTH = "partner.inn_length";
    public static final String DEFAULT_MESSAGE_KPP_LENGTH = "partner.kpp.length";
    public static final String DEFAULT_MESSAGE_OGRN_LENGTH = "partner.ogrn_length";
    public static final String DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER = "default.field.control_number";
    public static final String DEFAULT_MESSAGE_OKPO_LENGTH = "partner.okpo_length";

    @Override
    public void validator(Map<String, List<String>> errors, PartnerCreate entity) {
        commonValidationDigitalId(errors, entity.getDigitalId());
        if (StringUtils.isNotEmpty(entity.getFirstName()) && entity.getFirstName().length() > FIRST_NAME_MAX_LENGTH_VALIDATION) {
            setError(errors, "partner_firstName", MessagesTranslator.toLocale(DEFAULT_LENGTH, "50"));
        }
        if (StringUtils.isNotEmpty(entity.getMiddleName()) && entity.getOrgName().length() > MIDDLE_NAME_MAX_LENGTH_VALIDATION) {
            setError(errors, "partner_middleName", MessagesTranslator.toLocale(DEFAULT_LENGTH, "50"));
        }
        if (StringUtils.isNotEmpty(entity.getSecondName()) && entity.getSecondName().length() > SECOND_NAME_MAX_LENGTH_VALIDATION) {
            setError(errors, "partner_secondName", MessagesTranslator.toLocale(DEFAULT_LENGTH, "50"));
        }
        if (StringUtils.isNotEmpty(entity.getComment()) && entity.getComment().length() > COMMENT_PARTNER_MAX_LENGTH_VALIDATION) {
            setError(errors, "partner_comment", MessagesTranslator.toLocale(DEFAULT_LENGTH, "255"));
        }
        if (StringUtils.isNotEmpty(entity.getOkpo())) {
            if (entity.getOkpo().length() > OKPO_PARTNER_MAX_LENGTH_VALIDATION && entity.getOkpo().length() < OKPO_PARTNER_MIN_LENGTH_VALIDATION) {
                setError(errors, "okpo", MessagesTranslator.toLocale(DEFAULT_MESSAGE_OKPO_LENGTH));
            }
        }
        if (StringUtils.isNotEmpty(entity.getOgrn())) {
            if (entity.getOgrn().length() != OGRN_PARTNER_MAX_LENGTH_VALIDATION && entity.getOgrn().length() != OGRN_PARTNER_MIN_LENGTH_VALIDATION) {
                setError(errors, "ogrn", MessagesTranslator.toLocale(DEFAULT_MESSAGE_OGRN_LENGTH));
            }
        }
        if (StringUtils.isNotEmpty(entity.getKpp()) && entity.getKpp().length() != BasePartnerValidation.KPP_VALID_LENGTH) {
            setError(errors, "kpp", MessagesTranslator.toLocale(DEFAULT_MESSAGE_KPP_LENGTH));
        }
        if (entity.getLegalForm() == null) {
            setError(errors, "partner_legalForm", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "правовую форму партнёра"));
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

    private void checkLegalFormProperty(PartnerCreate entity, Map<String, List<String>> errors) {
        if (entity.getLegalForm() == LegalForm.LEGAL_ENTITY) {
            if (StringUtils.isEmpty(entity.getOrgName())) {
                setError(errors, "partner_orgName", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "название организации"));
            }
            if (StringUtils.isNotEmpty(entity.getInn()) && entity.getInn().length() != 10 && entity.getInn().length() != 5) {
                setError(errors, "inn", MessagesTranslator.toLocale(DEFAULT_MESSAGE_INN_LENGTH, "10"));
            }
            if (StringUtils.isNotEmpty(entity.getInn()) && !checkInn(entity.getInn())) {
                setError(errors, "inn", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER, "ИНН"));
            }
            if (StringUtils.isNotEmpty(entity.getOgrn()) && !checkOgrn(entity.getOgrn())) {
                setError(errors, "ogrn", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER, "ОГРН"));
            }
        } else if (entity.getLegalForm() == LegalForm.ENTREPRENEUR) {
            if (StringUtils.isEmpty(entity.getOrgName())) {
                setError(errors, "partner_orgName", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "название организации"));
            }
            if (StringUtils.isNotEmpty(entity.getInn()) && entity.getInn().length() != 12 && entity.getInn().length() != 5) {
                setError(errors, "inn", MessagesTranslator.toLocale(DEFAULT_MESSAGE_INN_LENGTH, "12"));
            }
            if (StringUtils.isNotEmpty(entity.getInn()) && !checkInn(entity.getInn())) {
                setError(errors, "inn", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER, "ИНН"));
            }
            if (StringUtils.isNotEmpty(entity.getOgrn()) && !checkOgrn(entity.getOgrn())) {
                setError(errors, "orgn", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER, "ОГРН"));
            }
        } else if (entity.getLegalForm() == LegalForm.PHYSICAL_PERSON) {
            if (StringUtils.isEmpty(entity.getFirstName())) {
                setError(errors, "partner_firstName", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "имя партнёра"));
            }
            if (StringUtils.isNotEmpty(entity.getInn()) && entity.getInn().length() != 12 && entity.getInn().length() != 5) {
                setError(errors, "inn", MessagesTranslator.toLocale(DEFAULT_MESSAGE_INN_LENGTH, "12"));
            }
            if (StringUtils.isNotEmpty(entity.getInn()) && !checkInn(entity.getInn())) {
                setError(errors, "inn", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER, "ИНН"));
            }
        }
    }
}
