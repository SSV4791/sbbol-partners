package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.exception.OptimisticLockException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AddressMapper;
import ru.sberbank.pprb.sbbol.partners.model.Address;
import ru.sberbank.pprb.sbbol.partners.model.AddressCreate;
import ru.sberbank.pprb.sbbol.partners.model.AddressesFilter;
import ru.sberbank.pprb.sbbol.partners.model.AddressesResponse;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AddressRepository;

import java.util.List;
import java.util.Objects;
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
    public Address getAddress(String digitalId, String id) {
        var address = addressRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(id))
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, id));
        return addressMapper.toAddress(address);
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
    public Address saveAddress(AddressCreate address) {
        var requestAddress = addressMapper.toAddress(address);
        var saveAddress = addressRepository.save(requestAddress);
        return addressMapper.toAddress(saveAddress);
    }

    @Override
    @Transactional
    public Address updateAddress(Address address) {
        var foundAddress = addressRepository.getByDigitalIdAndUuid(address.getDigitalId(), UUID.fromString(address.getId()))
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, address.getDigitalId(), address.getId()));
        if (!Objects.equals(address.getVersion(), foundAddress.getVersion())) {
            throw new OptimisticLockException(foundAddress.getVersion(), address.getVersion());
        }
        addressMapper.updateAddress(address, foundAddress);
        var saveContact = addressRepository.save(foundAddress);
        return addressMapper.toAddress(saveContact);
    }

    @Override
    @Transactional
    public void deleteAddresses(String digitalId, List<String> ids) {
        for (String id : ids) {
            var uuid = addressMapper.mapUuid(id);
            var foundAddress = addressRepository.getByDigitalIdAndUuid(digitalId, uuid)
                .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, id));
            addressRepository.delete(foundAddress);
        }
    }
}
