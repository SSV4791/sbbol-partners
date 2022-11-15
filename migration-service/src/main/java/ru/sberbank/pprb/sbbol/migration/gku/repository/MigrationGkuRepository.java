package ru.sberbank.pprb.sbbol.migration.gku.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.sberbank.pprb.sbbol.migration.gku.entity.MigrationGkuInnEntity;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface MigrationGkuRepository extends PagingAndSortingRepository<MigrationGkuInnEntity, UUID> {

    Optional<MigrationGkuInnEntity> getByInn(String inn);

    Page<MigrationGkuInnEntity> findAllByModifiedDateBefore(LocalDate lastModifiedDate, Pageable pageable);
}
