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
import ru.sberbank.pprb.sbbol.partners.model.ContactCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.ContactsFilter;
import ru.sberbank.pprb.sbbol.partners.model.ContactsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ContactRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static org.springframework.util.CollectionUtils.isEmpty;

@Loggable
public class ContactServiceImpl implements ContactService {

    public static final String DOCUMENT_NAME = "contact";

    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;
    private final PartnerService partnerService;
    private final AddressService contactAddressService;
    private final DocumentService contactDocumentService;

    public ContactServiceImpl(
        ContactRepository contactRepository,
        ContactMapper contactMapper,
        PartnerService partnerService,
        AddressService contactAddressService,
        DocumentService contactDocumentService
    ) {
        this.contactRepository = contactRepository;
        this.contactMapper = contactMapper;
        this.partnerService = partnerService;
        this.contactAddressService = contactAddressService;
        this.contactDocumentService = contactDocumentService;
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
    public List<Contact> getContactsByPartnerUuid(String digitalId, UUID partnerUuid) {
        return contactRepository.findByDigitalIdAndPartnerUuid(digitalId, partnerUuid).stream()
            .map(contactMapper::toContact)
            .toList();
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
        partnerService.existsPartner(contact.getDigitalId(), contact.getPartnerId());
        var requestContact = contactMapper.toContact(contact);
        var saveContact = contactRepository.save(requestContact);
        return contactMapper.toContact(saveContact);
    }

    @Override
    @Transactional
    public List<Contact> saveContacts(String digitalId, UUID unifiedUuid, Set<ContactCreateFullModel> contacts) {
        if (isEmpty(contacts)) {
            return emptyList();
        }
        return contacts.stream()
            .map(contact -> contactMapper.toContact(contact, digitalId, unifiedUuid))
            .map(this::saveContact)
            .toList();
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
    public void deleteContacts(String digitalId, List<UUID> contactIds) {
        contactIds.forEach(contactId -> deleteContact(digitalId, contactId));
    }

    @Override
    @Transactional
    public void deleteContactsByPartnerUuid(String digitalId, UUID partnerUuid) {
        contactRepository.findByDigitalIdAndPartnerUuid(digitalId, partnerUuid)
            .forEach(contact -> deleteContact(contact.getDigitalId(), contact.getUuid()));
    }

    private void deleteContact(String digitalId, UUID contactId) {
        var foundContact = contactRepository.getByDigitalIdAndUuid(digitalId, contactId)
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, contactId));
        contactRepository.delete(foundContact);
        contactAddressService.deleteAddressesByUnifiedUuid(digitalId, contactId);
        contactDocumentService.deleteDocumentsByUnifiedUuid(digitalId, contactId);
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
