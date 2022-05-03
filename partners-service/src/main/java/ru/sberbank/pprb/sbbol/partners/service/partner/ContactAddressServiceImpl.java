package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Logged;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.exception.PartnerMigrationException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AddressMapper;
import ru.sberbank.pprb.sbbol.partners.model.AddressCreate;
import ru.sberbank.pprb.sbbol.partners.model.AddressResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AddressRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ContactRepository;
import ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapter;

import java.util.UUID;

@Logged(printRequestResponse = true)
public class ContactAddressServiceImpl extends AddressServiceImpl {

    private final ContactRepository contactRepository;
    private final LegacySbbolAdapter legacySbbolAdapter;

    public ContactAddressServiceImpl(
        ContactRepository contactRepository,
        AddressRepository addressRepository,
        AddressMapper addressMapper,
        LegacySbbolAdapter legacySbbolAdapter
    ) {
        super(addressRepository, addressMapper, legacySbbolAdapter);
        this.contactRepository = contactRepository;
        this.legacySbbolAdapter = legacySbbolAdapter;
    }

    @Override
    @Transactional
    public AddressResponse saveAddress(AddressCreate address) {
        if (legacySbbolAdapter.checkNotMigration(address.getDigitalId())) {
            throw new PartnerMigrationException();
        }
        var partner = contactRepository.getByDigitalIdAndUuid(address.getDigitalId(), UUID.fromString(address.getUnifiedId()));
        if (partner.isEmpty()) {
            throw new EntryNotFoundException("partner", address.getDigitalId());
        }
        return super.saveAddress(address);
    }
}
