package ru.sberbank.pprb.sbbol.partners.partners;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.PartnerContactsApi;
import ru.sberbank.pprb.sbbol.partners.model.Contact;
import ru.sberbank.pprb.sbbol.partners.model.ContactCreate;
import ru.sberbank.pprb.sbbol.partners.model.ContactsFilter;
import ru.sberbank.pprb.sbbol.partners.model.ContactsResponse;
import ru.sberbank.pprb.sbbol.partners.service.partner.ContactService;

import java.util.List;

@RestController
public class ContactController implements PartnerContactsApi {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @Override
    public ResponseEntity<Contact> create(ContactCreate contact) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contactService.saveContact(contact));
    }

    @Override
    public ResponseEntity<Void> delete(String digitalId, List<String> ids) {
        contactService.deleteContacts(digitalId, ids);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Contact> getById(String digitalId, String id) {
        return ResponseEntity.ok(contactService.getContact(digitalId, id));
    }

    @Override
    public ResponseEntity<ContactsResponse> list(ContactsFilter contactsFilter) {
        return ResponseEntity.ok(contactService.getContacts(contactsFilter));
    }

    @Override
    public ResponseEntity<Contact> update(Contact contact) {
        return ResponseEntity.ok(contactService.updateContact(contact));
    }
}
