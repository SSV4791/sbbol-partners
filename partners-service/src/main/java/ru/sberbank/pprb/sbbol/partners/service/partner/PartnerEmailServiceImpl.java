package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.EmailMapper;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.EmailCreate;
import ru.sberbank.pprb.sbbol.partners.repository.partner.EmailRepository;

@Loggable
public class PartnerEmailServiceImpl extends EmailServiceImpl {

    private final PartnerService partnerService;

    public PartnerEmailServiceImpl(
        PartnerService partnerService,
        EmailRepository emailRepository,
        EmailMapper emailMapper
    ) {
        super(emailRepository, emailMapper);
        this.partnerService = partnerService;
    }

    @Override
    @Transactional
    public Email saveEmail(EmailCreate email) {
        var partnerId = email.getUnifiedId();
        partnerService.getPartner(email.getDigitalId(), partnerId);
        return super.saveEmail(email);
    }
}
