package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Logged;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.exception.PartnerMigrationException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AddressMapper;
import ru.sberbank.pprb.sbbol.partners.model.AddressCreate;
import ru.sberbank.pprb.sbbol.partners.model.AddressResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AddressRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;
import ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapter;

import java.util.UUID;

@Logged(printRequestResponse = true)
public class PartnerAddressServiceImpl extends AddressServiceImpl {

    private final PartnerRepository partnerRepository;
    private final LegacySbbolAdapter legacySbbolAdapter;

    public PartnerAddressServiceImpl(
        PartnerRepository partnerRepository,
        AddressRepository addressRepository,
        AddressMapper addressMapper,
        LegacySbbolAdapter legacySbbolAdapter
    ) {
        super(addressRepository, addressMapper, legacySbbolAdapter);
        this.partnerRepository = partnerRepository;
        this.legacySbbolAdapter = legacySbbolAdapter;
    }

    @Override
    @Transactional
    public AddressResponse saveAddress(AddressCreate address) {
        if (legacySbbolAdapter.checkNotMigration(address.getDigitalId())) {
            throw new PartnerMigrationException();
        }
        var partner = partnerRepository.getByDigitalIdAndUuid(address.getDigitalId(), UUID.fromString(address.getUnifiedId()));
        if (partner.isEmpty()) {
            throw new EntryNotFoundException("partner", address.getDigitalId());
        }
        return super.saveAddress(address);
    }
}
