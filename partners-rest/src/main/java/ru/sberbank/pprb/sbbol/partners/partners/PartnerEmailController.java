package ru.sberbank.pprb.sbbol.partners.partners;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.PartnerEmailApi;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.EmailResponse;
import ru.sberbank.pprb.sbbol.partners.model.EmailsFilter;
import ru.sberbank.pprb.sbbol.partners.model.EmailsResponse;
import ru.sberbank.pprb.sbbol.partners.service.partner.EmailService;

@RestController
public class PartnerEmailController implements PartnerEmailApi {

    private final EmailService partnerEmailService;

    public PartnerEmailController(EmailService partnerEmailService) {
        this.partnerEmailService = partnerEmailService;
    }

    @Override
    public ResponseEntity<EmailResponse> create(Email email) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partnerEmailService.saveEmail(email));
    }

    @Override
    public ResponseEntity<Void> delete(String digitalId, String id) {
        partnerEmailService.deleteEmail(digitalId, id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<EmailsResponse> list(EmailsFilter emailsFilter) {
        return ResponseEntity.ok(partnerEmailService.getEmails(emailsFilter));
    }

    @Override
    public ResponseEntity<EmailResponse> update(Email email) {
        return ResponseEntity.ok(partnerEmailService.updateEmail(email));
    }
}
