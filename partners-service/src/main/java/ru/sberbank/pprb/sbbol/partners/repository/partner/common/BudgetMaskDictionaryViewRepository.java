package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.BudgetMaskEntity;
import ru.sberbank.pprb.sbbol.partners.model.BudgetMaskFilter;

import java.util.List;

public interface BudgetMaskDictionaryViewRepository {

    /**
     * Получение масок бюджетности
     *
     * @param filter Фильтр для запроса масок
     * @return Маски
     */
    List<BudgetMaskEntity> findByFilter(BudgetMaskFilter filter);
}
