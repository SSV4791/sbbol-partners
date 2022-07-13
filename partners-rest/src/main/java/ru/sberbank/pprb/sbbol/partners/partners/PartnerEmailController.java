package ru.sberbank.pprb.sbbol.partners.partners;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.PartnerEmailApi;
import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validation;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.EmailCreate;
import ru.sberbank.pprb.sbbol.partners.model.EmailsFilter;
import ru.sberbank.pprb.sbbol.partners.model.EmailsResponse;
import ru.sberbank.pprb.sbbol.partners.service.partner.EmailService;
import ru.sberbank.pprb.sbbol.partners.validation.EmailCreateValidationImpl;
import ru.sberbank.pprb.sbbol.partners.validation.EmailUpdateValidationImpl;
import ru.sberbank.pprb.sbbol.partners.validation.EmailsFilterValidationImpl;

import java.util.List;

@RestController
public class PartnerEmailController implements PartnerEmailApi {

    private final EmailService partnerEmailService;

    public PartnerEmailController(EmailService partnerEmailService) {
        this.partnerEmailService = partnerEmailService;
    }

    @Override
    public ResponseEntity<Email> create(@Validation(type = EmailCreateValidationImpl.class) EmailCreate email) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partnerEmailService.saveEmail(email));
    }

    @Override
    public ResponseEntity<Void> delete(String digitalId, List<String> ids) {
        partnerEmailService.deleteEmails(digitalId, ids);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<EmailsResponse> list(@Validation(type = EmailsFilterValidationImpl.class) EmailsFilter emailsFilter) {
        return ResponseEntity.ok(partnerEmailService.getEmails(emailsFilter));
    }

    @Override
    public ResponseEntity<Email> update(@Validation(type = EmailUpdateValidationImpl.class) Email email) {
        return ResponseEntity.ok(partnerEmailService.updateEmail(email));
    }
}
