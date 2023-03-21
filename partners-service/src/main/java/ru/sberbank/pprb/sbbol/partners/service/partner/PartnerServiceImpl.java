package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.aspect.audit.Audit;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.PartnerType;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.exception.OptimisticLockException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AddressMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.ContactMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.DocumentMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerMapper;
import ru.sberbank.pprb.sbbol.partners.model.FraudMetaData;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModelResponse;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;
import ru.sberbank.pprb.sbbol.partners.model.PartnersResponse;
import ru.sberbank.pprb.sbbol.partners.model.fraud.FraudEventType;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AddressRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ContactRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.GkuInnDictionaryRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;
import ru.sberbank.pprb.sbbol.partners.service.fraud.FraudServiceManager;
import ru.sberbank.pprb.sbbol.partners.service.replication.ReplicationService;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static ru.sberbank.pprb.sbbol.partners.audit.model.EventType.PARTNER_CREATE;
import static ru.sberbank.pprb.sbbol.partners.audit.model.EventType.PARTNER_DELETE;
import static ru.sberbank.pprb.sbbol.partners.audit.model.EventType.PARTNER_FULL_MODEL_CREATE;
import static ru.sberbank.pprb.sbbol.partners.audit.model.EventType.PARTNER_UPDATE;

@Loggable
public class PartnerServiceImpl implements PartnerService {

    private static final String DOCUMENT_NAME = "partner";

    private final AccountRepository accountRepository;
    private final DocumentRepository documentRepository;
    private final ContactRepository contactRepository;
    private final AddressRepository addressRepository;
    private final PartnerRepository partnerRepository;
    private final GkuInnDictionaryRepository gkuInnDictionaryRepository;
    private final BudgetMaskService budgetMaskService;
    private final FraudServiceManager fraudServiceManager;
    private final AccountMapper accountMapper;
    private final DocumentMapper documentMapper;
    private final AddressMapper addressMapper;
    private final ContactMapper contactMapper;
    private final PartnerMapper partnerMapper;
    private final ReplicationService replicationService;

    public PartnerServiceImpl(
        AccountRepository accountRepository,
        DocumentRepository documentRepository,
        ContactRepository contactRepository,
        AddressRepository addressRepository,
        PartnerRepository partnerRepository,
        GkuInnDictionaryRepository gkuInnDictionaryRepository,
        BudgetMaskService budgetMaskService,
        FraudServiceManager fraudServiceManager,
        AccountMapper accountMapper,
        DocumentMapper documentMapper,
        AddressMapper addressMapper,
        ContactMapper contactMapper,
        PartnerMapper partnerMapper,
        ReplicationService replicationService
    ) {
        this.accountRepository = accountRepository;
        this.documentRepository = documentRepository;
        this.contactRepository = contactRepository;
        this.addressRepository = addressRepository;
        this.partnerRepository = partnerRepository;
        this.gkuInnDictionaryRepository = gkuInnDictionaryRepository;
        this.budgetMaskService = budgetMaskService;
        this.fraudServiceManager = fraudServiceManager;
        this.accountMapper = accountMapper;
        this.documentMapper = documentMapper;
        this.addressMapper = addressMapper;
        this.contactMapper = contactMapper;
        this.partnerMapper = partnerMapper;
        this.replicationService = replicationService;
    }

    @Override
    @Transactional(readOnly = true)
    public Partner getPartner(String digitalId, String id) {
        PartnerEntity partner = partnerRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(id))
            .filter(partnerEntity -> PartnerType.PARTNER == partnerEntity.getType())
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
    @Audit(eventType = PARTNER_FULL_MODEL_CREATE)
    public PartnerCreateFullModelResponse savePartner(PartnerCreateFullModel partner) {
        var partnerEntity = partnerMapper.toPartner(partner);
        var savedPartner = partnerRepository.save(partnerEntity);
        var digitalId = partner.getDigitalId();
        var partnerUuid = savedPartner.getUuid();
        var accounts = accountMapper.toAccounts(partner.getAccounts(), digitalId, partnerUuid).stream()
            .map(accountRepository::save)
            .map(value -> accountMapper.toAccount(value, budgetMaskService))
            .collect(Collectors.toList());
        var addresses = addressMapper.toAddress(partner.getAddress(), digitalId, partnerUuid).stream()
            .map(addressRepository::save)
            .map(addressMapper::toAddress)
            .collect(Collectors.toList());
        var documents = documentMapper.toDocuments(partner.getDocuments(), digitalId, partnerUuid).stream()
            .map(documentRepository::save)
            .map(documentMapper::toDocument)
            .collect(Collectors.toList());
        var contacts = contactMapper.toContacts(partner.getContacts(), digitalId, partnerUuid).stream()
            .map(contactRepository::save)
            .map(contactMapper::toContact)
            .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(accounts)) {
            replicationService.createCounterparty(accounts);
        }
        return partnerMapper.toPartnerMullResponse(savedPartner)
            .gku(getGku(savedPartner.getInn()))
            .accounts(accounts)
            .address(addresses)
            .documents(documents)
            .contacts(contacts);
    }

    @Override
    @Transactional
    @Audit(eventType = PARTNER_CREATE)
    public Partner savePartner(PartnerCreate partner) {
        var partnerEntity = partnerMapper.toPartner(partner);
        var savePartner = partnerRepository.save(partnerEntity);
        var response = partnerMapper.toPartner(savePartner);
        response.setGku(getGku(response.getInn()));
        return response;
    }

    @Override
    @Transactional
    @Audit(eventType = PARTNER_UPDATE)
    public Partner updatePartner(Partner partner) {
        var digitalId = partner.getDigitalId();
        var partnerUuid = UUID.fromString(partner.getId());
        PartnerEntity foundPartner = partnerRepository.getByDigitalIdAndUuid(digitalId, partnerUuid)
            .filter(partnerEntity -> PartnerType.PARTNER == partnerEntity.getType())
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, partner.getId()));
        if (!Objects.equals(partner.getVersion(), foundPartner.getVersion())) {
            throw new OptimisticLockException(foundPartner.getVersion(), partner.getVersion());
        }
        partnerMapper.updatePartner(partner, foundPartner);
        PartnerEntity savePartner = partnerRepository.save(foundPartner);
        var accountEntities = accountRepository.findByDigitalIdAndPartnerUuid(digitalId, partnerUuid);
        if (!CollectionUtils.isEmpty(accountEntities)) {
            var accounts = accountMapper.toAccounts(accountEntities);
            replicationService.updateCounterparty(accounts);
        }
        var response = partnerMapper.toPartner(savePartner);
        response.setVersion(response.getVersion() + 1);
        response.setGku(getGku(response.getInn()));
        return response;
    }

    @Override
    @Transactional
    @Audit(eventType = PARTNER_DELETE)
    public void deletePartners(String digitalId, List<String> ids, FraudMetaData fraudMetaData) {
        deletePartners(digitalId, Set.copyOf(ids), fraudMetaData);
    }

    private void deletePartners(String digitalId, Set<String> ids, FraudMetaData fraudMetaData) {
        for (String partnerId : ids) {
            var partnerUuid = partnerMapper.mapUuid(partnerId);
            PartnerEntity foundPartner = partnerRepository.getByDigitalIdAndUuid(digitalId, partnerUuid)
                .filter(partnerEntity -> PartnerType.PARTNER == partnerEntity.getType())
                .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, partnerUuid));
            if (nonNull(fraudMetaData)) {
                fraudServiceManager
                    .getService(FraudEventType.DELETE_PARTNER)
                    .sendEvent(fraudMetaData, foundPartner);
            }
            partnerRepository.delete(foundPartner);
            addressRepository.deleteAll(addressRepository.findByDigitalIdAndUnifiedUuid(digitalId, partnerUuid));
            contactRepository.deleteAll(contactRepository.findByDigitalIdAndPartnerUuid(digitalId, partnerUuid));
            documentRepository.deleteAll(documentRepository.findByDigitalIdAndUnifiedUuid(digitalId, partnerUuid));
            var accounts = accountRepository.findByDigitalIdAndPartnerUuid(digitalId, partnerUuid);
            if (!CollectionUtils.isEmpty(accounts)) {
                accountRepository.deleteAll(accounts);
                replicationService.deleteCounterparties(accounts);
            }
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
