package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Logged;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactEmailEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactPhoneEntity;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.ContactMapper;
import ru.sberbank.pprb.sbbol.partners.model.Contact;
import ru.sberbank.pprb.sbbol.partners.model.ContactResponse;
import ru.sberbank.pprb.sbbol.partners.model.ContactsFilter;
import ru.sberbank.pprb.sbbol.partners.model.ContactsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ContactRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;

import java.util.List;
import java.util.UUID;

@Service
@Logged(printRequestResponse = true)
public class ContactServiceImpl implements ContactService {

    private final PartnerRepository partnerRepository;
    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;

    public ContactServiceImpl(
        PartnerRepository partnerRepository,
        ContactRepository contactRepository,
        ContactMapper contactMapper
    ) {
        this.partnerRepository = partnerRepository;
        this.contactRepository = contactRepository;
        this.contactMapper = contactMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public ContactResponse getContact(String digitalId, String id) {
        var contact = contactRepository.getByDigitalIdAndId(digitalId, UUID.fromString(id));
        if (contact == null) {
            throw new EntryNotFoundException("contact", digitalId, id);
        }
        var response = contactMapper.toContact(contact);
        return new ContactResponse().contact(response);
    }

    @Override
    @Transactional(readOnly = true)
    public ContactsResponse getContacts(ContactsFilter contactsFilter) {
        var response = contactRepository.findByFilter(contactsFilter);
        var contactResponse = new ContactsResponse();
        for (var entity : response) {
            contactResponse.addContactsItem(contactMapper.toContact(entity));
        }
        contactResponse.setPagination(
            new Pagination()
                .offset(contactsFilter.getPagination().getOffset())
                .count(contactsFilter.getPagination().getCount())
        );
        return contactResponse;
    }

    @Override
    @Transactional
    public ContactResponse saveContact(Contact contact) {
        var partner = partnerRepository.getByDigitalIdAndId(contact.getDigitalId(), UUID.fromString(contact.getPartnerUuid()));
        if (partner == null) {
            throw new EntryNotFoundException("partner", contact.getDigitalId(), contact.getUuid());
        }
        var requestContact = contactMapper.toContact(contact);
        for (ContactEmailEntity email : requestContact.getEmails()) {
            email.setContact(requestContact);
        }
        for (ContactPhoneEntity phone : requestContact.getPhones()) {
            phone.setContact(requestContact);
        }
        var saveContact = contactRepository.save(requestContact);
        var response = contactMapper.toContact(saveContact);
        return new ContactResponse().contact(response);
    }

    @Override
    @Transactional
    public ContactResponse updateContact(Contact contact) {
        var searchContact = contactRepository.getByDigitalIdAndId(contact.getDigitalId(), UUID.fromString(contact.getUuid()));
        if (searchContact == null) {
            throw new EntryNotFoundException("contact", contact.getDigitalId(), contact.getUuid());
        }
        contactMapper.updateContact(contact, searchContact);
        var saveContact = contactRepository.save(searchContact);
        var response = contactMapper.toContact(saveContact);
        return new ContactResponse().contact(response);
    }

    @Override
    @Transactional
    public Error deleteContact(String digitalId, String id) {
        var searchContact = contactRepository.getByDigitalIdAndId(digitalId, UUID.fromString(id));
        if (searchContact == null) {
            throw new EntryNotFoundException("contact", digitalId, id);
        }
        contactRepository.delete(searchContact);
        return new Error();
    }
}
