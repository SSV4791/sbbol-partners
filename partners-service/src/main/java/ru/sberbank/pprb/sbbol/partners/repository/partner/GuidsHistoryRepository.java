package ru.sberbank.pprb.sbbol.partners.repository.partner;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sberbank.pprb.sbbol.partners.entity.partner.IdsHistoryEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.ParentType;

import java.util.List;
import java.util.UUID;

@Repository
public interface GuidsHistoryRepository extends CrudRepository<IdsHistoryEntity, UUID> {

    /**
     * @param digitalId   идентификатор личного кабинета
     * @param parentType  тип родительской сущности
     * @param externalIds список внешних идентификаторов сущностей
     * @return список сущностей
     */
    List<IdsHistoryEntity> findByDigitalIdAndParentTypeAndExternalIdIn(String digitalId, ParentType parentType, List<UUID> externalIds);

    /**
     * @param digitalId    идентификатор личного кабинета
     * @param parentType   тип родительской сущности
     * @param pprbEntityId идентификатор искомой сущности
     * @return список сущностей
     */
    List<IdsHistoryEntity> findByDigitalIdAndParentTypeAndPprbEntityId(String digitalId, ParentType parentType, UUID pprbEntityId);

    /**
     * @param digitalId идентификатор личного кабинета
     * @return список сущностей
     */
    List<IdsHistoryEntity> findByDigitalId(String digitalId);
}
