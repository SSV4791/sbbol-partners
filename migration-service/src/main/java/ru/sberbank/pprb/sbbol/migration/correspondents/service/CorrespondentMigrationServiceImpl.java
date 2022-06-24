package ru.sberbank.pprb.sbbol.migration.correspondents.service;

import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.migration.correspondents.entity.MigrationPartnerEntity;
import ru.sberbank.pprb.sbbol.migration.correspondents.mapper.MigrationPartnerMapper;
import ru.sberbank.pprb.sbbol.migration.correspondents.model.MigratedCorrespondentData;
import ru.sberbank.pprb.sbbol.migration.correspondents.model.MigrationCorrespondentCandidate;
import ru.sberbank.pprb.sbbol.migration.correspondents.repository.MigrationPartnerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AutoJsonRpcServiceImpl
public class CorrespondentMigrationServiceImpl implements CorrespondentMigrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CorrespondentMigrationServiceImpl.class);

    private final MigrationPartnerMapper migrationPartnerMapper;
    private final MigrationPartnerRepository migrationPartnerRepository;

    public CorrespondentMigrationServiceImpl(
        MigrationPartnerMapper migrationPartnerMapper,
        MigrationPartnerRepository migrationPartnerRepository
    ) {
        this.migrationPartnerMapper = migrationPartnerMapper;
        this.migrationPartnerRepository = migrationPartnerRepository;
    }

    @Override
    @Transactional
    public List<MigratedCorrespondentData> migrate(String digitalId, List<MigrationCorrespondentCandidate> correspondents) {
        var migratedCorrespondentData = new ArrayList<MigratedCorrespondentData>(correspondents.size());
        LOGGER.debug("Начало миграции контрагентов для организации c digitalId: {}. Количество кандидатов: {}", digitalId, correspondents.size());
        MigrationPartnerEntity savedPartnerEntity;
        for (MigrationCorrespondentCandidate correspondent : correspondents) {
            try {
                MigrationPartnerEntity partnerEntity;
                if (correspondent.getPprbGuid() != null) {
                    partnerEntity = migrationPartnerRepository.findByAccount_Uuid(UUID.fromString(correspondent.getPprbGuid()));
                    if (partnerEntity == null) {
                        partnerEntity = new MigrationPartnerEntity();
                    }
                    migrationPartnerMapper.toMigrationPartnerEntity(digitalId, correspondent, partnerEntity);
                } else {
                    partnerEntity = migrationPartnerMapper.toMigrationPartnerEntity(digitalId, correspondent);
                }
                savedPartnerEntity = migrationPartnerRepository.save(partnerEntity);
            } catch (Exception ex) {
                LOGGER.error(
                    "В процессе миграции контрагента с sbbolReplicationGuid: {}, произошла ошибка. Причина: {}",
                    correspondent.getReplicationGuid(),
                    ex.getCause()
                );
                throw ex;
            }
            String pprbGuid = null;
            if (savedPartnerEntity.getAccount() != null && savedPartnerEntity.getAccount().getUuid() != null) {
                pprbGuid = savedPartnerEntity.getAccount().getUuid().toString();
            }
            migratedCorrespondentData.add(
                new MigratedCorrespondentData(
                    pprbGuid,
                    correspondent.getReplicationGuid(),
                    savedPartnerEntity.getVersion()
                )
            );
        }
        LOGGER.debug("Для организации c digitalId: {}, мигрировано {} контрагентов", digitalId, migratedCorrespondentData.size());
        return migratedCorrespondentData;
    }
}
