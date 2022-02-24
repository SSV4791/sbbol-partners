package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.EmailResponse;
import ru.sberbank.pprb.sbbol.partners.model.EmailsFilter;
import ru.sberbank.pprb.sbbol.partners.model.EmailsResponse;

/**
 * Сервис по работе с Электронными адресами
 */
public interface EmailService {

    /**
     * Получение списка Электронных адресов по заданному фильтру
     *
     * @param emailsFilter фильтр для поиска Электронных адресов
     * @return список электронных адресов, удовлетворяющих заданному фильтру
     */
    EmailsResponse getEmails(EmailsFilter emailsFilter);

    /**
     * Создание нового Электронного адреса
     *
     * @param email электронный адрес
     * @return Электронный адрес
     */
    EmailResponse saveEmail(Email email);

    /**
     * Обновление Электронного адреса
     *
     * @param email электронный адрес
     * @return Электронный адрес
     */
    EmailResponse updateEmail(Email email);

    /**
     * Удаление Электронного адреса
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param id        Идентификатор документа
     */
    void deleteEmail(String digitalId, String id);
}
