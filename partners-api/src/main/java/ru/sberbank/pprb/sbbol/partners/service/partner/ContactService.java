package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.model.Contact;
import ru.sberbank.pprb.sbbol.partners.model.ContactChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.ContactCreate;
import ru.sberbank.pprb.sbbol.partners.model.ContactCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.ContactsFilter;
import ru.sberbank.pprb.sbbol.partners.model.ContactsResponse;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Сервис по работе с контактами Партнера
 */
public interface ContactService {

    /**
     * Получение контакта Партнера
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param id        Идентификатор контакта
     * @return Контакт
     */
    Contact getContact(String digitalId, UUID id);

    /**
     * Получение контактов Партнера
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param partnerUuid Идентификатор партнера
     * @return Список контактов
     */
    List<Contact> getContactsByPartnerUuid(String digitalId, UUID partnerUuid);

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
    Contact saveContact(ContactCreate contact);

    /**
     * Создание новых контактов
     *
     * @param digitalId идентификатор цифрового кабинета клиента
     * @param unifiedUuid идентификатор владельца контакта
     * @param contacts список создаваемых контактов
     * @return Список созданных контактов
     */
    List<Contact> saveContacts(String digitalId, UUID unifiedUuid, Set<ContactCreateFullModel> contacts);

    /**
     * Обновление контакта Партнера
     *
     * @param contact новые данные контакта Партнера
     * @return Контакт
     */
    Contact updateContact(Contact contact);

    /**
     * Частичное обновление контакта Партнера
     *
     * @param contact новые данные контакта Партнера
     * @return Контакт
     */
    Contact patchContact(Contact contact);

    /**
     * Создание/частичное обновление контакта партнера
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param partnerId Идентификатор партнера
     * @param contact Коонтакт для создания/частичного обновления
     */
    void saveOrPatchContact(String digitalId, UUID partnerId, ContactChangeFullModel contact);

    /**
     * Создание/частичное обновление контактов партнера
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param partnerId Идентификатор партнера
     * @param contacts Список контактов для создания/частичного обновления
     */
    void saveOrPatchContacts(String digitalId, UUID partnerId, Set<ContactChangeFullModel> contacts);

    /**
     * Удаление контакта Партнера
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param ids       Идентификаторы контактов Партнера
     */
    void deleteContacts(String digitalId, List<UUID> ids);

    /**
     * Удаление контактов Партнера
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param partnerUuid Идентификатор Партнера
     */
    void deleteContactsByPartnerUuid(String digitalId, UUID partnerUuid);
}
