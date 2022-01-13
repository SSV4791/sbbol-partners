package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Logged;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AddressMapper;
import ru.sberbank.pprb.sbbol.partners.model.Address;
import ru.sberbank.pprb.sbbol.partners.model.AddressResponse;
import ru.sberbank.pprb.sbbol.partners.model.AddressesFilter;
import ru.sberbank.pprb.sbbol.partners.model.AddressesResponse;
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
    @Transactional(readOnly = true)
    public AddressResponse getAddress(String digitalId, String id) {
        return super.getAddress(digitalId, id);
    }

    @Override
    @Transactional(readOnly = true)
    public AddressesResponse getAddresses(AddressesFilter addressesFilter) {
        return super.getAddresses(addressesFilter);
    }

    @Override
    @Transactional
    public AddressResponse saveAddress(Address address) {
        var partner = contactRepository.getByDigitalIdAndUuid(address.getDigitalId(), UUID.fromString(address.getUnifiedId()));
        if (partner == null) {
            throw new EntryNotFoundException("partner", address.getDigitalId(), address.getId());
        }
        return super.saveAddress(address);
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(Address address) {
        return super.updateAddress(address);
    }

    @Override
    @Transactional
    public void deleteAddress(String digitalId, String id) {
        super.deleteAddress(digitalId, id);
    }
}
