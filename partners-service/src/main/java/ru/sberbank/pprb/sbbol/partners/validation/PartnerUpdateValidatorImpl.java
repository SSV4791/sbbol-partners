package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.LegalType;
import ru.sberbank.pprb.sbbol.partners.exception.MissingValueException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerMapper;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;
import ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerValidation;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerValidation.checkInn;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerValidation.checkOgrn;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidation.setError;

public class PartnerUpdateValidatorImpl extends AbstractValidatorImpl<Partner> {
    private static final String DOCUMENT_NAME = "partner";
    private static final String LEGAL_FORM_LEGAL_ENTITY = "legalForm.LEGAL_ENTITY";
    private static final String LEGAL_FORM_ENTREPRENEUR = "legalForm.ENTREPRENEUR";
    private static final String LEGAL_FORM_PHYSICAL_PERSON = "legalForm.PHYSICAL_PERSON";
    private static final String DEFAULT_MESSAGE_INN_LENGTH = "partner.inn_length";
    private static final String DEFAULT_MESSAGE_KPP_LENGTH = "partner.kpp.length";
    private static final String DEFAULT_MESSAGE_OGRN_LENGTH = "partner.ogrn_length";
    private static final String DEFAULT_MESSAGE_OKPO_LENGTH = "partner.okpo_length";
    protected static final String DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER = "default.field.control_number";

    private final PartnerRepository partnerRepository;
    private final PartnerMapper partnerMapper;
    private final Validator<Email> emailUpdateValidator;
    private final Validator<Phone> phoneUpdateValidator;

    public PartnerUpdateValidatorImpl(
        PartnerRepository partnerRepository,
        PartnerMapper partnerMapper,
        Validator<Email> emailUpdateValidator,
        Validator<Phone> phoneUpdateValidator
    ) {
        this.emailUpdateValidator = emailUpdateValidator;
        this.phoneUpdateValidator = phoneUpdateValidator;
        this.partnerRepository = partnerRepository;
        this.partnerMapper = partnerMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public void validator(Map<String, List<String>> errors, Partner entity) {
        checkLegalFormProperty(errors, entity);
        commonValidationDigitalId(errors, entity.getDigitalId());
        commonValidationUuid(errors, entity.getId());
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
        if (entity.getEmails() != null) {
            for (var emails : entity.getEmails()) {
                emailUpdateValidator.validator(errors, emails);
            }
        }
        if (entity.getPhones() != null) {
            for (var phones : entity.getPhones()) {
                phoneUpdateValidator.validator(errors, phones);
            }
        }
        if (entity.getVersion() == null) {
            setError(errors, "common", MessagesTranslator.toLocale(DEFAULT_MESSAGE_CAMMON_FIELD_IS_NULL, "version"));
        }
    }

    private void checkLegalFormProperty(Map<String, List<String>> errors, Partner entity) {
        var foundPartner = partnerRepository.getByDigitalIdAndUuid(entity.getDigitalId(), UUID.fromString(entity.getId()))
            .orElseThrow(() -> new MissingValueException(MessagesTranslator.toLocale(DEFAULT_MESSAGE_OBJECT_NOT_FOUND_ERROR, DOCUMENT_NAME, entity.getDigitalId(), entity.getId())));
        if (!entity.getVersion().equals(foundPartner.getVersion())) {
            setError(errors, "common", MessagesTranslator.toLocale(DEFAULT_MESSAGE_VERSION_ERROR, foundPartner.getVersion().toString(), entity.getVersion().toString()));
        }
        partnerMapper.updatePartner(entity, foundPartner);
        if (foundPartner.getLegalType() != null) {
            if (foundPartner.getLegalType() == LegalType.LEGAL_ENTITY) {
                if (StringUtils.isEmpty(foundPartner.getOrgName())) {
                    setError(errors, "partner_orgName", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "название организации"));
                }
                if (StringUtils.isNotEmpty(entity.getInn()) && entity.getInn().length() != 10 && entity.getInn().length() != 5) {
                    setError(errors, "inn", MessagesTranslator.toLocale(DEFAULT_MESSAGE_INN_LENGTH, "10"));
                }
                if (StringUtils.isNotEmpty(entity.getInn()) && !checkInn(entity.getInn())) {
                    setError(errors, "inn", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER, "ИНН"));
                }
                if (StringUtils.isNotEmpty(foundPartner.getOgrn()) && !checkOgrn(foundPartner.getOgrn())) {
                    setError(errors, "ogrn", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER, "ogrn"));
                }
            } else if (foundPartner.getLegalType() == LegalType.ENTREPRENEUR) {
                if (StringUtils.isEmpty(foundPartner.getOrgName())) {
                    setError(errors, "partner_orgName", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "название организации"));
                }
                if (StringUtils.isNotEmpty(entity.getInn()) && entity.getInn().length() != 12 && entity.getInn().length() != 5) {
                    setError(errors, "inn", MessagesTranslator.toLocale(DEFAULT_MESSAGE_INN_LENGTH, "12"));
                }
                if (StringUtils.isNotEmpty(entity.getInn()) && !checkInn(entity.getInn())) {
                    setError(errors, "inn", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER, "ИНН"));
                }
                if (StringUtils.isNotEmpty(foundPartner.getOgrn()) && !checkOgrn(foundPartner.getOgrn())) {
                    setError(errors, "ogrn", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER, "ogrn"));
                }
            } else if (foundPartner.getLegalType() == LegalType.PHYSICAL_PERSON) {
                if (StringUtils.isEmpty(foundPartner.getFirstName())) {
                    setError(errors, "partner_firstName", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "имя партнёра"));
                }
                if (StringUtils.isNotEmpty(entity.getInn()) && entity.getInn().length() != 12 && entity.getInn().length() != 5) {
                    setError(errors, "inn", MessagesTranslator.toLocale(DEFAULT_MESSAGE_INN_LENGTH, "12"));
                }
                if (StringUtils.isNotEmpty(entity.getInn()) && !checkInn(entity.getInn())) {
                    setError(errors, "inn", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER, "ИНН"));
                }
                if (StringUtils.isNotEmpty(foundPartner.getInn()) && !checkInn(foundPartner.getInn())) {
                    setError(errors, "inn", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER, "inn"));
                }
            }
        } else {
            setError(errors, "partner_legalForm", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "правовую форму партнёра"));
        }
    }
}
