package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.renter.model.RenterFilter;

import java.util.List;

public interface RenterViewRepository {

    /**
     * Получение партнеров
     *
     * @param filter Фильтр для запроса партнеров
     * @return Партнеры
     */
    List<PartnerEntity> findByFilter(RenterFilter filter);
}
