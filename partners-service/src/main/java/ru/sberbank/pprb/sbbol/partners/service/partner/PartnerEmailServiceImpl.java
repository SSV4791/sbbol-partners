package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.aspect.logger.Logged;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.EmailMapper;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.EmailResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.EmailRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;

import java.util.UUID;

@Logged(printRequestResponse = true)
public class PartnerEmailServiceImpl extends EmailServiceImpl {

    private final PartnerRepository partnerRepository;

    public PartnerEmailServiceImpl(PartnerRepository partnerRepository, EmailRepository emailRepository, EmailMapper emailMapper) {
        super(emailRepository, emailMapper);
        this.partnerRepository = partnerRepository;
    }

    @Override
    public EmailResponse saveEmail(Email email) {
        var uuid = UUID.fromString(email.getUnifiedId());
        var partner = partnerRepository.getByUuid(uuid);
        if (partner == null) {
            throw new EntryNotFoundException("partner", uuid);
        }
        return super.saveEmail(email);
    }
}
