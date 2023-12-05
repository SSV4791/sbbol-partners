package ru.sberbank.pprb.sbbol.partners.repository.partner;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sberbank.pprb.sbbol.partners.entity.partner.IdsHistoryEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.ParentType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GuidsHistoryRepository extends CrudRepository<IdsHistoryEntity, UUID> {

    /**
     * @param digitalId  идентификатор личного кабинета
     * @param externalId внешний идентификатор сущности
     * @return история id
     */
    Optional<IdsHistoryEntity> findByDigitalIdAndExternalId(String digitalId, UUID externalId);

    /**
     * @param digitalId   идентификатор личного кабинета
     * @param parentType  тип родительской сущности
     * @param externalIds список внешних идентификаторов сущностей
     * @return список сущностей
     */
    List<IdsHistoryEntity> findByDigitalIdAndParentTypeAndExternalIdIn(String digitalId, ParentType parentType, List<UUID> externalIds);
}
