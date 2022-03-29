package ru.sberbank.pprb.sbbol.migration.gku.service;


import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.migration.gku.mapper.MigrationGkuMapper;
import ru.sberbank.pprb.sbbol.migration.gku.model.MigrationGkuCandidate;
import ru.sberbank.pprb.sbbol.migration.gku.repository.MigrationGkuRepository;

import java.util.List;

@Service
@AutoJsonRpcServiceImpl
public class GkuMigrationServiceImpl implements GkuMigrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GkuMigrationServiceImpl.class);

    private final MigrationGkuMapper migrationGkuMapper;
    private final MigrationGkuRepository migrationGkuRepository;

    public GkuMigrationServiceImpl(
        MigrationGkuMapper migrationGkuMapper,
        MigrationGkuRepository migrationGkuRepository
    ) {
        this.migrationGkuMapper = migrationGkuMapper;
        this.migrationGkuRepository = migrationGkuRepository;
    }

    @Override
    @Transactional
    public void migrate(List<MigrationGkuCandidate> gkuInns) {
        LOGGER.debug("Начало миграции справочника ЖКУ количество записей: {}", gkuInns.size());
        if (CollectionUtils.isEmpty(gkuInns)) {
            return;
        }
        migrationGkuRepository.deleteAll();
        var entities = migrationGkuMapper.toDictionary(gkuInns);
        try {
            migrationGkuRepository.saveAll(entities);
        } catch (Exception ex) {
            LOGGER.error("В процессе миграции справочника ЖКУ произошла ошибка. Причина: {}", ex.getLocalizedMessage());
            throw ex;
        }
        LOGGER.debug("Окончание миграции справочника ЖКУ количество записей: {}", entities.size());
    }
}
