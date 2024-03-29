package ru.sberbank.pprb.sbbol.migration.correspondents.service;

import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.migration.correspondents.mapper.MigrationPartnerMapper;
import ru.sberbank.pprb.sbbol.migration.correspondents.model.MigratedCorrespondentData;
import ru.sberbank.pprb.sbbol.migration.correspondents.model.MigrationCorrespondentCandidate;
import ru.sberbank.pprb.sbbol.migration.correspondents.model.MigrationCorrespondentResponse;
import ru.sberbank.pprb.sbbol.migration.correspondents.model.MigrationReplicationGuidCandidate;
import ru.sberbank.pprb.sbbol.migration.exception.MigrationException;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.IdsHistoryEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AccountStateType;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.PartnerType;
import ru.sberbank.pprb.sbbol.partners.exception.AccountAlreadySignedException;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountSignRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AddressRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ContactRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.DocumentRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.GuidsHistoryRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper.mapUuid;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper.prepareSearchString;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper.saveSearchString;

@Service
@AutoJsonRpcServiceImpl
public class CorrespondentMigrationServiceImpl implements CorrespondentMigrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CorrespondentMigrationServiceImpl.class);

    private final MigrationPartnerMapper migrationPartnerMapper;
    private final AccountRepository accountRepository;
    private final DocumentRepository documentRepository;
    private final ContactRepository contactRepository;
    private final AddressRepository addressRepository;
    private final PartnerRepository partnerRepository;
    private final AccountSignRepository accountSignRepository;
    private final GuidsHistoryRepository guidsHistoryRepository;

    public CorrespondentMigrationServiceImpl(
        MigrationPartnerMapper migrationPartnerMapper,
        PartnerRepository partnerRepository,
        DocumentRepository documentRepository,
        ContactRepository contactRepository,
        AddressRepository addressRepository,
        AccountRepository accountRepository,
        AccountSignRepository accountSignRepository,
        GuidsHistoryRepository guidsHistoryRepository
    ) {
        this.migrationPartnerMapper = migrationPartnerMapper;
        this.partnerRepository = partnerRepository;
        this.documentRepository = documentRepository;
        this.contactRepository = contactRepository;
        this.addressRepository = addressRepository;
        this.accountRepository = accountRepository;
        this.accountSignRepository = accountSignRepository;
        this.guidsHistoryRepository = guidsHistoryRepository;
    }

    @Override
    @Transactional
    public MigrationCorrespondentResponse migrate(String digitalId, List<MigrationCorrespondentCandidate> correspondents) {
        var migratedCorrespondentData = new ArrayList<MigratedCorrespondentData>(correspondents.size());
        LOGGER.debug("Начало миграции контрагентов для организации c digitalId: {}. Количество кандидатов: {}", digitalId, correspondents.size());
        AccountEntity savedAccount;
        for (MigrationCorrespondentCandidate correspondent : correspondents) {
            try {
                savedAccount = saveOrUpdate(digitalId, correspondent, true);
            } catch (Exception ex) {
                LOGGER.error(
                    "В процессе миграции контрагента с sbbolReplicationGuid: {}",
                    correspondent.getReplicationGuid(),
                    ex.getCause()
                );
                throw new MigrationException(ex);
            }
            String pprbGuid = null;
            if (savedAccount.getUuid() != null) {
                pprbGuid = savedAccount.getUuid().toString();
            }
            migratedCorrespondentData.add(
                new MigratedCorrespondentData(
                    pprbGuid,
                    correspondent.getReplicationGuid(),
                    savedAccount.getVersion()
                )
            );
        }
        LOGGER.debug("Для организации c digitalId: {}, мигрировано {} контрагентов", digitalId, migratedCorrespondentData.size());
        return new MigrationCorrespondentResponse(migratedCorrespondentData);
    }

    @Override
    @Transactional
    public void clear(String digitalId) {
        List<PartnerEntity> partners = partnerRepository.findByDigitalIdAndType(digitalId, PartnerType.PARTNER);
        for (var partner : partners) {
            partnerRepository.delete(partner);
            UUID partnerId = partner.getUuid();
            addressRepository.deleteAll(addressRepository.findByDigitalIdAndUnifiedUuid(digitalId, partnerId));
            contactRepository.deleteAll(contactRepository.findByDigitalIdAndPartnerUuid(digitalId, partnerId));
            documentRepository.deleteAll(documentRepository.findByDigitalIdAndUnifiedUuid(digitalId, partnerId));
            accountRepository.deleteAll(accountRepository.findByDigitalIdAndPartnerUuid(digitalId, partnerId));
        }
    }

    @Override
    @Transactional
    public MigrationCorrespondentCandidate save(String digitalId, MigrationCorrespondentCandidate correspondent) {
        var accountEntity = saveOrUpdate(digitalId, correspondent, false);
        var partnerEntity = partnerRepository.getByDigitalIdAndUuid(digitalId, accountEntity.getPartnerUuid());
        if (partnerEntity.isEmpty()) {
            throw new EntryNotFoundException("partner", digitalId, accountEntity.getPartnerUuid());
        }
        return migrationPartnerMapper.toCounterparty(partnerEntity.get(), accountEntity);
    }

    @Override
    public void delete(String digitalId, String pprbGuid) {
        if (isEmpty(pprbGuid)) {
            return;
        }
        var foundAccount = accountRepository.getByDigitalIdAndUuid(digitalId, mapUuid(pprbGuid));
        if (foundAccount.isPresent()) {
            var accountEntity = foundAccount.get();
            accountRepository.delete(accountEntity);
            var accountSignEntity =
                accountSignRepository.getByDigitalIdAndAccountUuid(digitalId, accountEntity.getUuid());
            accountSignEntity.ifPresent(accountSignRepository::delete);
        }
    }

    private AccountEntity saveOrUpdate(String digitalId, MigrationCorrespondentCandidate correspondent, boolean migration) {
        var pprbGuid = correspondent.getPprbGuid();
        if (pprbGuid != null) {
            var foundAccount = accountRepository.getByDigitalIdAndUuid(digitalId, mapUuid(pprbGuid));
            if (foundAccount.isPresent()) {
                var account = foundAccount.get();
                checkAccountForSign(account, migration);
                var foundPartner = partnerRepository.getByDigitalIdAndUuid(digitalId, account.getPartnerUuid());
                if (foundPartner.isPresent()) {
                    var partner = foundPartner.get();
                    migrationPartnerMapper.updatePartnerEntity(digitalId, correspondent, partner);
                    partnerRepository.save(partner);
                    migrationPartnerMapper.updateAccountEntity(digitalId, account.getPartnerUuid(), correspondent, account);
                    return accountRepository.save(account);
                }
            }
        }
        var search =
            prepareSearchString(correspondent.getInn(), correspondent.getKpp(), correspondent.getName());
        AccountEntity savedAccount;
        var searchPartner = partnerRepository.findByDigitalIdAndSearchAndType(digitalId, search, PartnerType.PARTNER);
        if (searchPartner == null) {
            var partnerEntity = migrationPartnerMapper.toPartnerEntity(digitalId, correspondent);
            var save = partnerRepository.save(partnerEntity);
            var accountEntity = migrationPartnerMapper.toAccountEntity(digitalId, save.getUuid(), correspondent);
            savedAccount = accountRepository.save(accountEntity);
        } else {
            var bic = correspondent.getBic();
            var account = correspondent.getAccount();
            var bankAccount = correspondent.getBankAccount();
            var searchAccount = saveSearchString(searchPartner.getUuid().toString(), account, bic, bankAccount);
            var foundAccount =
                accountRepository.findByDigitalIdAndSearch(digitalId, searchAccount);
            if (foundAccount == null) {
                var accountEntity =
                    migrationPartnerMapper.toAccountEntity(digitalId, searchPartner.getUuid(), correspondent);
                savedAccount = accountRepository.save(accountEntity);
            } else {
                checkAccountForSign(foundAccount, migration);
                migrationPartnerMapper.updateAccountEntity(digitalId, searchPartner.getUuid(), correspondent, foundAccount);
                savedAccount = accountRepository.save(foundAccount);
            }
            migrationPartnerMapper.updatePartnerEntity(digitalId, correspondent, searchPartner);
            partnerRepository.save(searchPartner);
        }
        return savedAccount;
    }

    private void checkAccountForSign(AccountEntity foundAccount, boolean migration) {
        if (migration) {
            return;
        }
        if (AccountStateType.SIGNED == foundAccount.getState()) {
            throw new AccountAlreadySignedException(foundAccount.getAccount());
        }
    }

    @Override
    @Transactional
    public void migrateReplicationGuid(String digitalId, List<MigrationReplicationGuidCandidate> candidates) {
        candidates.forEach(candidate -> {
            var account = accountRepository.getByDigitalIdAndUuid(digitalId, mapUuid(candidate.getPprbGuid()))
                .orElseThrow(() -> new EntryNotFoundException("account", digitalId, candidate.getPprbGuid()));
            guidsHistoryRepository.save(migrationPartnerMapper.fillIdLinks(account, candidate.getReplicationGuid()));
        });
    }

    @Override
    @Transactional
    public void clearReplicationGuid(String digitalId) {
        List<IdsHistoryEntity> history = guidsHistoryRepository.findByDigitalId(digitalId);
        guidsHistoryRepository.deleteAll(history);
    }
}
