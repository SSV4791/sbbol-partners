package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.model.FraudMetaData;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.PartnerFullModelResponse;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;
import ru.sberbank.pprb.sbbol.partners.model.PartnersResponse;

import java.util.List;
import java.util.UUID;

/**
 * Сервис по работе с Партнерами
 */
public interface PartnerService {

    /**
     * Получение Партнера
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param id        Идентификатор Партнера
     * @return Партнер
     */
    Partner getPartner(String digitalId, UUID id);

    /**
     * Получение списка Партнеров по заданному фильтру
     *
     * @param partnersFilter фильтр для поиска Партнеров
     * @return список Партнеров, удовлетворяющих заданному фильтру
     */
    PartnersResponse getPartners(PartnersFilter partnersFilter);

    /**
     * Создание нового Партнера со всеми дочерними сущностями
     *
     * @param partner данные Партнера
     * @return Партнер
     */
    PartnerFullModelResponse savePartner(PartnerCreateFullModel partner);

    /**
     * Создание нового Партнера
     *
     * @param partner данные Партнера
     * @return Партнер
     */
    Partner savePartner(PartnerCreate partner);

    /**
     * Обновление Партнера
     *
     * @param partner новые данные Партнера
     * @return Партнер
     */
    Partner patchPartner(Partner partner);

    /**
     * Частичное обновление Партнера со всеми дочерними сущностями
     *
     * @param partner данные Партнера
     * @return Партнер
     */
    PartnerFullModelResponse patchPartner(PartnerChangeFullModel partner);

    /**
     * Удаление Партнера
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param id        Идентификаторы Партнеров
     */
    void deletePartners(String digitalId, List<UUID> id, FraudMetaData fraudMetaData);
}
