package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;

import java.util.List;

public interface PartnerViewRepository {

    /**
     * Получение партнеров
     *
     * @param filter Фильтр для запроса партнеров
     * @return Партнеры
     */
    List<PartnerEntity> findByFilter(PartnersFilter filter);
}
