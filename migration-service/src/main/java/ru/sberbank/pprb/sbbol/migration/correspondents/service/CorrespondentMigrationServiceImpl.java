package ru.sberbank.pprb.sbbol.migration.correspondents.service;

import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.migration.correspondents.mapper.MigrationPartnerMapper;
import ru.sberbank.pprb.sbbol.migration.correspondents.model.MigratedCorrespondentData;
import ru.sberbank.pprb.sbbol.migration.correspondents.model.MigrationCorrespondentCandidate;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountSignRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AutoJsonRpcServiceImpl
public class CorrespondentMigrationServiceImpl implements CorrespondentMigrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CorrespondentMigrationServiceImpl.class);

    private final MigrationPartnerMapper migrationPartnerMapper;
    private final PartnerRepository partnerRepository;
    private final AccountRepository accountRepository;
    private final AccountSignRepository accountSignRepository;

    public CorrespondentMigrationServiceImpl(
        MigrationPartnerMapper migrationPartnerMapper,
        PartnerRepository partnerRepository,
        AccountRepository accountRepository,
        AccountSignRepository accountSignRepository
    ) {
        this.migrationPartnerMapper = migrationPartnerMapper;
        this.partnerRepository = partnerRepository;
        this.accountRepository = accountRepository;
        this.accountSignRepository = accountSignRepository;
    }

    @Override
    @Transactional
    public List<MigratedCorrespondentData> migrate(String digitalId, List<MigrationCorrespondentCandidate> correspondents) {
        var migratedCorrespondentData = new ArrayList<MigratedCorrespondentData>(correspondents.size());
        LOGGER.debug("Начало миграции контрагентов для организации c digitalId: {}. Количество кандидатов: {}", digitalId, correspondents.size());
        AccountEntity savedAccount;
        for (MigrationCorrespondentCandidate correspondent : correspondents) {
            try {
                savedAccount = saveOrUpdate(digitalId, correspondent);
            } catch (Exception ex) {
                LOGGER.error(
                    "В процессе миграции контрагента с sbbolReplicationGuid: {}, произошла ошибка. Причина: {}",
                    correspondent.getReplicationGuid(),
                    ex.getCause()
                );
                throw ex;
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
        return migratedCorrespondentData;
    }

    @Override
    @Transactional
    public MigrationCorrespondentCandidate save(String digitalId, MigrationCorrespondentCandidate correspondent) {
        var accountEntity = saveOrUpdate(digitalId, correspondent);
        var partnerEntity = partnerRepository.getByDigitalIdAndUuid(digitalId, accountEntity.getPartnerUuid());
        if (partnerEntity.isEmpty()) {
            throw new EntryNotFoundException("partner", digitalId, accountEntity.getPartnerUuid());
        }
        return migrationPartnerMapper.toCounterparty(partnerEntity.get(), accountEntity);
    }

    @Override
    public void delete(String digitalId, String pprbGuid) {
        if (StringUtils.isEmpty(pprbGuid)) {
            return;
        }
        var uuid = UUID.fromString(pprbGuid);
        var foundAccount = accountRepository.getByDigitalIdAndUuid(digitalId, uuid);
        if (foundAccount.isPresent()) {
            var accountEntity = foundAccount.get();
            accountRepository.delete(accountEntity);
            var accountSignEntity =
                accountSignRepository.getByDigitalIdAndAccountUuid(digitalId, accountEntity.getUuid());
            accountSignEntity.ifPresent(accountSignRepository::delete);
        }

    }

    private AccountEntity saveOrUpdate(String digitalId, MigrationCorrespondentCandidate correspondent) {
        var pprbGuid = correspondent.getPprbGuid();
        if (pprbGuid != null) {
            var foundAccount = accountRepository.getByDigitalIdAndUuid(digitalId, UUID.fromString(pprbGuid));
            if (foundAccount.isPresent()) {
                var account = foundAccount.get();
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
            migrationPartnerMapper.saveSearchString(correspondent.getInn(), correspondent.getKpp(), correspondent.getName());
        AccountEntity savedAccount;
        var searchPartner = partnerRepository.findByDigitalIdAndSearch(digitalId, search);
        if (searchPartner == null) {
            var partnerEntity = migrationPartnerMapper.toPartnerEntity(digitalId, correspondent);
            var save = partnerRepository.save(partnerEntity);
            var accountEntity = migrationPartnerMapper.toAccountEntity(digitalId, save.getUuid(), correspondent);
            savedAccount = accountRepository.save(accountEntity);
        } else {
            var bic = correspondent.getBic();
            var account = correspondent.getAccount();
            var bankAccount = correspondent.getBankAccount();
            var searchAccount = migrationPartnerMapper.prepareSearchString(searchPartner.getUuid().toString(), account, bic, bankAccount);
            var foundAccount =
                accountRepository.findByDigitalIdAndSearch(digitalId, searchAccount);
            if (foundAccount == null) {
                var accountEntity =
                    migrationPartnerMapper.toAccountEntity(digitalId, searchPartner.getUuid(), correspondent);
                savedAccount = accountRepository.save(accountEntity);
            } else {
                migrationPartnerMapper.updateAccountEntity(digitalId, searchPartner.getUuid(), correspondent, foundAccount);
                savedAccount = accountRepository.save(foundAccount);
            }
            migrationPartnerMapper.updatePartnerEntity(digitalId, correspondent, searchPartner);
            partnerRepository.save(searchPartner);
        }
        return savedAccount;
    }
}
