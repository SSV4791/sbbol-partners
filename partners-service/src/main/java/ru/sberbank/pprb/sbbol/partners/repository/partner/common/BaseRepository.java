package ru.sberbank.pprb.sbbol.partners.repository.partner.common;


import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

public interface BaseRepository {

    default Order defaultOrder(CriteriaBuilder builder, Root<?> root) {
        return builder.desc(root.get("digitalId"));
    }
}
