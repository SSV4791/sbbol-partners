package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.entity.partner.MergeHistoryEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerMapper;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerResponse;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;
import ru.sberbank.pprb.sbbol.partners.model.PartnersResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.MergeHistoryRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;

import java.util.UUID;

@Service
public class PartnerServiceImpl implements PartnerService {

    private final PartnerRepository partnerRepository;
    private final MergeHistoryRepository mergeHistoryRepository;
    private final LegacySbbolAdapter legacySbbolAdapter;
    private final PartnerMapper partnerMapper;

    public PartnerServiceImpl(
        PartnerRepository partnerRepository,
        MergeHistoryRepository mergeHistoryRepository,
        LegacySbbolAdapter legacySbbolAdapter,
        PartnerMapper partnerMapper) {
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
            var history = mergeHistoryRepository.getByIdAndPartnerDigitalIdAndPartnerDeletedIsFalse(uuid, digitalId);
            if (history == null) {
                return new PartnerResponse();
            }
            var response = partnerMapper.toPartner(history.getPartner());
            var partnerResponse = new PartnerResponse();
            partnerResponse.partner(response);
            return partnerResponse;
        } else {
            //TODO реализация работы с legacy
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
                response = partnerRepository.findAllByDigitalId(partnersFilter.getDigitalId(), PageRequest.of(pagination.getOffset(), pagination.getCount(), Sort.by("digitalId")));
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
            //TODO реализация работы с legacy
        }
        return null;
    }

    @Override
    @Transactional
    public PartnerResponse savePartner(Partner partner) {
        if (legacySbbolAdapter.checkMigration(partner.getDigitalId())) {
            var requestPartner = partnerMapper.toPartner(partner);
            fillEntity(requestPartner);
            var savePartner = partnerRepository.save(requestPartner);
            MergeHistoryEntity history = new MergeHistoryEntity();
            history.setId(savePartner.getId());
            history.setPartner(savePartner);
            mergeHistoryRepository.save(history);
            var response = partnerMapper.toPartner(savePartner);
            var partnerResponse = new PartnerResponse();
            partnerResponse.partner(response);
            return partnerResponse;
        } else {
            //TODO реализация работы с legacy
        }
        return null;
    }

    @Override
    @Transactional
    public PartnerResponse updatePartner(Partner partner) {
        if (legacySbbolAdapter.checkMigration(partner.getDigitalId())) {
            PartnerEntity searchPartner = partnerRepository.getByDigitalIdAndIdAndDeletedIsFalse(partner.getDigitalId(), UUID.fromString(partner.getUuid()));
            partnerMapper.updatePartner(partner, searchPartner);
            fillEntity(searchPartner);
            PartnerEntity savePartner = partnerRepository.save(searchPartner);
            var response = partnerMapper.toPartner(savePartner);
            var partnerResponse = new PartnerResponse();
            partnerResponse.partner(response);
            return partnerResponse;
        } else {
            //TODO реализация работы с legacy
        }
        return null;
    }

    @Override
    @Transactional
    public PartnerResponse deletePartner(String digitalId, String id) {
        if (legacySbbolAdapter.checkMigration(digitalId)) {
            PartnerEntity searchPartner = partnerRepository.getByDigitalIdAndIdAndDeletedIsFalse(digitalId, UUID.fromString(id));
            fillEntity(searchPartner);
            searchPartner.setDeleted(true);
            PartnerEntity savePartner = partnerRepository.save(searchPartner);
            var response = partnerMapper.toPartner(savePartner);
            var partnerResponse = new PartnerResponse();
            partnerResponse.partner(response);
            return partnerResponse;
        } else {
            //TODO реализация работы с legacy
        }
        return null;
    }

    //TODO надо перенести на маппер часть логики
    private void fillEntity(PartnerEntity partner) {
        if (!CollectionUtils.isEmpty(partner.getAccounts())) {
            for (var account : partner.getAccounts()) {
                account.setDigitalId(partner.getDigitalId());
                account.setPartner(partner);
                account.setDigitalId(partner.getDigitalId());
                if (account.getBank() != null) {
                    account.getBank().setAccount(account);
                    for (var bankAccount : account.getBank().getBankAccounts()) {
                        bankAccount.setBank(account.getBank());
                    }
                }
            }
        }
        if (!CollectionUtils.isEmpty(partner.getAddresses())) {
            for (var address : partner.getAddresses()) {
                address.setPartner(partner);
            }
        }
        if (!CollectionUtils.isEmpty(partner.getDocuments())) {
            for (var document : partner.getDocuments()) {
                document.setPartner(partner);
            }
        }
        if (!CollectionUtils.isEmpty(partner.getContacts())) {
            for (var contact : partner.getContacts()) {
                contact.setPartner(partner);
            }
        }
    }
}
