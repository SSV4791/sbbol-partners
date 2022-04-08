package ru.sberbank.pprb.sbbol.partners.repository.partner;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentEntity;
import ru.sberbank.pprb.sbbol.partners.repository.partner.common.DocumentViewRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentRepository extends CrudRepository<DocumentEntity, UUID>, DocumentViewRepository {

    /**
     * Получение документа Партнера
     *
     * @param digitalId Идентификатор личного кабинета
     * @param uuid      Идентификатор документа
     * @return документ Партнера/Контакта
     */
    Optional<DocumentEntity> getByDigitalIdAndUuid(String digitalId, UUID uuid);

    /**
     * Получение документов Партнера
     *
     * @param digitalId   Идентификатор личного кабинета
     * @param unifiedUuid Идентификатор партнера/контакта
     * @return документы Партнера/Контакта
     */
    List<DocumentEntity> findByDigitalIdAndUnifiedUuid(String digitalId, UUID unifiedUuid);
}
