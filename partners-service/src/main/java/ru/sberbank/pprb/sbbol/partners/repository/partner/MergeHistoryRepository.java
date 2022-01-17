package ru.sberbank.pprb.sbbol.partners.repository.partner;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sberbank.pprb.sbbol.partners.entity.partner.MergeHistoryEntity;

import java.util.UUID;

@Repository
public interface MergeHistoryRepository extends CrudRepository<MergeHistoryEntity, UUID> {

    /**
     * Получение главного Id из истории слияния
     *
     * @param id Идентификатор сущности
     * @return История слияния.
     */
    MergeHistoryEntity getByPartnerUuid(UUID id);

    /**
     * Удаление записи из истории
     *
     * @param id Идентификатор базовой сущности
     */
    void deleteByMainUuid(UUID id);
}
