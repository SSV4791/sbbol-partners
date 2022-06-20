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
import java.util.UUID;

public class ContactUpdateValidationImpl extends AbstractValidatorImpl<Contact> {
    private static final String DOCUMENT_NAME = "contact";
    private static final String DOCUMENT_NAME_OTHER = "partner";
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
    public void validator(List<String> errors, Contact entity) {
        var foundContact = contactRepository.getByDigitalIdAndUuid(entity.getDigitalId(), UUID.fromString(entity.getId()))
            .orElseThrow(() -> new MissingValueException(MessagesTranslator.toLocale(DEFAULT_MESSAGE_OBJECT_NOT_FOUND_ERROR, DOCUMENT_NAME, entity.getDigitalId(), entity.getId())));
        if(foundContact.getPartnerUuid().toString().equals(entity.getPartnerId())) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_OBJECT_NOT_FOUND_ERROR, DOCUMENT_NAME_OTHER, entity.getDigitalId(), entity.getId()));
        }
        commonValidationDigitalId(errors,entity.getDigitalId());
        commonValidationUuid(errors,entity.getPartnerId(), entity.getId());
        if (!entity.getVersion().equals(foundContact.getVersion())) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_VERSION_ERROR, foundContact.getVersion().toString(), entity.getVersion().toString()));
        }
        if (entity.getVersion() == null) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELD_IS_NULL, "version"));
        }
        if (StringUtils.isNotEmpty(entity.getFirstName()) && entity.getFirstName().length() > FIRST_NAME_MAX_LENGTH_VALIDATION) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_LENGTH, "fistName", "1", "50"));
        }
        if (StringUtils.isNotEmpty(entity.getOrgName()) && entity.getOrgName().length() > ORG_NAME_MAX_LENGTH_VALIDATION) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_LENGTH, "orgName", "1", "350"));
        }
        if (StringUtils.isNotEmpty(entity.getSecondName()) && entity.getSecondName().length() > SECOND_NAME_MAX_LENGTH_VALIDATION) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_LENGTH, "secondName", "1", "50"));
        }
        if (StringUtils.isNotEmpty(entity.getMiddleName()) && entity.getMiddleName().length() > MIDDLE_NAME_MAX_LENGTH_VALIDATION) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_LENGTH, "middleName", "1", "50"));
        }
        if (StringUtils.isNotEmpty(entity.getPosition()) && entity.getPosition().length() > POSITION_NAME_MAX_LENGTH_VALIDATION) {
            errors.add(MessagesTranslator.toLocale(DEFAULT_MESSAGE_FIELDS_IS_LENGTH, "position", "1", "100"));
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
