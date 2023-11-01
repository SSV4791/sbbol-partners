package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import org.springframework.transaction.annotation.Transactional;
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
    @Transactional(readOnly = true)
    List<PartnerEntity> findByFilter(PartnersFilter filter);
}
