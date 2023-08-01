package ru.sberbank.pprb.sbbol.partners.service.ids.history;


import ru.sberbank.pprb.sbbol.partners.model.ExternalInternalIdLinksResponse;

import java.util.List;
import java.util.UUID;

public interface IdsHistoryService {

    /**
     * Получение сущности историй id
     *
     * @param digitalId   идентификатор личного кабинета
     * @param externalIds список внешних идентификатор
     * @return Список внутренних идентификаторов
     */

    ExternalInternalIdLinksResponse getInternalIds(String digitalId, List<String> externalIds);

    /**
     * Добавление сущности историй id
     *
     * @param digitalId  идентификатор личного кабинета
     * @param externalUuid вешний идентификатор(передать pprbUuid, если сущность создана в ППРБ)
     * @param pprbUuid     ППРБ идентификатор сущности
     */
    void create(String digitalId, UUID externalUuid, UUID pprbUuid);

    /**
     * Удаление истории id
     *
     * @param digitalId идентификатор личного кабинета
     * @param pprbUuid    ППРБ идентификатор сущности
     */
    void delete(String digitalId, UUID pprbUuid);

    /**
     * Удаление историй id
     *
     * @param digitalId идентификатор личного кабинета
     * @param pprbUuids   список ППРБ идентификаторов сущностей
     */
    void delete(String digitalId, List<UUID> pprbUuids);
}
