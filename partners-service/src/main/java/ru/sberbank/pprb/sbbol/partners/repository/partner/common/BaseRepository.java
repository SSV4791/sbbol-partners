package ru.sberbank.pprb.sbbol.partners.repository.partner.common;


import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public interface BaseRepository<T> {

    default Order defaultOrder(CriteriaBuilder builder, Root<?> root) {
        return builder.desc(root.get("digitalId"));
    }

    default void defaultCriteria(CriteriaQuery<T> criteria, CriteriaBuilder builder, Root<T> root) {
        criteria.orderBy(defaultOrder(builder, root));
    }

    default void defaultSelect(CriteriaQuery<T> criteria, Root<T> root, CriteriaBuilder builder, List<Predicate> predicates) {
        defaultCriteria(criteria, builder, root);
        criteria.select(root).where(builder.and(predicates.toArray(Predicate[]::new)));
    }
}
