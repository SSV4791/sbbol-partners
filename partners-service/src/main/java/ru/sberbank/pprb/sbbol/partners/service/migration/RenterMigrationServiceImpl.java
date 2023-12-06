package ru.sberbank.pprb.sbbol.partners.service.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;

import java.util.concurrent.CompletableFuture;

@Loggable
public class RenterMigrationServiceImpl implements RenterMigrationService {

    private static final Logger LOG = LoggerFactory.getLogger(RenterMigrationServiceImpl.class);

    @Override
    public void startMigration() {
        LOG.info("Запуск процедуры миграции рентеров");
        CompletableFuture
            .runAsync(() -> {

                //TODO migrationScript.run()

            }).exceptionally(ex -> {
                LOG.error("Ошибка в процедуре миграции рентеров = {}", ex.getMessage());
                return null;
            });
    }
}
