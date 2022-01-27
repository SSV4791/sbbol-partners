package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.BudgetMaskEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.BudgetMaskType;
import ru.sberbank.pprb.sbbol.partners.model.BudgetMaskFilter;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class BudgetMaskDictionaryViewRepositoryImpl implements BudgetMaskDictionaryViewRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<BudgetMaskEntity> findByFilter(BudgetMaskFilter filter) {
        var builder = entityManager.getCriteriaBuilder();
        var criteria = builder.createQuery(BudgetMaskEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        var root = criteria.from(BudgetMaskEntity.class);
        predicates.add(builder.equal(root.get("type"), BudgetMaskType.valueOf(filter.getMaskType().name())));
        criteria.select(root).where(builder.and(predicates.toArray(Predicate[]::new)));
        var query = entityManager.createQuery(criteria);
        return query.getResultList();
    }
}
