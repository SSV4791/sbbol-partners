package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerAddressMapper;
import ru.sberbank.pprb.sbbol.partners.model.Address;
import ru.sberbank.pprb.sbbol.partners.model.AddressResponse;
import ru.sberbank.pprb.sbbol.partners.model.AddressesFilter;
import ru.sberbank.pprb.sbbol.partners.model.AddressesResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AddressRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;

import java.util.UUID;

@Service
public class PartnerAddressServiceImpl implements PartnerAddressService {

    private final PartnerRepository partnerRepository;
    private final AddressRepository addressRepository;
    private final PartnerAddressMapper partnerAddressMapper;

    public PartnerAddressServiceImpl(
        PartnerRepository partnerRepository,
        AddressRepository addressRepository,
        PartnerAddressMapper partnerAddressMapper
    ) {
        this.partnerRepository = partnerRepository;
        this.addressRepository = addressRepository;
        this.partnerAddressMapper = partnerAddressMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse getAddress(String digitalId, String id) {
        var address = addressRepository.getByDigitalIdAndId(digitalId, UUID.fromString(id));
        if (address == null) {
            throw new EntryNotFoundException("address", digitalId, id);
        }
        var response = partnerAddressMapper.toAddress(address);
        return new AddressResponse().address(response);
    }

    @Override
    @Transactional(readOnly = true)
    public AddressesResponse getAddresses(AddressesFilter addressesFilter) {
        var response = addressRepository.findByFilter(addressesFilter);
        var addressesResponse = new AddressesResponse();
        for (var entity : response) {
            addressesResponse.addAddressesItem(partnerAddressMapper.toAddress(entity));
        }
        addressesResponse.setPagination(
            new Pagination()
                .offset(addressesFilter.getPagination().getOffset())
                .count(addressesFilter.getPagination().getCount())
        );
        return addressesResponse;
    }

    @Override
    @Transactional
    public AddressResponse saveAddress(Address address) {
        var partner = partnerRepository.getByDigitalIdAndId(address.getDigitalId(), UUID.fromString(address.getUnifiedUuid()));
        if (partner == null) {
            throw new EntryNotFoundException("partenr", address.getDigitalId(), address.getUuid());
        }
        var requestAddress = partnerAddressMapper.toAddress(address);
        var saveAddress = addressRepository.save(requestAddress);
        var response = partnerAddressMapper.toAddress(saveAddress);
        return new AddressResponse().address(response);
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(Address address) {
        var searchAddress = addressRepository.getByDigitalIdAndId(address.getDigitalId(), UUID.fromString(address.getUuid()));
        if (searchAddress == null) {
            throw new EntryNotFoundException("address", address.getDigitalId(), address.getUuid());
        }
        partnerAddressMapper.updateAddress(address, searchAddress);
        var saveContact = addressRepository.save(searchAddress);
        var response = partnerAddressMapper.toAddress(saveContact);
        return new AddressResponse().address(response);
    }

    @Override
    @Transactional
    public Error deleteAddress(String digitalId, String id) {
        var searchAddress = addressRepository.getByDigitalIdAndId(digitalId, UUID.fromString(id));
        if (searchAddress == null) {
            throw new EntryNotFoundException("address", digitalId, id);
        }
        addressRepository.delete(searchAddress);
        return new Error();
    }
}
