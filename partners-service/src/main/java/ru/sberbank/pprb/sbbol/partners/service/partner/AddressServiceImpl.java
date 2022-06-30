package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AddressMapper;
import ru.sberbank.pprb.sbbol.partners.model.Address;
import ru.sberbank.pprb.sbbol.partners.model.AddressCreate;
import ru.sberbank.pprb.sbbol.partners.model.AddressResponse;
import ru.sberbank.pprb.sbbol.partners.model.AddressesFilter;
import ru.sberbank.pprb.sbbol.partners.model.AddressesResponse;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AddressRepository;

import java.util.UUID;

abstract class AddressServiceImpl implements AddressService {

    public static final String DOCUMENT_NAME = "contact_address";

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    public AddressServiceImpl(
        AddressRepository addressRepository,
        AddressMapper addressMapper
    ) {
        this.addressRepository = addressRepository;
        this.addressMapper = addressMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse getAddress(String digitalId, String id) {
        var address = addressRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(id))
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, id));
        var response = addressMapper.toAddress(address);
        return new AddressResponse().address(response);
    }

    @Override
    @Transactional(readOnly = true)
    public AddressesResponse getAddresses(AddressesFilter addressesFilter) {
        var response = addressRepository.findByFilter(addressesFilter);
        var addressesResponse = new AddressesResponse();
        for (var entity : response) {
            addressesResponse.addAddressesItem(addressMapper.toAddress(entity));
        }
        var pagination = addressesFilter.getPagination();
        addressesResponse.setPagination(
            new Pagination()
                .offset(pagination.getOffset())
                .count(pagination.getCount())
        );
        var size = response.size();
        if (pagination.getCount() < size) {
            addressesResponse.getPagination().hasNextPage(Boolean.TRUE);
            addressesResponse.getAddresses().remove(size - 1);
        }
        return addressesResponse;
    }

    @Override
    @Transactional
    public AddressResponse saveAddress(AddressCreate address) {
        var requestAddress = addressMapper.toAddress(address);
        var saveAddress = addressRepository.save(requestAddress);
        var response = addressMapper.toAddress(saveAddress);
        return new AddressResponse().address(response);
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(Address address) {
        var foundAddress = addressRepository.getByDigitalIdAndUuid(address.getDigitalId(), UUID.fromString(address.getId()))
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, address.getDigitalId(), address.getId()));
        addressMapper.updateAddress(address, foundAddress);
        var saveContact = addressRepository.save(foundAddress);
        var response = addressMapper.toAddress(saveContact);
        return new AddressResponse().address(response);
    }

    @Override
    @Transactional
    public void deleteAddress(String digitalId, String id) {
        var foundAddress = addressRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(id))
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, id));
        addressRepository.delete(foundAddress);
    }
}
