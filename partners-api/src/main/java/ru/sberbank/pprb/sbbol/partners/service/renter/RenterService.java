package ru.sberbank.pprb.sbbol.partners.service.renter;

import ru.sberbank.pprb.sbbol.renter.model.Renter;
import ru.sberbank.pprb.sbbol.renter.model.RenterFilter;
import ru.sberbank.pprb.sbbol.renter.model.RenterIdentifier;
import ru.sberbank.pprb.sbbol.renter.model.RenterListResponse;

@Deprecated
public interface RenterService {

    /**
     * Получение списка арендаторов по заданному фильтру
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
     * Редактирование арендатора
     *
     * @param renter новые данные арендатора
     * @return арендатор
     */
    Renter updateRenter(Renter renter);

    /**
     * Получение арендатора по идентификатору договора
     *
     * @param renterIdentifier Идентификатор арендатора
     * @return арендатор
     */
    Renter getRenter(RenterIdentifier renterIdentifier);
}
