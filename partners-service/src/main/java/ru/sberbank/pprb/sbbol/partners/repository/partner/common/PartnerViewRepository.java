package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;
import ru.sberbank.pprb.sbbol.renter.model.RenterFilter;

import java.util.List;

public interface PartnerViewRepository {

    /**
     * Получение партнеров
     *
     * @param filter Фильтр для запроса партнеров
     * @return Партнеры
     */
    List<PartnerEntity> findByFilter(RenterFilter filter);

    /**
     * Получение партнеров
     *
     * @param filter Фильтр для запроса партнеров
     * @return Партнеры
     */
    List<PartnerEntity> findByFilter(PartnersFilter filter);
}
