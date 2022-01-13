package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Logged;
import ru.sberbank.pprb.sbbol.partners.entity.partner.MergeHistoryEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEmailEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerPhoneEntity;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerMapper;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerResponse;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;
import ru.sberbank.pprb.sbbol.partners.model.PartnersResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.MergeHistoryRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;

import java.util.UUID;

@Logged(printRequestResponse = true)
public class PartnerServiceImpl implements PartnerService {

    public static final String DOCUMENT_NAME = "partner";

    private final PartnerRepository partnerRepository;
    private final MergeHistoryRepository mergeHistoryRepository;
    private final LegacySbbolAdapter legacySbbolAdapter;
    private final PartnerMapper partnerMapper;

    public PartnerServiceImpl(
        PartnerRepository partnerRepository,
        MergeHistoryRepository mergeHistoryRepository,
        LegacySbbolAdapter legacySbbolAdapter,
        PartnerMapper partnerMapper
    ) {
        this.partnerRepository = partnerRepository;
        this.mergeHistoryRepository = mergeHistoryRepository;
        this.legacySbbolAdapter = legacySbbolAdapter;
        this.partnerMapper = partnerMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PartnerResponse getPartner(String digitalId, String id) {
        if (legacySbbolAdapter.checkMigration(digitalId)) {
            UUID uuid = UUID.fromString(id);
            var history = mergeHistoryRepository.getByPartnerUuid(uuid);
            if (history == null) {
                throw new EntryNotFoundException(DOCUMENT_NAME, digitalId, id);
            }
            PartnerEntity partner = partnerRepository.getByDigitalIdAndUuid(digitalId, history.getMainUuid());
            if (partner == null) {
                throw new EntryNotFoundException(DOCUMENT_NAME, digitalId, id);
            }
            var response = partnerMapper.toPartner(partner);
            var partnerResponse = new PartnerResponse();
            partnerResponse.partner(response);
            return partnerResponse;
        } else {
            //TODO DCBBRAIN-1642 реализация работы с legacy
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public PartnersResponse getPartners(PartnersFilter partnersFilter) {
        if (legacySbbolAdapter.checkMigration(partnersFilter.getDigitalId())) {
            Slice<PartnerEntity> response;
            if (partnersFilter.getPagination() == null) {
                response = partnerRepository.findAllByDigitalId(partnersFilter.getDigitalId(), Sort.by("digitalId"));
            } else {
                Pagination pagination = partnersFilter.getPagination();
                PageRequest paginationRequest = PageRequest.of(pagination.getOffset(), pagination.getCount(), Sort.by("digitalId"));
                response = partnerRepository.findAllByDigitalId(partnersFilter.getDigitalId(), paginationRequest);
            }
            PartnersResponse partnersResponse = new PartnersResponse();
            for (PartnerEntity entity : response) {
                partnersResponse.addPartnersItem(partnerMapper.toPartner(entity));
            }
            partnersResponse.setPagination(
                new Pagination()
                    .offset(response.getNumber())
                    .count(response.getSize())
                    .hasNextPage(response.isLast())
            );
            return partnersResponse;
        } else {
            //TODO DCBBRAIN-1642 реализация работы с legacy
        }
        return null;
    }

    @Override
    @Transactional
    public PartnerResponse savePartner(Partner partner) {
        if (legacySbbolAdapter.checkMigration(partner.getDigitalId())) {
            var partnerEntity = partnerMapper.toPartner(partner);
            for (PartnerEmailEntity email : partnerEntity.getEmails()) {
                email.setPartner(partnerEntity);
            }
            for (PartnerPhoneEntity phone : partnerEntity.getPhones()) {
                phone.setPartner(partnerEntity);
            }
            var savePartner = partnerRepository.save(partnerEntity);
            MergeHistoryEntity history = new MergeHistoryEntity();
            history.setPartnerUuid(savePartner.getUuid());
            history.setMainUuid(savePartner.getUuid());
            mergeHistoryRepository.save(history);
            var response = partnerMapper.toPartner(savePartner);
            var partnerResponse = new PartnerResponse();
            partnerResponse.partner(response);
            return partnerResponse;
        } else {
            //TODO DCBBRAIN-1642 реализация работы с legacy
        }
        return null;
    }

    @Override
    @Transactional
    public PartnerResponse updatePartner(Partner partner) {
        if (legacySbbolAdapter.checkMigration(partner.getDigitalId())) {
            MergeHistoryEntity history = mergeHistoryRepository.getByPartnerUuid(UUID.fromString(partner.getId()));
            if (history == null) {
                throw new EntryNotFoundException(DOCUMENT_NAME, partner.getDigitalId(), partner.getId());
            }
            PartnerEntity foundPartner = partnerRepository.getByDigitalIdAndUuid(partner.getDigitalId(), history.getMainUuid());
            if (foundPartner == null) {
                throw new EntryNotFoundException(DOCUMENT_NAME, partner.getDigitalId(), partner.getId());
            }
            partnerMapper.updatePartner(partner, foundPartner);
            PartnerEntity savePartner = partnerRepository.save(foundPartner);
            var response = partnerMapper.toPartner(savePartner);
            var partnerResponse = new PartnerResponse();
            partnerResponse.partner(response);
            return partnerResponse;
        } else {
            //TODO DCBBRAIN-1642 реализация работы с legacy
        }
        return null;
    }

    @Override
    @Transactional
    public void deletePartner(String digitalId, String id) {
        if (legacySbbolAdapter.checkMigration(digitalId)) {
            MergeHistoryEntity history = mergeHistoryRepository.getByPartnerUuid(UUID.fromString(id));
            if (history == null) {
                throw new EntryNotFoundException(DOCUMENT_NAME, digitalId, id);
            }
            PartnerEntity foundPartner = partnerRepository.getByDigitalIdAndUuid(digitalId, history.getMainUuid());
            if (foundPartner == null) {
                throw new EntryNotFoundException(DOCUMENT_NAME, digitalId, id);
            }
            partnerRepository.deleteById(foundPartner.getUuid());
            mergeHistoryRepository.deleteByMainUuid(foundPartner.getUuid());
        } else {
            //TODO DCBBRAIN-1642 реализация работы с legacy
        }
    }
}
