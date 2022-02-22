package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.aspect.logger.Logged;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PhoneMapper;
import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.model.PhoneResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PhoneRepository;

import java.util.UUID;

@Logged(printRequestResponse = true)
public class PartnerPhoneServiceImpl extends PhoneServiceImpl {

    private final PartnerRepository partnerRepository;

    public PartnerPhoneServiceImpl(PartnerRepository partnerRepository, PhoneRepository phoneRepository, PhoneMapper phoneMapper) {
        super(phoneRepository, phoneMapper);
        this.partnerRepository = partnerRepository;
    }

    @Override
    public PhoneResponse savePhone(Phone phone) {
        var uuid = UUID.fromString(phone.getUnifiedId());
        var partner = partnerRepository.getByUuid(uuid);
        if (partner == null) {
            throw new EntryNotFoundException("partner", uuid);
        }
        return super.savePhone(phone);
    }
}
