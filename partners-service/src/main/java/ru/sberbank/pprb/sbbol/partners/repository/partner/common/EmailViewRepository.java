package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.EmailEntity;
import ru.sberbank.pprb.sbbol.partners.model.EmailsFilter;

import java.util.List;

public interface EmailViewRepository {

    /**
     * Получение электронных адресов
     *
     * @param filter Фильтр для запроса электронных адресов
     * @return Электронные адреса
     */
    List<EmailEntity> findByFilter(EmailsFilter filter);
}
