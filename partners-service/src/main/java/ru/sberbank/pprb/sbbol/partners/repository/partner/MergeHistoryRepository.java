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
     * @param id        Идентификатор сущности
     * @param digitalId Идентификатор личного кабинета клиента
     * @return История слияния.
     */
    MergeHistoryEntity getByIdAndPartnerDigitalIdAndPartnerDeletedIsFalse(UUID id, String digitalId);
}
