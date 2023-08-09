package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.audit.Audit;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.PartnerType;
import ru.sberbank.pprb.sbbol.partners.exception.ModelDuplicateException;
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
import ru.sberbank.pprb.sbbol.partners.model.PartnerChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.PartnerFullModelResponse;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;
import ru.sberbank.pprb.sbbol.partners.model.PartnersResponse;
import ru.sberbank.pprb.sbbol.partners.model.fraud.FraudEventType;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AddressRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ContactRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;
import ru.sberbank.pprb.sbbol.partners.service.fraud.FraudServiceManager;
import ru.sberbank.pprb.sbbol.partners.service.replication.ReplicationService;
import ru.sberbank.pprb.sbbol.partners.storage.GkuInnCacheableStorage;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.springframework.util.CollectionUtils.isEmpty;
import static ru.sberbank.pprb.sbbol.partners.audit.model.EventType.PARTNER_CREATE;
import static ru.sberbank.pprb.sbbol.partners.audit.model.EventType.PARTNER_DELETE;
import static ru.sberbank.pprb.sbbol.partners.audit.model.EventType.PARTNER_FULL_MODEL_CREATE;
import static ru.sberbank.pprb.sbbol.partners.audit.model.EventType.PARTNER_FULL_MODEL_UPDATE;
import static ru.sberbank.pprb.sbbol.partners.audit.model.EventType.PARTNER_UPDATE;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper.mapUuid;

@Loggable
public class PartnerServiceImpl implements PartnerService {

    private static final String DOCUMENT_NAME = "partner";

    private final AccountRepository accountRepository;
    private final DocumentRepository documentRepository;
    private final ContactRepository contactRepository;
    private final AddressRepository addressRepository;
    private final PartnerRepository partnerRepository;
    private final GkuInnCacheableStorage gkuInnCacheableStorage;
    private final BudgetMaskService budgetMaskService;
    private final FraudServiceManager fraudServiceManager;
    private final AccountMapper accountMapper;
    private final DocumentMapper documentMapper;
    private final AddressMapper addressMapper;
    private final ContactMapper contactMapper;
    private final PartnerMapper partnerMapper;
    private final ReplicationService replicationService;
    private final AddressService partnerAddressService;
    private final DocumentService partnerDocumentService;
    private final ContactService contactService;
    private final AccountService accountService;

    public PartnerServiceImpl(
        AccountRepository accountRepository,
        DocumentRepository documentRepository,
        ContactRepository contactRepository,
        AddressRepository addressRepository,
        PartnerRepository partnerRepository,
        GkuInnCacheableStorage gkuInnCacheableStorage,
        BudgetMaskService budgetMaskService,
        FraudServiceManager fraudServiceManager,
        AccountMapper accountMapper,
        DocumentMapper documentMapper,
        AddressMapper addressMapper,
        ContactMapper contactMapper,
        PartnerMapper partnerMapper,
        ReplicationService replicationService,
        AddressService partnerAddressService,
        DocumentService partnerDocumentService,
        ContactService contactService,
        AccountService accountService
    ) {
        this.accountRepository = accountRepository;
        this.documentRepository = documentRepository;
        this.contactRepository = contactRepository;
        this.addressRepository = addressRepository;
        this.partnerRepository = partnerRepository;
        this.gkuInnCacheableStorage = gkuInnCacheableStorage;
        this.budgetMaskService = budgetMaskService;
        this.fraudServiceManager = fraudServiceManager;
        this.accountMapper = accountMapper;
        this.documentMapper = documentMapper;
        this.addressMapper = addressMapper;
        this.contactMapper = contactMapper;
        this.partnerMapper = partnerMapper;
        this.replicationService = replicationService;
        this.partnerAddressService = partnerAddressService;
        this.partnerDocumentService = partnerDocumentService;
        this.contactService = contactService;
        this.accountService = accountService;
    }

    @Override
    @Transactional(readOnly = true)
    public Partner getPartner(String digitalId, String id) {
        PartnerEntity partner = partnerRepository.getByDigitalIdAndUuid(digitalId, mapUuid(id))
            .filter(partnerEntity -> PartnerType.PARTNER == partnerEntity.getType())
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, id));
        var response = partnerMapper.toPartner(partner);
        response.setGku(isGkuInn(response.getInn()));
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
        if (isEmpty(partners)) {
            return partnersResponse;
        }
        for (Partner partner : partners) {
            partner.setGku(isGkuInn(partner.getInn()));
        }
        return partnersResponse.partners(partners);
    }

    @Override
    @Transactional
    @Audit(eventType = PARTNER_FULL_MODEL_CREATE)
    public PartnerFullModelResponse savePartner(PartnerCreateFullModel partner) {
        checkPartnerDuplicate(partner);
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
        if (!isEmpty(accounts)) {
            replicationService.createCounterparty(accounts);
        }
        return partnerMapper.toPartnerMullResponse(savedPartner)
            .gku(isGkuInn(savedPartner.getInn()))
            .accounts(accounts)
            .address(addresses)
            .documents(documents)
            .contacts(contacts);
    }

    @Override
    @Transactional
    @Audit(eventType = PARTNER_CREATE)
    public Partner savePartner(PartnerCreate partner) {
        checkPartnerDuplicate(partner);
        var partnerEntity = partnerMapper.toPartner(partner);
        var savePartner = partnerRepository.save(partnerEntity);
        var response = partnerMapper.toPartner(savePartner);
        response.setGku(isGkuInn(response.getInn()));
        return response;
    }

    @Override
    @Transactional
    @Audit(eventType = PARTNER_UPDATE)
    public Partner patchPartner(Partner partner) {
        checkPartnerDuplicate(partner);
        var digitalId = partner.getDigitalId();
        var partnerId = partner.getId();
        var foundPartner = findPartnerEntity(digitalId, partnerId, partner.getVersion());
        partnerMapper.updatePartner(partner, foundPartner);
        PartnerEntity savePartner = partnerRepository.save(foundPartner);
        var accountEntities = accountRepository.findByDigitalIdAndPartnerUuid(digitalId, mapUuid(partnerId));
        if (!isEmpty(accountEntities)) {
            var accounts = accountMapper.toAccounts(accountEntities);
            replicationService.updateCounterparty(accounts);
        }
        var response = partnerMapper.toPartner(savePartner);
        response.setGku(isGkuInn(response.getInn()));
        return response;
    }

    @Override
    @Transactional
    @Audit(eventType = PARTNER_FULL_MODEL_UPDATE)
    public PartnerFullModelResponse patchPartner(PartnerChangeFullModel partner) {
        var digitalId = partner.getDigitalId();
        var partnerId = partner.getId();
        var partnerUuid = mapUuid(partnerId);
        var foundPartner = findPartnerEntity(digitalId, partnerId, partner.getVersion());
        checkPartnerDuplicate(partner, foundPartner);
        partnerMapper.patchPartner(partner, foundPartner);
        PartnerEntity savedPartner = partnerRepository.save(foundPartner);
        partnerAddressService.saveOrPatchAddresses(digitalId, partnerId, partner.getAddress());
        partnerDocumentService.saveOrPatchDocuments(digitalId, partnerId, partner.getDocuments());
        contactService.saveOrPatchContacts(digitalId, partnerId, partner.getContacts());
        accountService.saveOrPatchAccounts(digitalId, partnerId, partner.getAccounts());
        var addresses = addressRepository.findByDigitalIdAndUnifiedUuid(digitalId, partnerUuid).stream()
            .map(addressMapper::toAddress)
            .collect(Collectors.toList());
        var documents = documentRepository.findByDigitalIdAndUnifiedUuid(digitalId, partnerUuid).stream()
            .map(documentMapper::toDocument)
            .collect(Collectors.toList());
        var contacts = contactRepository.findByDigitalIdAndPartnerUuid(digitalId, partnerUuid).stream()
            .map(contactMapper::toContact)
            .collect(Collectors.toList());
        var accounts = accountRepository.findByDigitalIdAndPartnerUuid(digitalId, partnerUuid).stream()
            .map(accountMapper::toAccount)
            .collect(Collectors.toList());
        return partnerMapper.toPartnerMullResponse(savedPartner)
            .gku(isGkuInn(savedPartner.getInn()))
            .address(addresses)
            .documents(documents)
            .contacts(contacts)
            .accounts(accounts);
    }

    @Override
    @Transactional
    @Audit(eventType = PARTNER_DELETE)
    public void deletePartners(String digitalId, List<String> ids, FraudMetaData fraudMetaData) {
        deletePartners(digitalId, Set.copyOf(ids), fraudMetaData);
    }

    private void deletePartners(String digitalId, Set<String> ids, FraudMetaData fraudMetaData) {
        for (String partnerId : ids) {
            var partnerUuid = mapUuid(partnerId);
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
            if (!isEmpty(accounts)) {
                accountRepository.deleteAll(accounts);
                replicationService.deleteCounterparties(accounts);
            }
        }
    }

    private PartnerEntity findPartnerEntity(String digitalId, String partnerId, Long version) {
        PartnerEntity foundPartner = partnerRepository.getByDigitalIdAndUuid(digitalId, mapUuid(partnerId))
            .filter(partnerEntity -> PartnerType.PARTNER == partnerEntity.getType())
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, partnerId));
        if (!Objects.equals(version, foundPartner.getVersion())) {
            throw new OptimisticLockException(foundPartner.getVersion(), version);
        }
        return foundPartner;
    }

    private boolean isGkuInn(String inn) {
        return gkuInnCacheableStorage.isGkuInn(inn);
    }

    private void checkPartnerDuplicate(PartnerCreateFullModel partner) {
        checkPartnerDuplicate(
            partner.getDigitalId(),
            partner.getInn(),
            partner.getKpp(),
            partner.getOrgName(),
            partner.getSecondName(),
            partner.getFirstName(),
            partner.getMiddleName()
        );
    }

    private void checkPartnerDuplicate(PartnerCreate partner) {
        checkPartnerDuplicate(
            partner.getDigitalId(),
            partner.getInn(),
            partner.getKpp(),
            partner.getOrgName(),
            partner.getSecondName(),
            partner.getFirstName(),
            partner.getMiddleName()
        );
    }

    private void checkPartnerDuplicate(Partner partner) {
        checkPartnerDuplicate(
            UUID.fromString(partner.getId()),
            partner.getDigitalId(),
            partner.getInn(),
            partner.getKpp(),
            partner.getOrgName(),
            partner.getSecondName(),
            partner.getFirstName(),
            partner.getMiddleName()
        );
    }

    private void checkPartnerDuplicate(PartnerChangeFullModel partner, PartnerEntity partnerEntity) {
        var inn = Optional.ofNullable(partner.getInn())
            .orElse(partnerEntity.getInn());
        var kpp = Optional.ofNullable(partner.getKpp())
            .orElse(partnerEntity.getKpp());
        var orgName = Optional.ofNullable(partner.getOrgName())
            .orElse(partnerEntity.getOgrn());
        var secondName = Optional.ofNullable(partner.getSecondName())
            .orElse(partnerEntity.getSecondName());
        var firstName = Optional.ofNullable(partner.getFirstName())
            .orElse(partnerEntity.getFirstName());
        var middleName = Optional.ofNullable(partner.getMiddleName())
            .orElse(partnerEntity.getMiddleName());
        checkPartnerDuplicate(
            UUID.fromString(partner.getId()),
            partner.getDigitalId(),
            inn,
            kpp,
            orgName,
            secondName,
            firstName,
            middleName
        );
    }

    private void checkPartnerDuplicate(
        String digitalId,
        String inn,
        String kpp,
        String orgName,
        String secondName,
        String firstName,
        String middleName
    ) {
        checkPartnerDuplicate(null, digitalId, inn, kpp, orgName, secondName, firstName, middleName);
    }

    private void checkPartnerDuplicate(
        UUID uuid,
        String digitalId,
        String inn,
        String kpp,
        String orgName,
        String secondName,
        String firstName,
        String middleName
    ) {
        var search = partnerMapper.prepareSearchField(inn, kpp, orgName, secondName, firstName, middleName);
        var partnerEntity = partnerRepository.findByDigitalIdAndSearchAndType(digitalId, search, PartnerType.PARTNER);
        if (nonNull(partnerEntity) && !partnerEntity.getUuid().equals(uuid)) {
            throw new ModelDuplicateException(DOCUMENT_NAME);
        }
    }
}
