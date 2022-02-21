package ru.sberbank.pprb.sbbol.migration.correspondents.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sberbank.pprb.sbbol.migration.correspondents.entity.MigrationReplicationHistoryEntity;

import java.util.UUID;

@Repository
public interface MigrationReplicationHistoryRepository extends CrudRepository<MigrationReplicationHistoryEntity, UUID> {

    /**
     * Получить запись Истории репликации по идентификатору партёра из Legacy СББОЛ
     *
     * @param sbbolGuid идентификатор партёра из Legacy СББОЛ
     * @return запись Истории репликации
     */
    MigrationReplicationHistoryEntity getBySbbolGuid(String sbbolGuid);
}
