package ru.sberbank.pprb.sbbol.partners.service.ids.history;


import ru.sberbank.pprb.sbbol.partners.model.ExternalInternalIdLinksResponse;

import java.util.List;
import java.util.UUID;

public interface IdsHistoryService {

    /**
     * Добавление сущности историй id
     * @param digitalId идентификатор личного кабинета
     * @param externalIds список внешних идентификатор
     * @return Список внутренних идентификаторов
     */

    ExternalInternalIdLinksResponse getInternalIds(String digitalId, List<String> externalIds);

    /**
     * Добавление сущности историй id
     * @param digitalId идентификатор личного кабинета
     * @param externalId вешний идентификатор(передать pprbId, если сущность создана в ППРБ)
     * @param pprbId ППРБ идентификатор сущности
     */
    void add(String digitalId, UUID externalId, UUID pprbId);

    /**
     * Удаление истории id
     * @param digitalId идентификатор личного кабинета
     * @param pprbId ППРБ идентификатор сущности
     */
    void delete(String digitalId, UUID pprbId);

    /**
     * Удаление историй id
     * @param digitalId идентификатор личного кабинета
     * @param pprbIds список ППРБ идентификаторов сущностей
     */
    void delete(String digitalId, List<UUID> pprbIds);
}
