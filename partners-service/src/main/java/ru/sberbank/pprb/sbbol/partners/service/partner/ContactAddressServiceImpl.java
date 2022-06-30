package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validation;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AddressMapper;
import ru.sberbank.pprb.sbbol.partners.model.AddressCreate;
import ru.sberbank.pprb.sbbol.partners.model.AddressResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AddressRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ContactRepository;
import ru.sberbank.pprb.sbbol.partners.validation.AddressCreateValidationImpl;

import java.util.UUID;

@Loggable
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
    public AddressResponse saveAddress(@Validation(type = AddressCreateValidationImpl.class) AddressCreate address) {
        var contact = contactRepository.getByDigitalIdAndUuid(address.getDigitalId(), UUID.fromString(address.getUnifiedId()));
        if (contact.isEmpty()) {
            throw new EntryNotFoundException("contact", address.getDigitalId());
        }
        return super.saveAddress(address);
    }
}
