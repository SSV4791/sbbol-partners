package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactEntity;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.exception.OptimisticLockException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.ContactMapper;
import ru.sberbank.pprb.sbbol.partners.model.Contact;
import ru.sberbank.pprb.sbbol.partners.model.ContactChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.ContactCreate;
import ru.sberbank.pprb.sbbol.partners.model.ContactsFilter;
import ru.sberbank.pprb.sbbol.partners.model.ContactsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AddressRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ContactRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Loggable
public class ContactServiceImpl implements ContactService {

    public static final String DOCUMENT_NAME = "contact";

    private final ContactRepository contactRepository;
    private final AddressRepository addressRepository;
    private final DocumentRepository documentRepository;
    private final ContactMapper contactMapper;
    private final PartnerService partnerService;

    public ContactServiceImpl(
        ContactRepository contactRepository,
        AddressRepository addressRepository,
        DocumentRepository documentRepository,
        ContactMapper contactMapper,
        PartnerService partnerService
    ) {
        this.contactRepository = contactRepository;
        this.addressRepository = addressRepository;
        this.documentRepository = documentRepository;
        this.contactMapper = contactMapper;
        this.partnerService = partnerService;
    }

    @Override
    @Transactional(readOnly = true)
    public Contact getContact(String digitalId, UUID id) {
        var contact = contactRepository.getByDigitalIdAndUuid(digitalId, id)
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
        partnerService.getPartner(contact.getDigitalId(), contact.getPartnerId());
        var requestContact = contactMapper.toContact(contact);
        var saveContact = contactRepository.save(requestContact);
        return contactMapper.toContact(saveContact);
    }

    @Override
    @Transactional
    public Contact updateContact(Contact contact) {
        var foundContact = findContactEntity(contact.getDigitalId(), contact.getId(), contact.getVersion());
        contactMapper.updateContact(contact, foundContact);
        return saveContact(foundContact);
    }

    @Override
    @Transactional
    public Contact patchContact(Contact contact) {
        var foundContact = findContactEntity(contact.getDigitalId(), contact.getId(), contact.getVersion());
        contactMapper.patchContact(contact, foundContact);
        return saveContact(foundContact);
    }

    @Override
    @Transactional
    public void deleteContacts(String digitalId, List<UUID> ids) {
        for (var contactId : ids) {
            var foundContact = contactRepository.getByDigitalIdAndUuid(digitalId, contactId)
                .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, contactId));
            contactRepository.delete(foundContact);
            addressRepository.deleteAll(addressRepository.findByDigitalIdAndUnifiedUuid(digitalId, contactId));
            documentRepository.deleteAll(documentRepository.findByDigitalIdAndUnifiedUuid(digitalId, contactId));
        }
    }

    @Override
    @Transactional
    public void saveOrPatchContacts(String digitalId, UUID partnerId, Set<ContactChangeFullModel> contacts) {
        Optional.ofNullable(contacts)
            .ifPresent(contactList ->
                contactList.forEach(contactChangeFullModel -> saveOrPatchContact(digitalId, partnerId, contactChangeFullModel)));
    }

    @Override
    @Transactional
    public void saveOrPatchContact(String digitalId, UUID partnerId, ContactChangeFullModel contactChangeFullModel) {
        if (Objects.nonNull(contactChangeFullModel.getId())) {
            var contact = contactMapper.toContact(contactChangeFullModel, digitalId, partnerId);
            patchContact(contact);
        } else {
            var contactCreate = contactMapper.toContactCreate(contactChangeFullModel, digitalId, partnerId);
            saveContact(contactCreate);
        }
    }

    private ContactEntity findContactEntity(String digitalId, UUID contactId, Long version) {
        var foundContact = contactRepository.getByDigitalIdAndUuid(digitalId, contactId)
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, contactId));
        if (!Objects.equals(version, foundContact.getVersion())) {
            throw new OptimisticLockException(foundContact.getVersion(), version);
        }
        return foundContact;
    }

    private Contact saveContact(ContactEntity contact) {
        var saveContact = contactRepository.save(contact);
        var response = contactMapper.toContact(saveContact);
        response.setVersion(response.getVersion() + 1);
        return response;
    }
}
