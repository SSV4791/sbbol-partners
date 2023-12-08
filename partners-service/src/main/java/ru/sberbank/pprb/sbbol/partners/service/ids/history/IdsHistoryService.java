package ru.sberbank.pprb.sbbol.partners.service.ids.history;


import ru.sberbank.pprb.sbbol.partners.entity.partner.IdsHistoryEntity;
import ru.sberbank.pprb.sbbol.partners.model.ExternalInternalIdLinksResponse;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

public interface IdsHistoryService {

    List<UUID> getAccountByPprbUuid(@NotEmpty String digitalId, @NotEmpty UUID pprbUuid);

    /**
     * Получение историй id счетов
     *
     * @param digitalId   идентификатор личного кабинета
     * @param externalIds список внешних идентификатор
     * @return Список внутренних идентификаторов
     */
    ExternalInternalIdLinksResponse getAccountsInternalIds(
        @NotEmpty String digitalId,
        @NotEmpty List<UUID> externalIds
    );

    /**
     * Получение историй id партнеров
     *
     * @param digitalId   идентификатор личного кабинета
     * @param externalIds внешние идентификаторы
     * @return Список внутренних идентификаторов
     */
    ExternalInternalIdLinksResponse getPartnersInternalId(
        @NotEmpty String digitalId,
        @NotEmpty List<UUID> externalIds
    );

    /**
     * Получение сущности партнеров историй id
     *
     * @param externalId внешний идентификатор
     * @param internalId внутренний идентификатор
     * @return историчные идентификаторы
     */
    IdsHistoryEntity saveAccountIdLink(UUID externalId, UUID internalId, String digitalId);
}
