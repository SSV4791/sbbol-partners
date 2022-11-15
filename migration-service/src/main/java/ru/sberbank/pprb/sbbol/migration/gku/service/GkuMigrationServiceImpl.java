package ru.sberbank.pprb.sbbol.migration.gku.service;

import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.migration.gku.entity.MigrationGkuInnEntity;
import ru.sberbank.pprb.sbbol.migration.gku.entity.MigrationGkuInnEntity_;
import ru.sberbank.pprb.sbbol.migration.gku.mapper.MigrationGkuMapper;
import ru.sberbank.pprb.sbbol.migration.gku.model.MigrationGkuCandidate;
import ru.sberbank.pprb.sbbol.migration.gku.repository.MigrationGkuRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AutoJsonRpcServiceImpl
public class GkuMigrationServiceImpl implements GkuMigrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GkuMigrationServiceImpl.class);

    private final MigrationGkuMapper migrationGkuMapper;
    private final MigrationGkuRepository migrationGkuRepository;

    @Value("${migrate.gku.batch_size}")
    private int batchSize;

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
        LOGGER.info("Начало миграции справочника ЖКУ количество записей: {}", gkuInns.size());
        if (CollectionUtils.isEmpty(gkuInns)) {
            return;
        }
        List<MigrationGkuInnEntity> entities = gkuInns.stream()
            .map(value ->
                migrationGkuRepository.getByInn(value.getInn())
                    .orElse(migrationGkuMapper.toDictionary(value)))
            .collect(Collectors.toList());
        try {
            migrationGkuRepository.saveAll(entities);
        } catch (Exception ex) {
            LOGGER.error("В процессе миграции справочника ЖКУ произошла ошибка. Причина: {}", ex.getLocalizedMessage());
            throw ex;
        }
        LOGGER.info("Окончание миграции справочника ЖКУ количество записей: {}", entities.size());
    }

    @Override
    public void delete(LocalDate migrateDate) {
        LOGGER.info("Начало процедуры удаления записей ЖКУ");
        Page<MigrationGkuInnEntity> inns;
        do {
            inns = migrationGkuRepository.findAllByModifiedDateBefore(
                migrateDate,
                PageRequest.of(0, batchSize, Sort.by(MigrationGkuInnEntity_.INN))
            );
            delete(inns.getContent());
        } while (inns.hasNext());
        LOGGER.info("Окончание процедуры удаления записей ЖКУ");
    }

    @Transactional
    public void delete(List<MigrationGkuInnEntity> content) {
        migrationGkuRepository.deleteAll(content);
    }
}
