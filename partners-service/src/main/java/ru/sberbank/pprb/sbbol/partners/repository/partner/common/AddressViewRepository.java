package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.AddressEntity;
import ru.sberbank.pprb.sbbol.partners.model.AddressesFilter;

import java.util.List;

public interface AddressViewRepository {

    /**
     * Получение адресов
     *
     * @param filter Фильтр для запроса адресов
     * @return Адреса
     */
    List<AddressEntity> findByFilter(AddressesFilter filter);
}
