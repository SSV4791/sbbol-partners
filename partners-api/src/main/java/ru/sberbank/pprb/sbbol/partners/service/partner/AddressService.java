package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.model.Address;
import ru.sberbank.pprb.sbbol.partners.model.AddressCreate;
import ru.sberbank.pprb.sbbol.partners.model.AddressesFilter;
import ru.sberbank.pprb.sbbol.partners.model.AddressesResponse;

import java.util.List;

/**
 * Сервис по работе с адресами
 */
public interface AddressService {

    /**
     * Получение адреса
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param id        Идентификатор документа
     * @return Адрес
     */
    Address getAddress(String digitalId, String id);

    /**
     * Получение списка адресов по заданному фильтру
     *
     * @param addressesFilter фильтр для поиска адресов
     * @return список адресов, удовлетворяющих заданному фильтру
     */
    AddressesResponse getAddresses(AddressesFilter addressesFilter);

    /**
     * Создание нового адреса
     *
     * @param address данные адреса
     * @return Адрес
     */
    Address saveAddress(AddressCreate address);

    /**
     * Обновление адреса
     *
     * @param address данные адреса
     * @return Адрес
     */
    Address updateAddress(Address address);

    /**
     * Удаление адреса
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param ids       Идентификаторы адресов Контакта
     */
    void deleteAddresses(String digitalId, List<String> ids);
}
