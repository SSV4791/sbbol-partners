package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AddressMapper;
import ru.sberbank.pprb.sbbol.partners.model.Address;
import ru.sberbank.pprb.sbbol.partners.model.AddressCreate;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AddressRepository;

@Loggable
public class PartnerAddressServiceImpl extends AddressServiceImpl {

    private final PartnerService partnerService;

    public PartnerAddressServiceImpl(
        PartnerService partnerService,
        AddressRepository addressRepository,
        AddressMapper addressMapper
    ) {
        super(addressRepository, addressMapper);
        this.partnerService = partnerService;
    }

    @Override
    @Transactional
    public Address saveAddress(AddressCreate address) {
        partnerService.existsPartner(address.getDigitalId(), address.getUnifiedId());
        return super.saveAddress(address);
    }
}
