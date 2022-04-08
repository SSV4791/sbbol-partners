package ru.sberbank.pprb.sbbol.migration.correspondents.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sberbank.pprb.sbbol.migration.correspondents.entity.MigrationPartnerEntity;

import java.util.UUID;

@Repository
public interface MigrationPartnerRepository extends CrudRepository<MigrationPartnerEntity, UUID> {

    MigrationPartnerEntity findByAccount_Uuid(UUID accountUuid);
}
