package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Logged;
import ru.sberbank.pprb.sbbol.partners.entity.partner.MergeHistoryEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ReplicationHistoryEntity;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.counterparty.CounterpartyMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerMapper;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerResponse;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;
import ru.sberbank.pprb.sbbol.partners.model.PartnersResponse;
import ru.sberbank.pprb.sbbol.partners.model.sbbol.Counterparty;
import ru.sberbank.pprb.sbbol.partners.model.sbbol.CounterpartyFilter;
import ru.sberbank.pprb.sbbol.partners.model.sbbol.CounterpartyView;
import ru.sberbank.pprb.sbbol.partners.model.sbbol.ListResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.MergeHistoryRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ReplicationHistoryRepository;
import ru.sberbank.pprb.sbbol.partners.service.replication.ReplicationHistoryService;
import ru.sberbank.pprb.sbbol.partners.service.utils.PartnerUtils;

import java.util.List;
import java.util.UUID;

@Logged(printRequestResponse = true)
public class PartnerServiceImpl implements PartnerService {

    public static final String DOCUMENT_NAME = "partner";

    private final PartnerRepository partnerRepository;
    private final MergeHistoryRepository mergeHistoryRepository;
    private final ReplicationHistoryRepository replicationHistoryRepository;
    private final ReplicationHistoryService replicationHistoryService;
    private final LegacySbbolAdapter legacySbbolAdapter;
    private final PartnerUtils partnerUtils;
    private final PartnerMapper partnerMapper;
    private final CounterpartyMapper counterpartyMapper;

    public PartnerServiceImpl(
        PartnerRepository partnerRepository,
        MergeHistoryRepository mergeHistoryRepository,
        ReplicationHistoryRepository replicationHistoryRepository,
        ReplicationHistoryService replicationHistoryService,
        LegacySbbolAdapter legacySbbolAdapter,
        PartnerUtils partnerUtils,
        PartnerMapper partnerMapper,
        CounterpartyMapper counterpartyMapper
    ) {
        this.partnerRepository = partnerRepository;
        this.mergeHistoryRepository = mergeHistoryRepository;
        this.replicationHistoryRepository = replicationHistoryRepository;
        this.replicationHistoryService = replicationHistoryService;
        this.legacySbbolAdapter = legacySbbolAdapter;
        this.partnerUtils = partnerUtils;
        this.partnerMapper = partnerMapper;
        this.counterpartyMapper = counterpartyMapper;
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
            Counterparty counterparty = legacySbbolAdapter.getByPprbGuid(digitalId, id);
            if (counterparty == null) {
                throw new EntryNotFoundException(DOCUMENT_NAME, digitalId, id);
            }
            return new PartnerResponse().partner(counterpartyMapper.toPartner(counterparty, digitalId));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PartnersResponse getPartners(PartnersFilter partnersFilter) {
        PartnersResponse partnersResponse = new PartnersResponse();
        if (legacySbbolAdapter.checkMigration(partnersFilter.getDigitalId())) {
            Slice<PartnerEntity> response;
            if (partnersFilter.getPagination() == null) {
                response = partnerRepository.findAllByDigitalId(partnersFilter.getDigitalId(), Sort.by("digitalId"));
            } else {
                Pagination pagination = partnersFilter.getPagination();
                PageRequest paginationRequest = PageRequest.of(pagination.getOffset(), pagination.getCount(), Sort.by("digitalId"));
                response = partnerRepository.findAllByDigitalId(partnersFilter.getDigitalId(), paginationRequest);
            }
            for (PartnerEntity entity : response) {
                partnersResponse.addPartnersItem(partnerMapper.toPartner(entity));
            }
            partnersResponse.setPagination(
                new Pagination()
                    .offset(response.getNumber())
                    .count(partnersResponse.getPartners().size())
                    .hasNextPage(response.isLast())
            );
        } else {
            List<Partner> counterparties;
            if (partnersFilter.getPagination() != null) {
                CounterpartyFilter counterpartyFilter = counterpartyMapper.toCounterpartyFilter(partnersFilter);
                ListResponse<CounterpartyView> listResponse = legacySbbolAdapter.viewRequest(partnersFilter.getDigitalId(), counterpartyFilter);
                counterparties = counterpartyMapper.toPartners(listResponse.getItems(), partnersFilter.getDigitalId());
                partnersResponse.setPagination(new Pagination()
                    .offset(listResponse.getPagination().getOffset())
                    .count(counterparties.size())
                    .hasNextPage(listResponse.getPagination().getHasNextPage())
                );
            } else {
                List<CounterpartyView> viewResponse = legacySbbolAdapter.list(partnersFilter.getDigitalId());
                counterparties = counterpartyMapper.toPartners(viewResponse, partnersFilter.getDigitalId());
                partnersResponse.setPagination(
                    new Pagination()
                        .offset(0)
                        .count(counterparties.size())
                        .hasNextPage(false)
                );
            }
            partnersResponse.setPartners(counterparties);
        }
        return partnersResponse;
    }

    @Override
    @Transactional
    public PartnerResponse savePartner(Partner partner) {
        var partnerEntity = partnerMapper.toPartner(partner);
        var savePartner = partnerRepository.save(partnerEntity);
        MergeHistoryEntity history = new MergeHistoryEntity();
        history.setPartnerUuid(savePartner.getUuid());
        history.setMainUuid(savePartner.getUuid());
        mergeHistoryRepository.save(history);
        var response = partnerMapper.toPartner(savePartner);
        var partnerResponse = new PartnerResponse();
        partnerResponse.partner(response);
        var replicationHistory = new ReplicationHistoryEntity();
        replicationHistory.setPartnerUuid(savePartner.getUuid());
        replicationHistoryRepository.save(replicationHistory);
        return partnerResponse;
    }

    @Override
    @Transactional
    public PartnerResponse updatePartner(Partner partner) {
        if (legacySbbolAdapter.checkMigration(partner.getDigitalId())) {
            PartnerResponse partnerResponse = partnerUtils.updatePartnerByPartner(partner);
            replicationHistoryService.updateCounterparty(partner);
            return partnerResponse;
        } else {
            PartnerResponse partnerResponse = partnerUtils.updateCounterpartyByPartner(partner);
            replicationHistoryService.updatePartner(partner);
            return partnerResponse;
        }
    }

    @Override
    @Transactional
    public void deletePartner(String digitalId, String id) {
        if (legacySbbolAdapter.checkMigration(digitalId)) {
            partnerUtils.deletePartnerByDigitalIdAndId(digitalId, id);
            replicationHistoryService.deleteCounterparty(digitalId, id);
        } else {
            legacySbbolAdapter.delete(digitalId, id);
            replicationHistoryService.deletePartner(digitalId, id);
        }
    }
}
