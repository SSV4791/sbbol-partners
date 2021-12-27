package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerResponse;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;
import ru.sberbank.pprb.sbbol.partners.model.PartnersResponse;

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
    PartnerResponse getPartner(String digitalId, String id);


    /**
     * Получение списка Партнеров по заданному фильтру
     *
     * @param partnersFilter фильтр для поиска Партнеров
     * @return список Партнеров, удовлетворяющих заданному фильтру
     */
    PartnersResponse getPartners(PartnersFilter partnersFilter);

    /**
     * Создание нового Партнера
     *
     * @param partner данные Партнера
     * @return Партнер
     */
    PartnerResponse savePartner(Partner partner);

    /**
     * Обновление Партнера
     *
     * @param partner новые данные Партнера
     * @return Партнер
     */
    PartnerResponse updatePartner(Partner partner);

    /**
     * Удаление Партнера
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param id        Идентификатор Партнера
     * @return Партнер
     */
    Error deletePartner(String digitalId, String id);
}
