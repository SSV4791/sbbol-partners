package ru.sberbank.pprb.sbbol.partners.storage;

import org.springframework.cache.annotation.Cacheable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BudgetMaskEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.BudgetMaskType;
import ru.sberbank.pprb.sbbol.partners.model.BudgetMaskFilter;
import ru.sberbank.pprb.sbbol.partners.repository.partner.BudgetMaskDictionaryRepository;

import java.util.List;

import static ru.sberbank.pprb.sbbol.partners.storage.CacheNames.GET_BUDGET_MASKS_BY_TYPE;

public class BudgetMaskCacheableStorage {

    private final BudgetMaskDictionaryRepository budgetMaskDictionaryRepository;

    public BudgetMaskCacheableStorage(BudgetMaskDictionaryRepository budgetMaskDictionaryRepository) {
        this.budgetMaskDictionaryRepository = budgetMaskDictionaryRepository;
    }

    @Cacheable(GET_BUDGET_MASKS_BY_TYPE)
    public List<BudgetMaskEntity> findAllByType(BudgetMaskType type) {
        return budgetMaskDictionaryRepository.findAllByType(type);
    }

    public List<BudgetMaskEntity> findByFilter(BudgetMaskFilter filter) {
        return budgetMaskDictionaryRepository.findByFilter(filter);
    }
}
