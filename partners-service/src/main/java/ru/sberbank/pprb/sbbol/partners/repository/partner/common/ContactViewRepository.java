package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactEntity;
import ru.sberbank.pprb.sbbol.partners.model.ContactsFilter;

import java.util.List;

public interface ContactViewRepository {

    /**
     * Получение контактов
     *
     * @param filter Фильтр для запроса контактов
     * @return Контакты
     */
    List<ContactEntity> findByFilter(ContactsFilter filter);
}
