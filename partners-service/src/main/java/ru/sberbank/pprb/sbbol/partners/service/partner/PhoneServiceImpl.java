package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.entity.partner.PhoneEntity;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PhoneMapper;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.model.PhoneResponse;
import ru.sberbank.pprb.sbbol.partners.model.PhonesFilter;
import ru.sberbank.pprb.sbbol.partners.model.PhonesResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PhoneRepository;

import java.util.UUID;

abstract class PhoneServiceImpl implements PhoneService {

    public static final String DOCUMENT_NAME = "phone";

    private final PhoneRepository phoneRepository;
    private final PhoneMapper phoneMapper;

    public PhoneServiceImpl(PhoneRepository phoneRepository, PhoneMapper phoneMapper) {
        this.phoneRepository = phoneRepository;
        this.phoneMapper = phoneMapper;
    }

    @Override
    public PhonesResponse getPhones(PhonesFilter phonesFilter) {
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
    public PhoneResponse savePhone(Phone phone) {
        var phoneEntity = phoneMapper.toPhone(phone);
        PhoneEntity savedPhone = phoneRepository.save(phoneEntity);
        var response = phoneMapper.toPhone(savedPhone);
        return new PhoneResponse().phone(response);
    }

    @Override
    public PhoneResponse updatePhone(Phone phone) {
        var uuid = UUID.fromString(phone.getId());
        var foundPhone = phoneRepository.getByUuid(uuid);
        if (foundPhone == null) {
            throw new EntryNotFoundException(DOCUMENT_NAME, uuid);
        }
        phoneMapper.updatePhone(phone, foundPhone);
        var savedPhone = phoneRepository.save(foundPhone);
        var response = phoneMapper.toPhone(savedPhone);
        return new PhoneResponse().phone(response);
    }

    @Override
    public void deletePhone(String digitalId, String id) {
        var foundPhone = phoneRepository.getByUuid(UUID.fromString(id));
        if (foundPhone == null) {
            throw new EntryNotFoundException(DOCUMENT_NAME, digitalId, id);
        }
        phoneRepository.delete(foundPhone);
    }
}
