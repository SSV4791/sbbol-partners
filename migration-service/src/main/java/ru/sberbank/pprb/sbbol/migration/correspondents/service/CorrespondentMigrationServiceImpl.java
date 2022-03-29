package ru.sberbank.pprb.sbbol.migration.correspondents.service;

import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.migration.correspondents.entity.MigrationReplicationHistoryEntity;
import ru.sberbank.pprb.sbbol.migration.correspondents.entity.MigrationBankAccountEntity;
import ru.sberbank.pprb.sbbol.migration.correspondents.entity.MigrationBankEntity;
import ru.sberbank.pprb.sbbol.migration.correspondents.entity.MigrationPartnerAccountEntity;
import ru.sberbank.pprb.sbbol.migration.correspondents.entity.MigrationPartnerEmailEntity;
import ru.sberbank.pprb.sbbol.migration.correspondents.entity.MigrationPartnerEntity;
import ru.sberbank.pprb.sbbol.migration.correspondents.entity.MigrationPartnerPhoneEntity;
import ru.sberbank.pprb.sbbol.migration.correspondents.mapper.MigrationPartnerMapper;
import ru.sberbank.pprb.sbbol.migration.correspondents.mapper.MigrationReplicationHistoryMapper;
import ru.sberbank.pprb.sbbol.migration.correspondents.model.MigratedCorrespondentData;
import ru.sberbank.pprb.sbbol.migration.correspondents.model.MigrationCorrespondentCandidate;
import ru.sberbank.pprb.sbbol.migration.correspondents.repository.MigrationPartnerRepository;
import ru.sberbank.pprb.sbbol.migration.correspondents.repository.MigrationReplicationHistoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.ObjectUtils.allNotNull;
import static org.apache.commons.lang3.ObjectUtils.anyNull;

@Service
@AutoJsonRpcServiceImpl
public class CorrespondentMigrationServiceImpl implements CorrespondentMigrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CorrespondentMigrationServiceImpl.class);

    private final MigrationPartnerMapper migrationPartnerMapper;
    private final MigrationReplicationHistoryMapper migrationReplicationHistoryMapper;
    private final MigrationPartnerRepository migrationPartnerRepository;
    private final MigrationReplicationHistoryRepository migrationReplicationHistoryRepository;

    public CorrespondentMigrationServiceImpl(MigrationPartnerMapper migrationPartnerMapper,
                                             MigrationReplicationHistoryMapper migrationReplicationHistoryMapper,
                                             MigrationPartnerRepository migrationPartnerRepository,
                                             MigrationReplicationHistoryRepository migrationReplicationHistoryRepository
    ) {
        this.migrationPartnerMapper = migrationPartnerMapper;
        this.migrationReplicationHistoryMapper = migrationReplicationHistoryMapper;
        this.migrationPartnerRepository = migrationPartnerRepository;
        this.migrationReplicationHistoryRepository = migrationReplicationHistoryRepository;
    }

    @Override
    @Transactional
    public List<MigratedCorrespondentData> migrate(String digitalId, List<MigrationCorrespondentCandidate> correspondents) {
        var migratedCorrespondentData = new ArrayList<MigratedCorrespondentData>(correspondents.size());
        LOGGER.debug("Начало миграции контрагентов для организации c digitalId: {}. Количество кандидатов: {}", digitalId, correspondents.size());
        MigrationPartnerEntity savedPartnerEntity;
        MigrationReplicationHistoryEntity existingReplicationHistoryEntity;
        for (MigrationCorrespondentCandidate correspondent : correspondents) {
            try {
                existingReplicationHistoryEntity = migrationReplicationHistoryRepository.getBySbbolGuid(correspondent.getReplicationGuid());
                var partnerEntity = migrationPartnerMapper.toMigrationPartnerEntity(digitalId, correspondent);
                if (existingReplicationHistoryEntity == null) {
                    savedPartnerEntity = migrationPartnerRepository.save(partnerEntity);
                    var replicationHistoryEntity = migrationReplicationHistoryMapper.toReplicationHistoryEntity(savedPartnerEntity);
                    replicationHistoryEntity.setSbbolGuid(correspondent.getReplicationGuid());
                    existingReplicationHistoryEntity = migrationReplicationHistoryRepository.save(replicationHistoryEntity);
                } else {
                    savedPartnerEntity = migrationPartnerRepository.save(fillEntityUuids(partnerEntity, existingReplicationHistoryEntity));
                }
            } catch (Exception ex) {
                LOGGER.error("В процессе миграции контрагента с sbbolReplicationGuid: {}, произошла ошибка. Причина: {}", correspondent.getReplicationGuid(), ex.getCause());
                throw ex;
            }
            migratedCorrespondentData.add(new MigratedCorrespondentData(
                existingReplicationHistoryEntity.getPartnerUuid().toString(),
                existingReplicationHistoryEntity.getSbbolGuid(),
                savedPartnerEntity.getVersion())
            );
        }
        LOGGER.debug("Для организации c digitalId: {}, мигрировано {} контрагентов", digitalId, migratedCorrespondentData.size());
        return migratedCorrespondentData;
    }

    private MigrationPartnerEntity fillEntityUuids(MigrationPartnerEntity partnerEntity, MigrationReplicationHistoryEntity replicationHistoryEntity) {
        if (anyNull(partnerEntity, replicationHistoryEntity)) {
            throw new IllegalArgumentException("Сущность Партнера или сущность Истории репликации не может быть null");
        }
        if (ObjectUtils.allNotNull(replicationHistoryEntity.getPartnerUuid())) {
            partnerEntity.setUuid(replicationHistoryEntity.getPartnerUuid());
        }
        UUID accountUuid = replicationHistoryEntity.getAccountUuid();
        MigrationPartnerAccountEntity partnerAccount = partnerEntity.getAccount();
        if (allNotNull(accountUuid, partnerAccount)) {
            partnerAccount.setUuid(accountUuid);
            UUID bankUuid = replicationHistoryEntity.getBankUuid();
            MigrationBankEntity bank = partnerAccount.getBank();
            if (allNotNull(bankUuid, bank)) {
                bank.setUuid(bankUuid);
                UUID bankAccountUuid = replicationHistoryEntity.getBankAccountUuid();
                MigrationBankAccountEntity bankAccount = bank.getBankAccount();
                if (allNotNull(bankAccountUuid, bankAccount)) {
                    bankAccount.setUuid(bankAccountUuid);
                }
            }
        }
        UUID emailUuid = replicationHistoryEntity.getEmailUuid();
        MigrationPartnerEmailEntity email = partnerEntity.getEmail();
        if (allNotNull(emailUuid, email)) {
            email.setUuid(emailUuid);
        }
        UUID phoneUuid = replicationHistoryEntity.getPhoneUuid();
        MigrationPartnerPhoneEntity phone = partnerEntity.getPhone();
        if (allNotNull(phoneUuid, phone)) {
            phone.setUuid(phoneUuid);
        }
        return partnerEntity;
    }
}
