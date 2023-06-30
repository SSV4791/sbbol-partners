package ru.sberbank.pprb.sbbol.migration.gku.service;

import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.migration.exception.MigrationException;
import ru.sberbank.pprb.sbbol.migration.gku.mapper.MigrationGkuMapper;
import ru.sberbank.pprb.sbbol.migration.gku.model.MigrationGkuCandidate;
import ru.sberbank.pprb.sbbol.migration.gku.repository.MigrationGkuRepository;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BaseEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.GkuInnEntity;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
@AutoJsonRpcServiceImpl
public class GkuMigrationServiceImpl implements GkuMigrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GkuMigrationServiceImpl.class);

    private final MigrationGkuMapper migrationGkuMapper;
    private final MigrationGkuRepository migrationGkuRepository;
    private final ExecutorService executorService;

    @Value("${migrate.gku.batch_size}")
    private int batchSize;

    public GkuMigrationServiceImpl(
        MigrationGkuMapper migrationGkuMapper,
        MigrationGkuRepository migrationGkuRepository,
        ExecutorService gkuExecutorService
    ) {
        this.migrationGkuMapper = migrationGkuMapper;
        this.migrationGkuRepository = migrationGkuRepository;
        this.executorService = gkuExecutorService;
    }

    @Override
    @Transactional
    public void migrate(List<MigrationGkuCandidate> gkuInns) {
        LOGGER.info("Начало миграции справочника ЖКУ количество записей: {}", gkuInns.size());
        if (CollectionUtils.isEmpty(gkuInns)) {
            return;
        }
        List<GkuInnEntity> entities = gkuInns.stream()
            .map(value ->
                migrationGkuRepository.getByInn(value.getInn())
                    .orElse(migrationGkuMapper.toDictionary(value)))
            .collect(Collectors.toList());
        entities.forEach(BaseEntity::updateSysLastChangeDate);
        try {
            migrationGkuRepository.saveAll(entities);
        } catch (Exception ex) {
            LOGGER.error("В процессе миграции справочника ЖКУ произошла ошибка. Причина: {}", ex.getLocalizedMessage());
            throw new MigrationException(ex);
        }
        LOGGER.info("Окончание миграции справочника ЖКУ количество записей: {}", entities.size());
    }

    @Override
    public void delete() {
        LOGGER.info("Начало процедуры удаления записей ЖКУ");
        CompletableFuture.runAsync(() -> {
                List<GkuInnEntity> inns;
                do {
                    inns = migrationGkuRepository.findAllOldValue(
                        PageRequest.of(0, batchSize)
                    );
                    migrationGkuRepository.deleteAll(inns);
                    LOGGER.info("При работе процедуры удаления записей ЖКУ, проведено удаление {} записей", inns.size());
                } while (!inns.isEmpty());
            },
            executorService
        );
        LOGGER.info("Окончание процедуры удаления записей ЖКУ");
    }
}
