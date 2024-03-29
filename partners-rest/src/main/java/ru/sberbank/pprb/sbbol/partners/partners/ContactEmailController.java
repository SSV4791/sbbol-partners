package ru.sberbank.pprb.sbbol.partners.partners;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.ContactEmailApi;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.EmailCreate;
import ru.sberbank.pprb.sbbol.partners.model.EmailsFilter;
import ru.sberbank.pprb.sbbol.partners.model.EmailsResponse;
import ru.sberbank.pprb.sbbol.partners.service.partner.EmailService;

import java.util.List;
import java.util.UUID;

@Loggable
@RestController
public class ContactEmailController implements ContactEmailApi {

    private final EmailService contactEmailService;

    public ContactEmailController(EmailService contactEmailService) {
        this.contactEmailService = contactEmailService;
    }

    @Override
    public ResponseEntity<Email> create(EmailCreate email) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contactEmailService.saveEmail(email));
    }

    @Override
    public ResponseEntity<Void> delete(String digitalId, List<UUID> ids) {
        contactEmailService.deleteEmails(digitalId, ids);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<EmailsResponse> list(EmailsFilter emailsFilter) {
        return ResponseEntity.ok(contactEmailService.getEmails(emailsFilter));
    }

    @Override
    public ResponseEntity<Email> update(Email email) {
        return ResponseEntity.ok(contactEmailService.updateEmail(email));
    }
}
