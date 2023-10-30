package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PhoneMapper;
import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.model.PhoneCreate;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PhoneRepository;

@Loggable
public class PartnerPhoneServiceImpl extends PhoneServiceImpl {

    private final PartnerService partnerService;

    public PartnerPhoneServiceImpl(
        PartnerService partnerService,
        PhoneRepository phoneRepository,
        PhoneMapper phoneMapper
    ) {
        super(phoneRepository, phoneMapper);
        this.partnerService = partnerService;
    }

    @Override
    @Transactional
    public Phone savePhone(PhoneCreate phone) {
        var partnerId = phone.getUnifiedId();
        partnerService.existsPartner(phone.getDigitalId(), partnerId);
        return super.savePhone(phone);
    }
}
