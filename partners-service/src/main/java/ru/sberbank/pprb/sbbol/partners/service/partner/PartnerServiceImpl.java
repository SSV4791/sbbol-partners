package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
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
import java.util.stream.Collectors;

@Logged(printRequestResponse = true)
public class PartnerServiceImpl implements PartnerService {

    public static final String DOCUMENT_NAME = "partner";

    private final PartnerRepository partnerRepository;
    private final ReplicationHistoryRepository replicationHistoryRepository;
    private final ReplicationHistoryService replicationHistoryService;
    private final LegacySbbolAdapter legacySbbolAdapter;
    private final PartnerUtils partnerUtils;
    private final PartnerMapper partnerMapper;
    private final CounterpartyMapper counterpartyMapper;

    public PartnerServiceImpl(
        PartnerRepository partnerRepository,
        ReplicationHistoryRepository replicationHistoryRepository,
        ReplicationHistoryService replicationHistoryService,
        LegacySbbolAdapter legacySbbolAdapter,
        PartnerUtils partnerUtils,
        PartnerMapper partnerMapper,
        CounterpartyMapper counterpartyMapper
    ) {
        this.partnerRepository = partnerRepository;
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
        Partner response;
        if (legacySbbolAdapter.checkMigration(digitalId)) {
            UUID uuid = UUID.fromString(id);
            PartnerEntity partner = partnerRepository.getByDigitalIdAndUuid(digitalId, uuid);
            if (partner == null) {
                throw new EntryNotFoundException(DOCUMENT_NAME, digitalId, id);
            }
            response = partnerMapper.toPartner(partner);
        } else {
            Counterparty counterparty = legacySbbolAdapter.getByPprbGuid(digitalId, id);
            if (counterparty == null) {
                throw new EntryNotFoundException(DOCUMENT_NAME, digitalId, id);
            }
            response = counterpartyMapper.toPartner(counterparty, digitalId);
        }
        response.setGku(partnerUtils.getGku(response.getDigitalId(), response.getInn()));
        return new PartnerResponse().partner(response);
    }

    @Override
    @Transactional(readOnly = true)
    public PartnersResponse getPartners(PartnersFilter partnersFilter) {
        PartnersResponse partnersResponse = new PartnersResponse();
        if (legacySbbolAdapter.checkMigration(partnersFilter.getDigitalId())) {
            var response = partnerRepository.findByFilter(partnersFilter);
            for (PartnerEntity entity : response) {
                partnersResponse.addPartnersItem(partnerMapper.toPartner(entity));
            }
            var pagination = partnersFilter.getPagination();
            partnersResponse.setPagination(
                new Pagination()
                    .offset(pagination.getOffset())
                    .count(pagination.getCount())
            );
            var size = response.size();
            if (pagination.getCount() < size) {
                partnersResponse.getPagination().hasNextPage(Boolean.TRUE);
                partnersResponse.getPartners().remove(size - 1);
            }
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
        var partners = partnersResponse.getPartners();
        if (CollectionUtils.isEmpty(partners)) {
            return partnersResponse;
        }
        var inns = partners.stream().map(Partner::getInn).collect(Collectors.toSet());
        var housingInn = legacySbbolAdapter.getHousingInn(partnersFilter.getDigitalId(), inns);
        for (Partner partner : partners) {
            if (housingInn.contains(partner.getInn())) {
                partner.setGku(Boolean.TRUE);
            }
        }
        return partnersResponse.partners(partners);
    }

    @Override
    @Transactional
    public PartnerResponse savePartner(Partner partner) {
        var partnerEntity = partnerMapper.toPartner(partner);
        var savePartner = partnerRepository.save(partnerEntity);
        var response = partnerMapper.toPartner(savePartner);
        response.setGku(partnerUtils.getGku(response.getDigitalId(), response.getInn()));
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
        Partner response;
        if (legacySbbolAdapter.checkMigration(partner.getDigitalId())) {
            response = partnerUtils.updatePartnerByPartner(partner);
            replicationHistoryService.updateCounterparty(partner);
        } else {
            response = partnerUtils.updateCounterpartyByPartner(partner);
            replicationHistoryService.updatePartner(partner);
        }
        response.setGku(partnerUtils.getGku(response.getDigitalId(), response.getInn()));
        return new PartnerResponse().partner(response);
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
