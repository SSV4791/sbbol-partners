package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Logged;
import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validation;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.ContactMapper;
import ru.sberbank.pprb.sbbol.partners.model.Contact;
import ru.sberbank.pprb.sbbol.partners.model.ContactCreate;
import ru.sberbank.pprb.sbbol.partners.model.ContactResponse;
import ru.sberbank.pprb.sbbol.partners.model.ContactsFilter;
import ru.sberbank.pprb.sbbol.partners.model.ContactsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ContactRepository;
import ru.sberbank.pprb.sbbol.partners.validation.ContactCreateValidationImpl;
import ru.sberbank.pprb.sbbol.partners.validation.ContactUpdateValidationImpl;
import ru.sberbank.pprb.sbbol.partners.validation.ContactsFilterValidationImpl;

import java.util.UUID;

@Logged(printRequestResponse = true)
public class ContactServiceImpl implements ContactService {

    public static final String DOCUMENT_NAME = "contact";

    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;

    public ContactServiceImpl(
        ContactRepository contactRepository,
        ContactMapper contactMapper
    ){
        this.contactRepository = contactRepository;
        this.contactMapper = contactMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public ContactResponse getContact(String digitalId, String id) {
        var contact = contactRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(id))
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, id));
        var response = contactMapper.toContact(contact);
        return new ContactResponse().contact(response);
    }

    @Override
    @Transactional(readOnly = true)
    public ContactsResponse getContacts(@Validation(type = ContactsFilterValidationImpl.class) ContactsFilter contactsFilter) {
        var response = contactRepository.findByFilter(contactsFilter);
        var contactResponse = new ContactsResponse();
        for (var entity : response) {
            contactResponse.addContactsItem(contactMapper.toContact(entity));
        }
        var pagination = contactsFilter.getPagination();
        contactResponse.setPagination(
            new Pagination()
                .offset(pagination.getOffset())
                .count(pagination.getCount())
        );
        var size = response.size();
        if (pagination.getCount() < size) {
            contactResponse.getPagination().hasNextPage(Boolean.TRUE);
            contactResponse.getContacts().remove(size - 1);
        }
        return contactResponse;
    }

    @Override
    @Transactional
    public ContactResponse saveContact(@Validation(type = ContactCreateValidationImpl.class) ContactCreate contact) {
        var requestContact = contactMapper.toContact(contact);
        var saveContact = contactRepository.save(requestContact);
        var response = contactMapper.toContact(saveContact);
        return new ContactResponse().contact(response);
    }

    @Override
    @Transactional
    public ContactResponse updateContact(@Validation(type = ContactUpdateValidationImpl.class) Contact contact) {
        var foundContact = contactRepository.getByDigitalIdAndUuid(contact.getDigitalId(), UUID.fromString(contact.getId()))
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, contact.getDigitalId(), contact.getId()));
        contactMapper.updateContact(contact, foundContact);
        var saveContact = contactRepository.save(foundContact);
        var response = contactMapper.toContact(saveContact);
        return new ContactResponse().contact(response);
    }

    @Override
    @Transactional
    public void deleteContact(String digitalId, String id) {
        var foundContact = contactRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(id));
        if (foundContact.isEmpty()) {
            throw new EntryNotFoundException(DOCUMENT_NAME, digitalId, id);
        }
        contactRepository.delete(foundContact.get());
    }
}
