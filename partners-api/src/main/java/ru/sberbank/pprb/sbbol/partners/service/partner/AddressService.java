package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.model.Address;
import ru.sberbank.pprb.sbbol.partners.model.AddressCreate;
import ru.sberbank.pprb.sbbol.partners.model.AddressResponse;
import ru.sberbank.pprb.sbbol.partners.model.AddressesFilter;
import ru.sberbank.pprb.sbbol.partners.model.AddressesResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;

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
    AddressResponse getAddress(String digitalId, String id);

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
    AddressResponse saveAddress(AddressCreate address);

    /**
     * Обновление адреса
     *
     * @param address данные адреса
     * @return Адрес
     */
    AddressResponse updateAddress(Address address);

    /**
     * Удаление адреса
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param id        Идентификатор адреса Контакта
     */
    void deleteAddress(String digitalId, String id);
}
