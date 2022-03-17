package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.aspect.logger.Logged;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.EmailMapper;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.EmailResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ContactRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.EmailRepository;

import java.util.UUID;

@Logged(printRequestResponse = true)
public class ContactEmailServiceImpl extends EmailServiceImpl {

    private final ContactRepository contactRepository;

    public ContactEmailServiceImpl(ContactRepository contactRepository, EmailRepository emailRepository, EmailMapper emailMapper) {
        super(emailRepository, emailMapper);
        this.contactRepository = contactRepository;
    }

    @Override
    public EmailResponse updateEmail(Email email) {
        var partner = contactRepository.getByUuid(UUID.fromString(email.getUnifiedId()));
        if (partner == null) {
            throw new EntryNotFoundException("partner", UUID.fromString(email.getUnifiedId()));
        }
        return super.updateEmail(email);
    }
}
