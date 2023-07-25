package ru.sberbank.pprb.sbbol.partners.repository.partner;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sberbank.pprb.sbbol.partners.entity.partner.GuidsHistoryEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GuidsHistoryRepository extends CrudRepository<GuidsHistoryEntity, UUID> {

    /**
     * @param digitalId  идентификатор личного кабинета
     * @param externalId внешний идентификатор сущности
     * @return история id
     */
    Optional<GuidsHistoryEntity> findByDigitalIdAndExternalId(String digitalId, UUID externalId);

    /**
     * @param digitalId   идентификатор личного кабинета
     * @param externalIds список внешних идентификаторов сущностей
     * @return список сущностей
     */
    List<GuidsHistoryEntity> findByDigitalIdAndExternalIdIn(String digitalId, List<UUID> externalIds);

    /**
     * @param digitalId    идентификатор личного кабинета
     * @param pprbEntityId идентификатор сущности в ППРБ
     * @return список сущностей
     */
    List<GuidsHistoryEntity> findByDigitalIdAndPprbEntityId(String digitalId, UUID pprbEntityId);
}
