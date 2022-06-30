package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.exception.MissingValueException;
import ru.sberbank.pprb.sbbol.partners.model.ContactCreate;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static ru.sberbank.pprb.sbbol.partners.validation.common.BaseEmailValidation.commonValidationChildEmail;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BasePhoneValidation.commonValidationChildPhone;
import static ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidation.setError;

public class ContactCreateValidationImpl extends AbstractValidatorImpl<ContactCreate> {

    private static final String DOCUMENT_NAME = "partner";
    private static final String DEFAULT_LENGTH = "default.field.max_length";
    private final PartnerRepository partnerRepository;

    public ContactCreateValidationImpl(PartnerRepository partnerRepository) {
        this.partnerRepository = partnerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void validator(Map<String, List<String>> errors, ContactCreate entity) {
        var partner = partnerRepository.getByDigitalIdAndUuid(entity.getDigitalId(), UUID.fromString(entity.getPartnerId()));
        if (partner.isEmpty()) {
            throw new MissingValueException(MessagesTranslator.toLocale(DEFAULT_MESSAGE_OBJECT_NOT_FOUND_ERROR, DOCUMENT_NAME, entity.getDigitalId(), entity.getPartnerId()));
        }
        commonValidationUuid(errors, entity.getPartnerId());
        commonValidationDigitalId(errors, entity.getDigitalId());
        if (StringUtils.isNotEmpty(entity.getFirstName()) && entity.getFirstName().length() > FIRST_NAME_MAX_LENGTH_VALIDATION) {
            setError(errors, "contact_firstName", MessagesTranslator.toLocale(DEFAULT_LENGTH, "50"));
        }
        if (StringUtils.isNotEmpty(entity.getOrgName()) && entity.getOrgName().length() > ORG_NAME_MAX_LENGTH_VALIDATION) {
            setError(errors, "contact_orgName", MessagesTranslator.toLocale(DEFAULT_LENGTH, "350"));
        }
        if (StringUtils.isNotEmpty(entity.getSecondName()) && entity.getSecondName().length() > SECOND_NAME_MAX_LENGTH_VALIDATION) {
            setError(errors, "contact_secondName", MessagesTranslator.toLocale(DEFAULT_LENGTH, "50"));
        }
        if (StringUtils.isNotEmpty(entity.getMiddleName()) && entity.getMiddleName().length() > MIDDLE_NAME_MAX_LENGTH_VALIDATION) {
            setError(errors, "contact_middleName", MessagesTranslator.toLocale(DEFAULT_LENGTH, "50"));
        }
        if (StringUtils.isNotEmpty(entity.getPosition()) && entity.getPosition().length() > POSITION_NAME_MAX_LENGTH_VALIDATION) {
            setError(errors, "contact_position", MessagesTranslator.toLocale(DEFAULT_LENGTH, "100"));
        }
        if (entity.getLegalForm() == null) {
            setError(errors, "contact_legalForm", MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_NULL, "правовую форму контакта"));
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
}
