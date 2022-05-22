package ru.sberbank.pprb.sbbol.partners.partners;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.ContactEmailApi;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.EmailCreate;
import ru.sberbank.pprb.sbbol.partners.model.EmailResponse;
import ru.sberbank.pprb.sbbol.partners.model.EmailsFilter;
import ru.sberbank.pprb.sbbol.partners.model.EmailsResponse;
import ru.sberbank.pprb.sbbol.partners.service.partner.EmailService;

@RestController
public class ContactEmailController implements ContactEmailApi {

    private final EmailService contactEmailService;

    public ContactEmailController(EmailService contactEmailService) {
        this.contactEmailService = contactEmailService;
    }

    @Override
    public ResponseEntity<EmailResponse> create(EmailCreate email) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contactEmailService.saveEmail(email));
    }

    @Override
    public ResponseEntity<Void> delete(String digitalId, String id) {
        contactEmailService.deleteEmail(digitalId, id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<EmailsResponse> list(EmailsFilter emailsFilter) {
        return ResponseEntity.ok(contactEmailService.getEmails(emailsFilter));
    }

    @Override
    public ResponseEntity<EmailResponse> update(Email email) {
        return ResponseEntity.ok(contactEmailService.updateEmail(email));
    }
}
