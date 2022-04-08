package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Logged;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.exception.PartnerMigrationException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerMapper;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;
import ru.sberbank.pprb.sbbol.partners.model.PartnerResponse;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;
import ru.sberbank.pprb.sbbol.partners.model.PartnersResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ContactRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.EmailRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PhoneRepository;
import ru.sberbank.pprb.sbbol.partners.service.replication.ReplicationService;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Logged(printRequestResponse = true)
public class PartnerServiceImpl implements PartnerService {

    public static final String DOCUMENT_NAME = "partner";

    private final AccountRepository accountRepository;
    private final DocumentRepository documentRepository;
    private final ContactRepository contactRepository;
    private final PhoneRepository phoneRepository;
    private final EmailRepository emailRepository;
    private final PartnerRepository partnerRepository;
    private final LegacySbbolAdapter legacySbbolAdapter;
    private final ReplicationService replicationService;
    private final PartnerMapper partnerMapper;

    public PartnerServiceImpl(
        AccountRepository accountRepository,
        DocumentRepository documentRepository,
        ContactRepository contactRepository,
        PhoneRepository phoneRepository,
        EmailRepository emailRepository,
        PartnerRepository partnerRepository,
        LegacySbbolAdapter legacySbbolAdapter,
        ReplicationService replicationService,
        PartnerMapper partnerMapper
    ) {
        this.accountRepository = accountRepository;
        this.documentRepository = documentRepository;
        this.contactRepository = contactRepository;
        this.phoneRepository = phoneRepository;
        this.emailRepository = emailRepository;
        this.partnerRepository = partnerRepository;
        this.legacySbbolAdapter = legacySbbolAdapter;
        this.replicationService = replicationService;
        this.partnerMapper = partnerMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PartnerResponse getPartner(String digitalId, String id) {
        if (legacySbbolAdapter.checkNotMigration(digitalId)) {
            throw new PartnerMigrationException();
        }
        PartnerEntity partner = partnerRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(id))
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, id));
        var response = partnerMapper.toPartner(partner);
        response.setGku(getGku(response.getDigitalId(), response.getInn()));
        return new PartnerResponse().partner(response);
    }

    @Override
    @Transactional(readOnly = true)
    public PartnersResponse getPartners(PartnersFilter partnersFilter) {
        if (legacySbbolAdapter.checkNotMigration(partnersFilter.getDigitalId())) {
            throw new PartnerMigrationException();
        }
        PartnersResponse partnersResponse = new PartnersResponse();
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
    public PartnerResponse savePartner(PartnerCreate partner) {
        if (legacySbbolAdapter.checkNotMigration(partner.getDigitalId())) {
            throw new PartnerMigrationException();
        }
        var partnerEntity = partnerMapper.toPartner(partner);
        var savePartner = partnerRepository.save(partnerEntity);
        var response = partnerMapper.toPartner(savePartner);
        response.setGku(getGku(response.getDigitalId(), response.getInn()));
        return new PartnerResponse().partner(response);
    }

    @Override
    @Transactional
    public PartnerResponse updatePartner(Partner partner) {
        if (legacySbbolAdapter.checkNotMigration(partner.getDigitalId())) {
            throw new PartnerMigrationException();
        }
        PartnerEntity foundPartner = partnerRepository.getByDigitalIdAndUuid(partner.getDigitalId(), UUID.fromString(partner.getId()))
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, partner.getDigitalId(), partner.getId()));
        partnerMapper.updatePartner(partner, foundPartner);
        PartnerEntity savePartner = partnerRepository.save(foundPartner);
        var response = partnerMapper.toPartner(savePartner);
        response.setGku(getGku(response.getDigitalId(), response.getInn()));
        return new PartnerResponse().partner(response);
    }

    @Override
    @Transactional
    public void deletePartner(String digitalId, String id) {
        if (legacySbbolAdapter.checkNotMigration(digitalId)) {
            throw new PartnerMigrationException();
        }
        var partnerUuid = UUID.fromString(id);
        PartnerEntity foundPartner = partnerRepository.getByDigitalIdAndUuid(digitalId, partnerUuid)
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, id));
        partnerRepository.delete(foundPartner);
        emailRepository.deleteAll(emailRepository.findByDigitalIdAndUnifiedUuid(digitalId, partnerUuid));
        phoneRepository.deleteAll(phoneRepository.findByDigitalIdAndUnifiedUuid(digitalId, partnerUuid));
        contactRepository.deleteAll(contactRepository.findByDigitalIdAndPartnerUuid(digitalId, partnerUuid));
        documentRepository.deleteAll(documentRepository.findByDigitalIdAndUnifiedUuid(digitalId, partnerUuid));
        var accounts = accountRepository.findByDigitalIdAndPartnerUuid(digitalId, partnerUuid);
        if (!CollectionUtils.isEmpty(accounts)) {
            accountRepository.deleteAll(accounts);
            replicationService.deleteCounterparties(accounts);
        }
    }

    /**
     * Получение признака ЖКУ
     *
     * @param digitalId Идентификатор личного кабинета
     * @param inn       ИНН
     * @return признак принадлежит инн ЖКУ true - да, false - нет
     */
    private Boolean getGku(String digitalId, String inn) {
        var housingInn = legacySbbolAdapter.getHousingInn(digitalId, Set.of(inn));
        if (!CollectionUtils.isEmpty(housingInn)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
