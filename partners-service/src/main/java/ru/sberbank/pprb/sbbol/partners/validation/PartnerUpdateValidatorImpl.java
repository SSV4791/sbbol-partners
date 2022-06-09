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
import java.util.UUID;

import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerValidation.checkInn;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePartnerValidation.checkOgrn;

public class PartnerUpdateValidatorImpl extends AbstractValidatorImpl<Partner> {
    private static final String DOCUMENT_NAME = "partner";
    private static final String LEGAL_FORM_LEGAL_ENTITY = "legalForm.LEGAL_ENTITY";
    private static final String LEGAL_FORM_ENTREPRENEUR = "legalForm.ENTREPRENEUR";
    private static final String LEGAL_FORM_PHYSICAL_PERSON = "legalForm.PHYSICAL_PERSON";
    private static final String DEFAULT_MESSAGE_INN_LENGTH = "partner.inn_length";
    private static final String DEFAULT_MESSAGE_KPP_LENGTH = "partner.kpp.length";

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
    public void validator(List<String> errors, Partner entity) {
        checkLegalFormProperty(errors, entity);
        commonValidationDigitalId(errors, entity.getDigitalId());
        commonValidationUuid(errors, entity.getId());
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
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "version"));
        }
    }

    private void checkLegalFormProperty(List<String> errors, Partner entity) {
        var foundPartner = partnerRepository.getByDigitalIdAndUuid(entity.getDigitalId(), UUID.fromString(entity.getId()))
            .orElseThrow(() -> new MissingValueException(MessagesTranslator.toLocale(DEFAULT_MESSAGE_OBJECT_NOT_FOUND_ERROR, DOCUMENT_NAME, entity.getDigitalId(), entity.getId())));
        if (!entity.getVersion().equals(foundPartner.getVersion())) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_VERSION_ERROR, foundPartner.getVersion().toString(), entity.getVersion().toString()));
        }
        partnerMapper.updatePartner(entity, foundPartner);
        if (foundPartner.getLegalType() != null) {
            if (foundPartner.getLegalType() == LegalType.LEGAL_ENTITY) {
                if (StringUtils.isEmpty(foundPartner.getOrgName())) {
                    errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "orgName", LEGAL_FORM_LEGAL_ENTITY));
                }
                if (StringUtils.isNotEmpty(foundPartner.getInn()) && !checkInn(foundPartner.getInn())) {
                    errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER, "inn"));
                }
                if (StringUtils.isNotEmpty(foundPartner.getOgrn()) && !checkOgrn(foundPartner.getOgrn())) {
                    errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER, "ogrn"));
                }
            } else if (foundPartner.getLegalType() == LegalType.ENTREPRENEUR) {
                if (StringUtils.isEmpty(foundPartner.getOrgName())) {
                    errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "orgName", LEGAL_FORM_ENTREPRENEUR));
                }
                if (StringUtils.isNotEmpty(foundPartner.getInn()) && !checkInn(foundPartner.getInn())) {
                    errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER, "inn"));
                }
                if (StringUtils.isNotEmpty(foundPartner.getOgrn()) && !checkOgrn(foundPartner.getOgrn())) {
                    errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER, "ogrn"));
                }
            } else if (foundPartner.getLegalType() == LegalType.PHYSICAL_PERSON) {
                if (StringUtils.isEmpty(foundPartner.getFirstName())) {
                    errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "firstName", LEGAL_FORM_PHYSICAL_PERSON));
                }
                if (StringUtils.isNotEmpty(foundPartner.getInn()) && foundPartner.getInn().length() != 12) {
                    errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_INN_LENGTH, "inn", LEGAL_FORM_PHYSICAL_PERSON, "12"));
                }
                if (StringUtils.isNotEmpty(foundPartner.getInn()) && !checkInn(foundPartner.getInn())) {
                    errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_CONTROL_NUMBER, "inn"));
                }
            }
        } else {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "legalForm"));
        }
    }
}
