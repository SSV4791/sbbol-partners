package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.model.PhoneCreate;
import ru.sberbank.pprb.sbbol.partners.model.PhonesFilter;
import ru.sberbank.pprb.sbbol.partners.model.PhonesResponse;

import java.util.List;

/**
 * Сервис по работе с Телефонами
 */
public interface PhoneService {

    /**
     * Получение списка телефонов по заданному фильтру
     *
     * @param phonesFilter фильтр для поиска телефонов
     * @return список телефонов, удовлетворяющих заданному фильтру
     */
    PhonesResponse getPhones(PhonesFilter phonesFilter);

    /**
     * Создание нового телефона
     *
     * @param phone телефон
     * @return Телефон
     */
    Phone savePhone(PhoneCreate phone);

    /**
     * Обновление Телефона
     *
     * @param phone телефон
     * @return Телефон
     */
    Phone updatePhone(Phone phone);

    /**
     * Удаление Телефона
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param ids       Идентификаторы документов
     */
    void deletePhones(String digitalId, List<String> ids);
}
