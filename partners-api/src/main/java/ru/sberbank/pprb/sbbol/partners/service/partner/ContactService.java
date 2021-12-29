package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.model.Contact;
import ru.sberbank.pprb.sbbol.partners.model.ContactResponse;
import ru.sberbank.pprb.sbbol.partners.model.ContactsFilter;
import ru.sberbank.pprb.sbbol.partners.model.ContactsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;

/**
 * Сервис по работе с контактами Партнера
 */
public interface ContactService {

    /**
     * Получение контакты Партнера
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param id        Идентификатор контакта
     * @return Контакт
     */
    ContactResponse getContact(String digitalId, String id);

    /**
     * Получение списка контактов партнеров по заданному фильтру
     *
     * @param contactsFilter фильтр для поиска контактов Партнера
     * @return список контактов партнера, удовлетворяющих заданному фильтру
     */
    ContactsResponse getContacts(ContactsFilter contactsFilter);

    /**
     * Создание нового контакта Партнера
     *
     * @param contact данные контакта Партнера
     * @return Контакт
     */
    ContactResponse saveContact(Contact contact);

    /**
     * Обновление контакта Партнера
     *
     * @param contact новые данные контакта Партнера
     * @return Контакт
     */
    ContactResponse updateContact(Contact contact);

    /**
     * Удаление контакта Партнера
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param id        Идентификатор контакта Партнера
     */
    Error deleteContact(String digitalId, String id);
}
