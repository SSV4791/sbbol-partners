package ru.sberbank.pprb.sbbol.partners;

import ru.sberbank.pprb.sbbol.partners.renter.model.Renter;
import ru.sberbank.pprb.sbbol.partners.renter.model.RenterFilter;
import ru.sberbank.pprb.sbbol.partners.renter.model.RenterListResponse;

public interface RenterService {

    /**
     * Получение списка договоров аренды по заданному фильтру
     *
     * @param renterFilter фильтр для поиска арендаторов
     * @return список арендаторов, удовлетворяющих заданному фильтру
     */
    RenterListResponse getRenters(RenterFilter renterFilter);

    /**
     * Создание нового арендатора
     *
     * @param renter данные арендатора
     * @return арендатор
     */
    Renter createRenter(Renter renter);

    /**
     * Редактирование договора
     *
     * @param renter новые данные арендатора
     * @return арендатор
     */
    Renter updateRenter(Renter renter);

    /**
     * Получение арендатора по идентификатору договора
     *
     * @param renterGuid Идентификатор арендатора
     * @return арендатор
     */
    Renter getRenter(String renterGuid);
}
