package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Logged;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.exception.PartnerMigrationException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.EmailMapper;
import ru.sberbank.pprb.sbbol.partners.model.EmailCreate;
import ru.sberbank.pprb.sbbol.partners.model.EmailResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ContactRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.EmailRepository;
import ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapter;

import java.util.UUID;

@Logged(printRequestResponse = true)
public class ContactEmailServiceImpl extends EmailServiceImpl {

    private final ContactRepository contactRepository;
    private final LegacySbbolAdapter legacySbbolAdapter;

    public ContactEmailServiceImpl(
        ContactRepository contactRepository,
        EmailRepository emailRepository,
        EmailMapper emailMapper,
        LegacySbbolAdapter legacySbbolAdapter
    ) {
        super(emailRepository, emailMapper, legacySbbolAdapter);
        this.contactRepository = contactRepository;
        this.legacySbbolAdapter = legacySbbolAdapter;
    }

    @Override
    @Transactional
    public EmailResponse saveEmail(EmailCreate email) {
        if (legacySbbolAdapter.checkNotMigration(email.getDigitalId())) {
            throw new PartnerMigrationException();
        }
        var uuid = UUID.fromString(email.getUnifiedId());
        var partner = contactRepository.getByDigitalIdAndUuid(email.getDigitalId(), uuid);
        if (partner.isEmpty()) {
            throw new EntryNotFoundException("partner", uuid);
        }
        return super.saveEmail(email);
    }
}
