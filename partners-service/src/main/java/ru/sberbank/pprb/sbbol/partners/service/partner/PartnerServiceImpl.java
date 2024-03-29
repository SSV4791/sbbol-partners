package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.audit.Audit;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.PartnerType;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.exception.OptimisticLockException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerMapper;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.FraudMetaData;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.PartnerFullModelResponse;
import ru.sberbank.pprb.sbbol.partners.model.PartnerInfo;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;
import ru.sberbank.pprb.sbbol.partners.model.PartnersResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.dto.PartnerLegalTypeDto;
import ru.sberbank.pprb.sbbol.partners.repository.partner.dto.PartnerTypeDto;
import ru.sberbank.pprb.sbbol.partners.service.replication.ReplicationService;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static org.springframework.util.CollectionUtils.isEmpty;
import static ru.sberbank.pprb.sbbol.partners.audit.model.EventType.PARTNER_CREATE;
import static ru.sberbank.pprb.sbbol.partners.audit.model.EventType.PARTNER_DELETE;
import static ru.sberbank.pprb.sbbol.partners.audit.model.EventType.PARTNER_FULL_MODEL_CREATE;
import static ru.sberbank.pprb.sbbol.partners.audit.model.EventType.PARTNER_FULL_MODEL_UPDATE;
import static ru.sberbank.pprb.sbbol.partners.audit.model.EventType.PARTNER_UPDATE;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerMapper.toLegalType;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper.prepareSearchString;

@Loggable
public class PartnerServiceImpl implements PartnerService {

    private static final String DOCUMENT_NAME = "partner";

    private final PartnerRepository partnerRepository;
    private final PartnerMapper partnerMapper;
    private final ReplicationService replicationService;
    private final AddressService partnerAddressService;
    private final DocumentService partnerDocumentService;
    private final ContactService contactService;
    private final AccountService accountService;

    public PartnerServiceImpl(
        PartnerRepository partnerRepository,
        PartnerMapper partnerMapper,
        ReplicationService replicationService,
        AddressService partnerAddressService,
        DocumentService partnerDocumentService,
        ContactService contactService,
        AccountService accountService
    ) {
        this.partnerRepository = partnerRepository;
        this.partnerMapper = partnerMapper;
        this.replicationService = replicationService;
        this.partnerAddressService = partnerAddressService;
        this.partnerDocumentService = partnerDocumentService;
        this.contactService = contactService;
        this.accountService = accountService;
    }

    @Override
    @Transactional(readOnly = true)
    public Partner getPartner(String digitalId, UUID id) {
        var partner = findPartnerByDigitalIdAndUuid(digitalId, id);
        return partnerMapper.toPartner(partner);
    }

    @Override
    public void existsPartner(String digitalId, UUID id) throws EntryNotFoundException {
        var partner = partnerRepository.getByDigitalIdAndUuid(digitalId, id, PartnerTypeDto.class)
            .filter(partnerDto -> PartnerType.PARTNER == partnerDto.getType());
        if (partner.isEmpty()) {
            throw new EntryNotFoundException(DOCUMENT_NAME, digitalId, id);
        }
    }

    @Override
    public LegalForm getPartnerLegalForm(String digitalId, UUID id) {
        var partner = partnerRepository.getByDigitalIdAndUuid(digitalId, id, PartnerLegalTypeDto.class)
            .filter(partnerDto -> PartnerType.PARTNER == partnerDto.getType())
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, id));
        return toLegalType(partner.getLegalType());
    }

    @Override
    @Transactional(readOnly = true)
    public PartnerInfo findPartner(@NotNull String digitalId, String name, String inn, String kpp) {
        var search = prepareSearchString(inn, kpp, name);
        var foundPartner = partnerRepository.findByDigitalIdAndSearchAndType(digitalId, search, PartnerType.PARTNER);
        if (Objects.isNull(foundPartner)) {
            throw new EntryNotFoundException(DOCUMENT_NAME, digitalId);
        }
        return partnerMapper.toPartnerInfo(foundPartner);
    }

    @Override
    public PartnersResponse getPartners(PartnersFilter partnersFilter) {
        var partnersResponse = new PartnersResponse();
        var response = partnerRepository.findByFilter(partnersFilter);
        for (PartnerEntity entity : response) {
            partnersResponse.addPartnersItem(partnerMapper.toPartnerInfo(entity));
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
        return partnersResponse;
    }

    @Override
    @Transactional
    @Audit(eventType = PARTNER_FULL_MODEL_CREATE)
    public PartnerFullModelResponse savePartner(PartnerCreateFullModel partner) {
        var partnerEntity = partnerMapper.toPartner(partner);
        var savedPartner = partnerRepository.save(partnerEntity);
        var digitalId = partner.getDigitalId();
        var partnerUuid = savedPartner.getUuid();
        var accounts = accountService.saveAccounts(partner.getAccounts(), digitalId, partnerUuid);
        var addresses = partnerAddressService.saveAddresses(digitalId, partnerUuid, partner.getAddress());
        var documents = partnerDocumentService.saveDocuments(digitalId, partnerUuid, partner.getDocuments());
        var contacts = contactService.saveContacts(digitalId, partnerUuid, partner.getContacts());
        if (!isEmpty(accounts)) {
            replicationService.createCounterparty(accounts);
        }
        return partnerMapper.toPartnerFullResponse(savedPartner)
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
        return partnerMapper.toPartner(savePartner);
    }

    @Override
    @Transactional
    @Audit(eventType = PARTNER_UPDATE)
    public Partner patchPartner(Partner partner) {
        var digitalId = partner.getDigitalId();
        var partnerId = partner.getId();
        var foundPartner = findPartnerForPatch(digitalId, partnerId, partner.getVersion());
        partnerMapper.updatePartner(partner, foundPartner);
        var savePartner = partnerRepository.save(foundPartner);
        accountService.getAccountsByPartnerId(digitalId, partnerId)
            .forEach(replicationService::updateCounterparty);
        return partnerMapper.toPartner(savePartner);
    }

    @Override
    @Transactional
    @Audit(eventType = PARTNER_FULL_MODEL_UPDATE)
    public PartnerFullModelResponse patchPartner(PartnerChangeFullModel partner) {
        var digitalId = partner.getDigitalId();
        var partnerId = partner.getId();
        var foundPartner = findPartnerForPatch(digitalId, partnerId, partner.getVersion());
        partnerMapper.patchPartner(partner, foundPartner);
        var savedPartner = partnerRepository.save(foundPartner);
        partnerAddressService.saveOrPatchAddresses(digitalId, partnerId, partner.getAddress());
        partnerDocumentService.saveOrPatchDocuments(digitalId, partnerId, partner.getDocuments());
        contactService.saveOrPatchContacts(digitalId, partnerId, partner.getContacts());
        accountService.saveOrPatchAccounts(digitalId, partnerId, partner.getAccounts());
        var addresses = partnerAddressService.getAddressesByUnifiedUuid(digitalId, partnerId);
        var documents = partnerDocumentService.getDocumentsByUnifiedUuid(digitalId, partnerId);
        var contacts = contactService.getContactsByPartnerUuid(digitalId, partnerId);
        var accounts = accountService.getAccountsByPartnerId(digitalId, partnerId);
        return partnerMapper.toPartnerFullResponse(savedPartner)
            .address(addresses)
            .documents(documents)
            .contacts(contacts)
            .accounts(accounts);
    }

    @Override
    @Transactional
    @Audit(eventType = PARTNER_DELETE)
    public void deletePartners(String digitalId, List<UUID> ids, FraudMetaData fraudMetaData) {
        var uuids = Set.copyOf(ids);
        for (UUID partnerId : uuids) {
            PartnerEntity foundPartner = findPartnerByDigitalIdAndUuid(digitalId, partnerId);
            partnerRepository.delete(foundPartner);
            partnerAddressService.deleteAddressesByUnifiedUuid(digitalId, partnerId);
            contactService.deleteContactsByPartnerUuid(digitalId, partnerId);
            partnerDocumentService.deleteDocumentsByUnifiedUuid(digitalId, partnerId);

            var accountIds = accountService.getAccountsByPartnerId(digitalId, partnerId).stream()
                .map(Account::getId)
                .toList();
            accountService.deleteAccounts(digitalId, accountIds);
            replicationService.deleteCounterparties(digitalId, accountIds.stream().map(UUID::toString).toList());
        }
    }

    private PartnerEntity findPartnerForPatch(String digitalId, UUID partnerId, Long version) {
        var foundPartner = findPartnerByDigitalIdAndUuid(digitalId, partnerId);
        if (!Objects.equals(version, foundPartner.getVersion())) {
            throw new OptimisticLockException(foundPartner.getVersion(), version);
        }
        return foundPartner;
    }

    private PartnerEntity findPartnerByDigitalIdAndUuid(String digitalId, UUID partnerId) {
        return partnerRepository.getByDigitalIdAndUuid(digitalId, partnerId)
            .filter(partnerEntity -> PartnerType.PARTNER == partnerEntity.getType())
            .orElseThrow(() -> new EntryNotFoundException(DOCUMENT_NAME, digitalId, partnerId));
    }
}
