package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PhoneEntity;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.exception.PartnerMigrationException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PhoneMapper;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.model.PhoneCreate;
import ru.sberbank.pprb.sbbol.partners.model.PhoneResponse;
import ru.sberbank.pprb.sbbol.partners.model.PhonesFilter;
import ru.sberbank.pprb.sbbol.partners.model.PhonesResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PhoneRepository;

import java.util.UUID;

abstract class PhoneServiceImpl implements PhoneService {

    public static final String DOCUMENT_NAME = "phone";

    private final PhoneRepository phoneRepository;
    private final PhoneMapper phoneMapper;
    private final LegacySbbolAdapter legacySbbolAdapter;

    public PhoneServiceImpl(
        PhoneRepository phoneRepository,
        PhoneMapper phoneMapper,
        LegacySbbolAdapter legacySbbolAdapter
    ) {
        this.phoneRepository = phoneRepository;
        this.phoneMapper = phoneMapper;
        this.legacySbbolAdapter = legacySbbolAdapter;
    }

    @Override
    @Transactional(readOnly = true)
    public PhonesResponse getPhones(PhonesFilter phonesFilter) {
        if (legacySbbolAdapter.checkNotMigration(phonesFilter.getDigitalId())) {
            throw new PartnerMigrationException();
        }
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
    public PhoneResponse savePhone(PhoneCreate phone) {
        if (legacySbbolAdapter.checkNotMigration(phone.getDigitalId())) {
            throw new PartnerMigrationException();
        }
        var phoneEntity = phoneMapper.toPhone(phone);
        PhoneEntity savedPhone = phoneRepository.save(phoneEntity);
        var response = phoneMapper.toPhone(savedPhone);
        return new PhoneResponse().phone(response);
    }

    @Override
    @Transactional
    public PhoneResponse updatePhone(Phone phone) {
        if (legacySbbolAdapter.checkNotMigration(phone.getDigitalId())) {
            throw new PartnerMigrationException();
        }
        var uuid = UUID.fromString(phone.getId());
        var foundPhone = phoneRepository.getByDigitalIdAndUuid(phone.getDigitalId(), uuid)
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, uuid));
        if (phone.getVersion() <= foundPhone.getVersion()) {
            throw new OptimisticLockingFailureException("Версия документа в базе данных " + foundPhone.getVersion() +
                " больше или равна версии документа в запросе version=" + phone.getVersion());
        }
        phoneMapper.updatePhone(phone, foundPhone);
        var savedPhone = phoneRepository.save(foundPhone);
        var response = phoneMapper.toPhone(savedPhone);
        return new PhoneResponse().phone(response);
    }

    @Override
    @Transactional
    public void deletePhone(String digitalId, String id) {
        if (legacySbbolAdapter.checkNotMigration(digitalId)) {
            throw new PartnerMigrationException();
        }
        var foundPhone = phoneRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(id));
        if (foundPhone.isEmpty()) {
            throw new EntryNotFoundException(DOCUMENT_NAME, digitalId, id);
        }
        phoneRepository.delete(foundPhone.get());
    }
}
