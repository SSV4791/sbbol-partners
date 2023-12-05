package ru.sberbank.pprb.sbbol.partners.service.ids.history;


import ru.sberbank.pprb.sbbol.partners.model.ExternalInternalIdLinksResponse;

import javax.validation.constraints.NotEmpty;
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
    ExternalInternalIdLinksResponse getAccountInternalIds(
        @NotEmpty String digitalId,
        @NotEmpty List<UUID> externalIds
    );
}
