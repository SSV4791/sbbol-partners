package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.EmailMapper;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.EmailCreate;
import ru.sberbank.pprb.sbbol.partners.repository.partner.EmailRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;

@Loggable
public class PartnerEmailServiceImpl extends EmailServiceImpl {

    private final PartnerRepository partnerRepository;

    public PartnerEmailServiceImpl(
        PartnerRepository partnerRepository,
        EmailRepository emailRepository,
        EmailMapper emailMapper
    ) {
        super(emailRepository, emailMapper);
        this.partnerRepository = partnerRepository;
    }

    @Override
    @Transactional
    public Email saveEmail(EmailCreate email) {
        var partnerId = email.getUnifiedId();
        var partner = partnerRepository.getByDigitalIdAndUuid(email.getDigitalId(), partnerId);
        if (partner.isEmpty()) {
            throw new EntryNotFoundException("partner", partnerId);
        }
        return super.saveEmail(email);
    }
}
