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
import ru.sberbank.pprb.sbbol.migration.exception.MigrationException;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AccountStateType;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.PartnerType;
import ru.sberbank.pprb.sbbol.partners.exception.AccountAlreadySignedException;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountSignRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;
import ru.sberbank.pprb.sbbol.partners.service.ids.history.IdsHistoryService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper.prepareSearchString;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper.saveSearchString;

@Service
@AutoJsonRpcServiceImpl
public class CorrespondentMigrationServiceImpl implements CorrespondentMigrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CorrespondentMigrationServiceImpl.class);

    private final MigrationPartnerMapper migrationPartnerMapper;
    private final PartnerRepository partnerRepository;
    private final AccountRepository accountRepository;
    private final AccountSignRepository accountSignRepository;
    private final IdsHistoryService idsHistoryService;

    public CorrespondentMigrationServiceImpl(
        MigrationPartnerMapper migrationPartnerMapper,
        PartnerRepository partnerRepository,
        AccountRepository accountRepository,
        AccountSignRepository accountSignRepository,
        IdsHistoryService idsHistoryService
    ) {
        this.migrationPartnerMapper = migrationPartnerMapper;
        this.partnerRepository = partnerRepository;
        this.accountRepository = accountRepository;
        this.accountSignRepository = accountSignRepository;
        this.idsHistoryService = idsHistoryService;
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
                idsHistoryService.add(digitalId, UUID.fromString(correspondent.getReplicationGuid()), savedAccount.getUuid());
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
        var uuid = UUID.fromString(pprbGuid);
        var foundAccount = accountRepository.getByDigitalIdAndUuid(digitalId, uuid);
        if (foundAccount.isPresent()) {
            var accountEntity = foundAccount.get();
            accountRepository.delete(accountEntity);
            idsHistoryService.delete(digitalId, accountEntity.getUuid());
            var accountSignEntity =
                accountSignRepository.getByDigitalIdAndAccountUuid(digitalId, accountEntity.getUuid());
            accountSignEntity.ifPresent(accountSignRepository::delete);
        }
    }

    private AccountEntity saveOrUpdate(String digitalId, MigrationCorrespondentCandidate correspondent, boolean migration) {
        var pprbGuid = correspondent.getPprbGuid();
        if (pprbGuid != null) {
            var foundAccount = accountRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(pprbGuid));
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
}
