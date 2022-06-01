package ru.sberbank.pprb.sbbol.partners.validation;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validator;
import ru.sberbank.pprb.sbbol.partners.config.MessagesTranslator;
import ru.sberbank.pprb.sbbol.partners.exception.MissingValueException;
import ru.sberbank.pprb.sbbol.partners.model.Contact;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ContactRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;

import java.util.List;
import java.util.UUID;

public class ContactUpdateValidationImpl extends AbstractValidatorImpl<Contact> {
    private static final String DOCUMENT_NAME = "contact";
    private final ContactRepository contactRepository;
    private final PartnerRepository partnerRepository;
    private final Validator<Email> emailUpdateValidator;
    private final Validator<Phone> phoneUpdateValidator;

    public ContactUpdateValidationImpl(
        ContactRepository contactRepository,
        PartnerRepository partnerRepository,
        Validator<Email> emailUpdateValidator,
        Validator<Phone> phoneUpdateValidator
    ) {
        this.contactRepository = contactRepository;
        this.partnerRepository = partnerRepository;
        this.emailUpdateValidator = emailUpdateValidator;
        this.phoneUpdateValidator = phoneUpdateValidator;
    }

    @Override
    @Transactional(readOnly = true)
    public void validator(List<String> errors, Contact entity) {
        commonValidationDigitalId(entity.getDigitalId());
        commonValidationUuid(entity.getPartnerId(), entity.getId());
        var foundContact = contactRepository.getByDigitalIdAndUuid(entity.getDigitalId(), UUID.fromString(entity.getId()))
            .orElseThrow(() -> new MissingValueException("Не найден объект " + DOCUMENT_NAME + " " + entity.getDigitalId() + " " + entity.getId()));
        var foundPartner = partnerRepository.getByDigitalIdAndUuid(entity.getDigitalId(), UUID.fromString(entity.getPartnerId()));
        if (foundPartner.isEmpty()) {
            throw new MissingValueException("Не найден partner " + entity.getDigitalId() + " " + entity.getPartnerId());
        }
        if (!entity.getVersion().equals(foundContact.getVersion())) {
            throw new OptimisticLockingFailureException("Версия записи в базе данных " + foundContact.getVersion() +
                " не равна версии записи в запросе version=" + entity.getVersion());
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
