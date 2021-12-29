package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.model.Address;
import ru.sberbank.pprb.sbbol.partners.model.AddressResponse;
import ru.sberbank.pprb.sbbol.partners.model.AddressesFilter;
import ru.sberbank.pprb.sbbol.partners.model.AddressesResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;

/**
 * Сервис по работе с адресами Партнера
 */
public interface PartnerAddressService {

    /**
     * Получение адресов Партнера
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param id        Идентификатор адреса
     * @return Контакт
     */
    AddressResponse getAddress(String digitalId, String id);

    /**
     * Получение списка адресов партнеров по заданному фильтру
     *
     * @param addressesFilter фильтр для поиска адресов Партнера
     * @return список адресов партнера, удовлетворяющих заданному фильтру
     */
    AddressesResponse getAddresses(AddressesFilter addressesFilter);

    /**
     * Создание нового адреса Партнера
     *
     * @param address данные адреса Партнера
     * @return Адрес
     */
    AddressResponse saveAddress(Address address);

    /**
     * Обновление адреса Партнера
     *
     * @param address данные адреса Партнера
     * @return Адрес
     */
    AddressResponse updateAddress(Address address);

    /**
     * Удаление адрес Партнера
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param id        Идентификатор адреса Партнера
     */
    Error deleteAddress(String digitalId, String id);
}
