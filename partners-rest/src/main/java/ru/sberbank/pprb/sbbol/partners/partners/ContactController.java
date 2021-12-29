package ru.sberbank.pprb.sbbol.partners.partners;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.PartnerContactsApi;
import ru.sberbank.pprb.sbbol.partners.model.Contact;
import ru.sberbank.pprb.sbbol.partners.model.ContactResponse;
import ru.sberbank.pprb.sbbol.partners.model.ContactsFilter;
import ru.sberbank.pprb.sbbol.partners.model.ContactsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.service.partner.ContactService;

@RestController
public class ContactController implements PartnerContactsApi {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @Override
    public ResponseEntity<ContactResponse> create(Contact contact) {
        return ResponseEntity.ok(contactService.saveContact(contact));
    }

    @Override
    public ResponseEntity<Error> delete(String digitalId, String id) {
        return ResponseEntity.ok(contactService.deleteContact(digitalId, id));
    }

    @Override
    public ResponseEntity<ContactResponse> getById(String digitalId, String id) {
        return ResponseEntity.ok(contactService.getContact(digitalId, id));
    }

    @Override
    public ResponseEntity<ContactsResponse> list(ContactsFilter contactsFilter) {
        return ResponseEntity.ok(contactService.getContacts(contactsFilter));
    }

    @Override
    public ResponseEntity<ContactResponse> update(Contact contact) {
        return ResponseEntity.ok(contactService.updateContact(contact));
    }
}
