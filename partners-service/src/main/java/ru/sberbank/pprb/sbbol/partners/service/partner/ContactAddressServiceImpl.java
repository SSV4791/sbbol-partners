package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Logged;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AddressMapper;
import ru.sberbank.pprb.sbbol.partners.model.AddressCreate;
import ru.sberbank.pprb.sbbol.partners.model.AddressResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AddressRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ContactRepository;

import java.util.UUID;

@Logged(printRequestResponse = true)
public class ContactAddressServiceImpl extends AddressServiceImpl {

    private final ContactRepository contactRepository;

    public ContactAddressServiceImpl(
        ContactRepository contactRepository,
        AddressRepository addressRepository,
        AddressMapper addressMapper
    ) {
        super(addressRepository, addressMapper);
        this.contactRepository = contactRepository;
    }

    @Override
    @Transactional
    public AddressResponse saveAddress(AddressCreate address) {
        var partner = contactRepository.getByDigitalIdAndUuid(address.getDigitalId(), UUID.fromString(address.getUnifiedId()));
        if (partner.isEmpty()) {
            throw new EntryNotFoundException("partner", address.getDigitalId());
        }
        return super.saveAddress(address);
    }
}
