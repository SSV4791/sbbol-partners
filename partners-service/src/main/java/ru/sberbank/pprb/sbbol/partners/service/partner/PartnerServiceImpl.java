package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerMapper;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;
import ru.sberbank.pprb.sbbol.partners.model.PartnersResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ContactRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.EmailRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.GkuInnDictionaryRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PhoneRepository;
import ru.sberbank.pprb.sbbol.partners.service.replication.ReplicationService;

import java.util.UUID;

@Loggable
public class PartnerServiceImpl implements PartnerService {

    public static final String DOCUMENT_NAME = "partner";

    private final AccountRepository accountRepository;
    private final DocumentRepository documentRepository;
    private final ContactRepository contactRepository;
    private final PhoneRepository phoneRepository;
    private final EmailRepository emailRepository;
    private final PartnerRepository partnerRepository;
    private final GkuInnDictionaryRepository gkuInnDictionaryRepository;
    private final ReplicationService replicationService;
    private final PartnerMapper partnerMapper;

    public PartnerServiceImpl(
        AccountRepository accountRepository,
        DocumentRepository documentRepository,
        ContactRepository contactRepository,
        PhoneRepository phoneRepository,
        EmailRepository emailRepository,
        PartnerRepository partnerRepository,
        GkuInnDictionaryRepository gkuInnDictionaryRepository,
        ReplicationService replicationService,
        PartnerMapper partnerMapper
    ) {
        this.accountRepository = accountRepository;
        this.documentRepository = documentRepository;
        this.contactRepository = contactRepository;
        this.phoneRepository = phoneRepository;
        this.emailRepository = emailRepository;
        this.partnerRepository = partnerRepository;
        this.gkuInnDictionaryRepository = gkuInnDictionaryRepository;
        this.replicationService = replicationService;
        this.partnerMapper = partnerMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Partner getPartner(String digitalId, String id) {
        PartnerEntity partner = partnerRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(id))
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, id));
        var response = partnerMapper.toPartner(partner);
        response.setGku(getGku(response.getInn()));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PartnersResponse getPartners(PartnersFilter partnersFilter) {
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
        for (Partner partner : partners) {
            partner.setGku(getGku(partner.getInn()));
        }
        return partnersResponse.partners(partners);
    }

    @Override
    @Transactional
    public Partner savePartner(PartnerCreate partner) {
        var partnerEntity = partnerMapper.toPartner(partner);
        var savePartner = partnerRepository.save(partnerEntity);
        var response = partnerMapper.toPartner(savePartner);
        response.setGku(getGku(response.getInn()));
        return response;
    }

    @Override
    @Transactional
    public Partner updatePartner(Partner partner) {
        PartnerEntity foundPartner = partnerRepository.getByDigitalIdAndUuid(partner.getDigitalId(), UUID.fromString(partner.getId()))
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, partner.getDigitalId(), partner.getId()));
        partnerMapper.updatePartner(partner, foundPartner);
        PartnerEntity savePartner = partnerRepository.save(foundPartner);
        var response = partnerMapper.toPartner(savePartner);
        response.setGku(getGku(response.getInn()));
        return response;
    }

    @Override
    @Transactional
    public void deletePartner(String digitalId, String id) {
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
     * @param inn ИНН
     * @return признак принадлежит инн ЖКУ true - да, false - нет
     */
    private boolean getGku(String inn) {
        if (inn == null) {
            return false;
        }
        var housingInn = gkuInnDictionaryRepository.getByInn(inn);
        return housingInn != null;
    }
}
