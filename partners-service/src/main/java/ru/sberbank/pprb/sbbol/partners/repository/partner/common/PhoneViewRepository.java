package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.PhoneEntity;
import ru.sberbank.pprb.sbbol.partners.model.PhonesFilter;

import java.util.List;

public interface PhoneViewRepository {

    /**
     * Получение телефонов
     *
     * @param filter Фильтр для запроса телефонов
     * @return Телефоны
     */
    List<PhoneEntity> findByFilter(PhonesFilter filter);
}
