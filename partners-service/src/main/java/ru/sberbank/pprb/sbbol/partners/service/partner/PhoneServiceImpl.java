package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validation;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PhoneEntity;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PhoneMapper;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.model.PhoneCreate;
import ru.sberbank.pprb.sbbol.partners.model.PhoneResponse;
import ru.sberbank.pprb.sbbol.partners.model.PhonesFilter;
import ru.sberbank.pprb.sbbol.partners.model.PhonesResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PhoneRepository;
import ru.sberbank.pprb.sbbol.partners.validation.PhoneCreateValidationImpl;
import ru.sberbank.pprb.sbbol.partners.validation.PhoneUpdateValidationImpl;
import ru.sberbank.pprb.sbbol.partners.validation.PhonesFilterValidationImpl;

import java.util.UUID;

abstract class PhoneServiceImpl implements PhoneService {

    public static final String DOCUMENT_NAME = "phone";

    private final PhoneRepository phoneRepository;
    private final PhoneMapper phoneMapper;

    public PhoneServiceImpl(
        PhoneRepository phoneRepository,
        PhoneMapper phoneMapper
    ) {
        this.phoneRepository = phoneRepository;
        this.phoneMapper = phoneMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PhonesResponse getPhones(@Validation(type = PhonesFilterValidationImpl.class) PhonesFilter phonesFilter) {
        var response = phoneRepository.findByFilter(phonesFilter);
        var phoneResponse = new PhonesResponse();
        for (var entity : response) {
            phoneResponse.addPhonesItem(phoneMapper.toPhone(entity));
        }
        var pagination = phonesFilter.getPagination();
        phoneResponse.setPagination(
            new Pagination()
                .offset(pagination.getOffset())
                .count(pagination.getCount())
        );
        var size = response.size();
        if (pagination.getCount() < size) {
            phoneResponse.getPagination().hasNextPage(Boolean.TRUE);
            phoneResponse.getPhones().remove(size - 1);
        }
        return phoneResponse;
    }

    @Override
    @Transactional
    public PhoneResponse savePhone(@Validation(type = PhoneCreateValidationImpl.class) PhoneCreate phone) {
        var phoneEntity = phoneMapper.toPhone(phone);
        PhoneEntity savedPhone = phoneRepository.save(phoneEntity);
        var response = phoneMapper.toPhone(savedPhone);
        return new PhoneResponse().phone(response);
    }

    @Override
    @Transactional
    public PhoneResponse updatePhone(@Validation(type = PhoneUpdateValidationImpl.class) Phone phone) {
        var uuid = UUID.fromString(phone.getId());
        var foundPhone = phoneRepository.getByDigitalIdAndUuid(phone.getDigitalId(), uuid)
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, uuid));
        phoneMapper.updatePhone(phone, foundPhone);
        var savedPhone = phoneRepository.save(foundPhone);
        var response = phoneMapper.toPhone(savedPhone);
        return new PhoneResponse().phone(response);
    }

    @Override
    @Transactional
    public void deletePhone(String digitalId, String id) {
        var foundPhone = phoneRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(id));
        if (foundPhone.isEmpty()) {
            throw new EntryNotFoundException(DOCUMENT_NAME, digitalId, id);
        }
        phoneRepository.delete(foundPhone.get());
    }
}
