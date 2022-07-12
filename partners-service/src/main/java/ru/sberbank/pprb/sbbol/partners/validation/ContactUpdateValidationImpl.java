package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.exception.MissingValueException;
import ru.sberbank.pprb.sbbol.partners.model.Contact;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ContactRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static ru.sberbank.pprb.sbbol.partners.validation.common.BaseValidation.setError;

public class ContactUpdateValidationImpl extends AbstractValidatorImpl<Contact> {
    private static final String DOCUMENT_NAME = "contact";
    private static final String DOCUMENT_NAME_OTHER = "partner";
    private static final String DEFAULT_LENGTH = "default.field.max_length";
    private final ContactRepository contactRepository;
    private final Validator<Email> emailUpdateValidator;
    private final Validator<Phone> phoneUpdateValidator;

    public ContactUpdateValidationImpl(
        ContactRepository contactRepository,
        Validator<Email> emailUpdateValidator,
        Validator<Phone> phoneUpdateValidator
    ) {
        this.contactRepository = contactRepository;
        this.emailUpdateValidator = emailUpdateValidator;
        this.phoneUpdateValidator = phoneUpdateValidator;
    }

    @Override
    @Transactional(readOnly = true)
    public void validator(Map<String, List<String>> errors, Contact entity) {
        var foundContact = contactRepository.getByDigitalIdAndUuid(entity.getDigitalId(), UUID.fromString(entity.getId()))
            .orElseThrow(() -> new MissingValueException(MessagesTranslator.toLocale(DEFAULT_MESSAGE_OBJECT_NOT_FOUND_ERROR, DOCUMENT_NAME, entity.getDigitalId(), entity.getId())));
        if (!foundContact.getPartnerUuid().toString().equals(entity.getPartnerId())) {
            setError(errors, "common", MessagesTranslator.toLocale(DEFAULT_MESSAGE_OBJECT_NOT_FOUND_ERROR, DOCUMENT_NAME_OTHER, entity.getDigitalId(), entity.getId()));
        }
        commonValidationDigitalId(errors, entity.getDigitalId());
        commonValidationUuid(errors, entity.getPartnerId(), entity.getId());
        if (!Objects.equals(entity.getVersion(), foundContact.getVersion())) {
            setError(errors, "common", MessagesTranslator.toLocale(DEFAULT_MESSAGE_VERSION_ERROR, foundContact.getVersion(), entity.getVersion()));
        }
        if (entity.getVersion() == null) {
            setError(errors, "common", MessagesTranslator.toLocale(DEFAULT_MESSAGE_CAMMON_FIELD_IS_NULL, "version"));
        }
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
    }
}
