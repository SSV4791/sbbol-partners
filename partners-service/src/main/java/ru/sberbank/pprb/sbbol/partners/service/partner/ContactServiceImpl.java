package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.exception.OptimisticLockException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.ContactMapper;
import ru.sberbank.pprb.sbbol.partners.model.Contact;
import ru.sberbank.pprb.sbbol.partners.model.ContactCreate;
import ru.sberbank.pprb.sbbol.partners.model.ContactsFilter;
import ru.sberbank.pprb.sbbol.partners.model.ContactsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AddressRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ContactRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.EmailRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PhoneRepository;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Loggable
public class ContactServiceImpl implements ContactService {

    public static final String DOCUMENT_NAME = "contact";

    private final ContactRepository contactRepository;
    private final EmailRepository emailRepository;
    private final PhoneRepository phoneRepository;
    private final AddressRepository addressRepository;
    private final DocumentRepository documentRepository;
    private final ContactMapper contactMapper;

    public ContactServiceImpl(
        ContactRepository contactRepository,
        EmailRepository emailRepository,
        PhoneRepository phoneRepository,
        AddressRepository addressRepository,
        DocumentRepository documentRepository,
        ContactMapper contactMapper
    ) {
        this.contactRepository = contactRepository;
        this.emailRepository = emailRepository;
        this.phoneRepository = phoneRepository;
        this.addressRepository = addressRepository;
        this.documentRepository = documentRepository;
        this.contactMapper = contactMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Contact getContact(String digitalId, String id) {
        var contact = contactRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(id))
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, id));
        return contactMapper.toContact(contact);
    }

    @Override
    @Transactional(readOnly = true)
    public ContactsResponse getContacts(ContactsFilter contactsFilter) {
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
    public Contact saveContact(ContactCreate contact) {
        var requestContact = contactMapper.toContact(contact);
        var saveContact = contactRepository.save(requestContact);
        return contactMapper.toContact(saveContact);
    }

    @Override
    @Transactional
    public Contact updateContact(Contact contact) {
        var foundContact = contactRepository.getByDigitalIdAndUuid(contact.getDigitalId(), UUID.fromString(contact.getId()))
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, contact.getDigitalId(), contact.getId()));
        if (!Objects.equals(contact.getVersion(), foundContact.getVersion())) {
            throw new OptimisticLockException(foundContact.getVersion(), contact.getVersion());
        }
        contactMapper.updateContact(contact, foundContact);
        var saveContact = contactRepository.save(foundContact);
        var response = contactMapper.toContact(saveContact);
        response.setVersion(response.getVersion() + 1);
        return response;
    }

    @Override
    @Transactional
    public void deleteContacts(String digitalId, List<String> ids) {
        for (String id : ids) {
            var contactUuid = contactMapper.mapUuid(id);
            var foundContact = contactRepository.getByDigitalIdAndUuid(digitalId, contactUuid)
                .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, id));
            contactRepository.delete(foundContact);
            emailRepository.deleteAll(emailRepository.findByDigitalIdAndUnifiedUuid(digitalId, contactUuid));
            phoneRepository.deleteAll(phoneRepository.findByDigitalIdAndUnifiedUuid(digitalId, contactUuid));
            addressRepository.deleteAll(addressRepository.findByDigitalIdAndUnifiedUuid(digitalId, contactUuid));
            documentRepository.deleteAll(documentRepository.findByDigitalIdAndUnifiedUuid(digitalId, contactUuid));
        }
    }
}
