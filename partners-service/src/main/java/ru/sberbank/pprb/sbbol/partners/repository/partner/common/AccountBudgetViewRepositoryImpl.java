package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AccountBudgetViewRepositoryImpl implements AccountBudgetViewRepository {

    private final EntityManager entityManager;

    public AccountBudgetViewRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<AccountEntity> findBudgetAccounts(String digitalId, List<String> masksConditions) {
        var builder = entityManager.getCriteriaBuilder();
        var criteria = builder.createQuery(AccountEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        var root = criteria.from(AccountEntity.class);
        predicates.add(builder.equal(root.get("digitalId"), digitalId));
        List<Predicate> maskPredicates = new ArrayList<>();
        for (String mask : masksConditions) {
            maskPredicates.add(builder.or(builder.like(builder.upper(root.get("account")), mask.toUpperCase(Locale.getDefault()))));
        }
        predicates.add(builder.or(maskPredicates.toArray(Predicate[]::new)));
        criteria.orderBy(defaultOrder(builder, root));
        criteria.select(root).where(builder.and(predicates.toArray(Predicate[]::new)));
        var query = entityManager.createQuery(criteria);
        return query.getResultList();
    }

    private List<Order> defaultOrder(CriteriaBuilder builder, Root<?> root) {
        return List.of(
            builder.desc(root.get("digitalId")),
            builder.desc(root.get("uuid"))
        );
    }
}
