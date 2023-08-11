package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AddressMapper;
import ru.sberbank.pprb.sbbol.partners.model.Address;
import ru.sberbank.pprb.sbbol.partners.model.AddressCreate;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AddressRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;

@Loggable
public class PartnerAddressServiceImpl extends AddressServiceImpl {

    private final PartnerRepository partnerRepository;

    public PartnerAddressServiceImpl(
        PartnerRepository partnerRepository,
        AddressRepository addressRepository,
        AddressMapper addressMapper
    ) {
        super(addressRepository, addressMapper);
        this.partnerRepository = partnerRepository;
    }

    @Override
    @Transactional
    public Address saveAddress(AddressCreate address) {
        var partnerId = address.getUnifiedId();
        var partner = partnerRepository.getByDigitalIdAndUuid(address.getDigitalId(), partnerId);
        if (partner.isEmpty()) {
            throw new EntryNotFoundException("partner", partnerId);
        }
        return super.saveAddress(address);
    }
}
