package ru.sberbank.pprb.sbbol.partners.repository.partner.common;


import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

abstract class BaseRepository<T, F> {

    @PersistenceContext
    private EntityManager entityManager;
    private final Class<T> clazz;

    protected BaseRepository(Class<T> clazz) {
        this.clazz = clazz;
    }

    protected List<T> filter(F filter) {
        var builder = entityManager.getCriteriaBuilder();
        var criteria = builder.createQuery(clazz);
        List<Predicate> predicates = new ArrayList<>();
        var root = criteria.from(clazz);
        createPredicate(builder, criteria, predicates, root, filter);
        return defaultQuery(criteria, root, builder, predicates, filter);
    }

    abstract void createPredicate(CriteriaBuilder builder, CriteriaQuery<T> criteria, List<Predicate> predicates, Root<T> root, F filter);

    protected List<T> defaultQuery(CriteriaQuery<T> criteria, Root<T> root, CriteriaBuilder builder, List<Predicate> predicates, F filter) {
        defaultSelect(criteria, root, builder, predicates);
        var query = entityManager.createQuery(criteria);
        pagination(query, filter);
        return query.getResultList();
    }

    protected void defaultSelect(CriteriaQuery<T> criteria, Root<T> root, CriteriaBuilder builder, List<Predicate> predicates) {
        defaultCriteria(criteria, builder, root);
        criteria.select(root).where(builder.and(predicates.toArray(Predicate[]::new)));
    }

    protected void defaultCriteria(CriteriaQuery<T> criteria, CriteriaBuilder builder, Root<T> root) {
        criteria.orderBy(defaultOrder(builder, root));
    }

    abstract List<Order> defaultOrder(CriteriaBuilder builder, Root<?> root);

    abstract void pagination(TypedQuery<T> query, F filter);

    protected void inPredicate(CriteriaBuilder builder, List<Predicate> predicates, Root<?> root, String field, List<UUID> ids) {
        if (!CollectionUtils.isEmpty(ids)) {
            if (ids.size() == 1) {
                predicates.add(builder.equal(root.get(field), ids.get(0)));
            } else {
                predicates.add(
                    root.get(field).in(ids)
                );
            }
        }
    }
}
