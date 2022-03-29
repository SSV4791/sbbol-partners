package ru.sberbank.pprb.sbbol.migration.gku.repository;

import org.springframework.data.repository.CrudRepository;
import ru.sberbank.pprb.sbbol.migration.gku.entity.MigrationGkuInnEntity;

import java.util.UUID;

public interface MigrationGkuRepository extends CrudRepository<MigrationGkuInnEntity, UUID> {
}
