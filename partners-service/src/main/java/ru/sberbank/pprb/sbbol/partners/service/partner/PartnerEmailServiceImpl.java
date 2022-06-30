package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validation;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.EmailMapper;
import ru.sberbank.pprb.sbbol.partners.model.EmailCreate;
import ru.sberbank.pprb.sbbol.partners.model.EmailResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.EmailRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;
import ru.sberbank.pprb.sbbol.partners.validation.EmailCreateValidationImpl;

import java.util.UUID;

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
    public EmailResponse saveEmail(@Validation(type = EmailCreateValidationImpl.class) EmailCreate email) {
        var uuid = UUID.fromString(email.getUnifiedId());
        var partner = partnerRepository.getByDigitalIdAndUuid(email.getDigitalId(), uuid);
        if (partner.isEmpty()) {
            throw new EntryNotFoundException("partner", uuid);
        }
        return super.saveEmail(email);
    }
}
