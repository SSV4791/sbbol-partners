package ru.sberbank.pprb.sbbol.migration.gku.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.sberbank.pprb.sbbol.migration.gku.entity.MigrationGkuInnEntity;

import java.util.UUID;

public interface MigrationGkuRepository extends PagingAndSortingRepository<MigrationGkuInnEntity, UUID> {
}
