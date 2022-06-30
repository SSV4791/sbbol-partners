package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.EmailMapper;
import ru.sberbank.pprb.sbbol.partners.model.EmailCreate;
import ru.sberbank.pprb.sbbol.partners.model.EmailResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ContactRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.EmailRepository;

import java.util.UUID;

@Loggable
public class ContactEmailServiceImpl extends EmailServiceImpl {

    private final ContactRepository contactRepository;

    public ContactEmailServiceImpl(
        ContactRepository contactRepository,
        EmailRepository emailRepository,
        EmailMapper emailMapper
    ) {
        super(emailRepository, emailMapper);
        this.contactRepository = contactRepository;
    }

    @Override
    @Transactional
    public EmailResponse saveEmail(EmailCreate email) {
        var uuid = UUID.fromString(email.getUnifiedId());
        var contact = contactRepository.getByDigitalIdAndUuid(email.getDigitalId(), uuid);
        if (contact.isEmpty()) {
            throw new EntryNotFoundException("contact", uuid);
        }
        return super.saveEmail(email);
    }
}
