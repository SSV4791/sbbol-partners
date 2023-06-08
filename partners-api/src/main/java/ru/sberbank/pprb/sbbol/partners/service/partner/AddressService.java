package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.model.Address;
import ru.sberbank.pprb.sbbol.partners.model.AddressChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.AddressCreate;
import ru.sberbank.pprb.sbbol.partners.model.AddressesFilter;
import ru.sberbank.pprb.sbbol.partners.model.AddressesResponse;

import java.util.List;
import java.util.Set;

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
     * Частичное обновление адреса (patch)
     *
     * @param address данные адреса
     * @return Адрес
     */
    Address patchAddress(Address address);

    /**
     * Создание/частичное обновление адреса партнера
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param partnerId Идентификатор партнера
     * @param address   Адрес для создания/частичного обновления
     */
    void saveOrPatchAddress(String digitalId, String partnerId, AddressChangeFullModel address);

    /**
     * Создание/частичное обновление адресов партнера
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param partnerId Идентификатор партнера
     * @param addresses Список адресов для создания/частичное обновление
     */
    void saveOrPatchAddresses(String digitalId, String partnerId, Set<AddressChangeFullModel> addresses);

    /**
     * Удаление адреса
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param ids       Идентификаторы адресов Контакта
     */
    void deleteAddresses(String digitalId, List<String> ids);
}
