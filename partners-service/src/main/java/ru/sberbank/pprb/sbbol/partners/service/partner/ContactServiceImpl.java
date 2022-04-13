package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Logged;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.exception.PartnerMigrationException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.ContactMapper;
import ru.sberbank.pprb.sbbol.partners.model.Contact;
import ru.sberbank.pprb.sbbol.partners.model.ContactCreate;
import ru.sberbank.pprb.sbbol.partners.model.ContactResponse;
import ru.sberbank.pprb.sbbol.partners.model.ContactsFilter;
import ru.sberbank.pprb.sbbol.partners.model.ContactsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ContactRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;

import java.util.UUID;

@Logged(printRequestResponse = true)
public class ContactServiceImpl implements ContactService {

    public static final String DOCUMENT_NAME = "contact";

    private final PartnerRepository partnerRepository;
    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;
    private final LegacySbbolAdapter legacySbbolAdapter;

    public ContactServiceImpl(
        PartnerRepository partnerRepository,
        ContactRepository contactRepository,
        ContactMapper contactMapper,
        LegacySbbolAdapter legacySbbolAdapter
    ) {
        this.partnerRepository = partnerRepository;
        this.contactRepository = contactRepository;
        this.contactMapper = contactMapper;
        this.legacySbbolAdapter = legacySbbolAdapter;
    }

    @Override
    @Transactional(readOnly = true)
    public ContactResponse getContact(String digitalId, String id) {
        if (legacySbbolAdapter.checkNotMigration(digitalId)) {
            throw new PartnerMigrationException();
        }
        var contact = contactRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(id))
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, id));
        var response = contactMapper.toContact(contact);
        return new ContactResponse().contact(response);
    }

    @Override
    @Transactional(readOnly = true)
    public ContactsResponse getContacts(ContactsFilter contactsFilter) {
        if (legacySbbolAdapter.checkNotMigration(contactsFilter.getDigitalId())) {
            throw new PartnerMigrationException();
        }
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
    public ContactResponse saveContact(ContactCreate contact) {
        if (legacySbbolAdapter.checkNotMigration(contact.getDigitalId())) {
            throw new PartnerMigrationException();
        }
        var partner = partnerRepository.getByDigitalIdAndUuid(contact.getDigitalId(), UUID.fromString(contact.getPartnerId()));
        if (partner.isEmpty()) {
            throw new EntryNotFoundException("partner", contact.getDigitalId());
        }
        var requestContact = contactMapper.toContact(contact);
        var saveContact = contactRepository.save(requestContact);
        var response = contactMapper.toContact(saveContact);
        return new ContactResponse().contact(response);
    }

    @Override
    @Transactional
    public ContactResponse updateContact(Contact contact) {
        if (legacySbbolAdapter.checkNotMigration(contact.getDigitalId())) {
            throw new PartnerMigrationException();
        }
        var foundContact = contactRepository.getByDigitalIdAndUuid(contact.getDigitalId(), UUID.fromString(contact.getId()))
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, contact.getDigitalId(), contact.getId()));
        if (!contact.getVersion().equals(foundContact.getVersion())) {
            throw new OptimisticLockingFailureException("Версия записи в базе данных " + foundContact.getVersion() +
                " не равна версии записи в запросе version=" + contact.getVersion());
        }
        contactMapper.updateContact(contact, foundContact);
        var saveContact = contactRepository.save(foundContact);
        var response = contactMapper.toContact(saveContact);
        return new ContactResponse().contact(response);
    }

    @Override
    @Transactional
    public void deleteContact(String digitalId, String id) {
        if (legacySbbolAdapter.checkNotMigration(digitalId)) {
            throw new PartnerMigrationException();
        }
        var foundContact = contactRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(id));
        if (foundContact.isEmpty()) {
            throw new EntryNotFoundException(DOCUMENT_NAME, digitalId, id);
        }
        contactRepository.delete(foundContact.get());
    }
}
