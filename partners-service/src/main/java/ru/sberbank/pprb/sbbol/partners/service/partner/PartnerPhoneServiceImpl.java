package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Logged;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PhoneMapper;
import ru.sberbank.pprb.sbbol.partners.model.PhoneCreate;
import ru.sberbank.pprb.sbbol.partners.model.PhoneResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PhoneRepository;

import java.util.UUID;

@Logged(printRequestResponse = true)
public class PartnerPhoneServiceImpl extends PhoneServiceImpl {

    private final PartnerRepository partnerRepository;

    public PartnerPhoneServiceImpl(
        PartnerRepository partnerRepository,
        PhoneRepository phoneRepository,
        PhoneMapper phoneMapper
    ) {
        super(phoneRepository, phoneMapper);
        this.partnerRepository = partnerRepository;
    }

    @Override
    @Transactional
    public PhoneResponse savePhone(PhoneCreate phone) {
        var uuid = UUID.fromString(phone.getUnifiedId());
        var partner = partnerRepository.getByDigitalIdAndUuid(phone.getDigitalId(), uuid);
        if (partner.isEmpty()) {
            throw new EntryNotFoundException("partner", uuid);
        }
        return super.savePhone(phone);
    }
}
