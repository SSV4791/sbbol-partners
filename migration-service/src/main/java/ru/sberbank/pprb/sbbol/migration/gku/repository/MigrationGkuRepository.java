package ru.sberbank.pprb.sbbol.migration.gku.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sberbank.pprb.sbbol.migration.gku.repository.common.MigrationGkuDeleteRepository;
import ru.sberbank.pprb.sbbol.partners.entity.partner.GkuInnEntity;

import java.util.Optional;
import java.util.UUID;

public interface MigrationGkuRepository extends JpaRepository<GkuInnEntity, UUID>, MigrationGkuDeleteRepository {

    Optional<GkuInnEntity> getByInn(String inn);
}
