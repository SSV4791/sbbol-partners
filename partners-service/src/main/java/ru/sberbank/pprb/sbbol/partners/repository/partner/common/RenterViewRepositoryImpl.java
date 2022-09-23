package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.PartnerType;
import ru.sberbank.pprb.sbbol.renter.model.RenterFilter;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class RenterViewRepositoryImpl extends BaseRepository<PartnerEntity, RenterFilter> implements RenterViewRepository {

    public RenterViewRepositoryImpl(EntityManager entityManager) {
        super(entityManager, PartnerEntity.class);
    }

    @Override
    public List<PartnerEntity> findByFilter(RenterFilter filter) {
        return filter(filter);
    }

    @Override
    void createPredicate(
        CriteriaBuilder builder,
        CriteriaQuery<PartnerEntity> criteria,
        List<Predicate> predicates,
        Root<PartnerEntity> root,
        RenterFilter filter
    ) {
        predicates.add(builder.equal(root.get("digitalId"), filter.getDigitalId()));
        predicates.add(builder.equal(root.get("type"), PartnerType.RENTER));
    }

    @Override
    List<Order> defaultOrder(CriteriaBuilder builder, Root<?> root) {
        return List.of(
            builder.desc(root.get("digitalId")),
            builder.desc(root.get("uuid"))
        );
    }

    @Override
    void pagination(TypedQuery<PartnerEntity> query, RenterFilter filter) {
        var pagination = filter.getPagination();
        if (pagination != null) {
            query.setFirstResult(pagination.getOffset());
            query.setMaxResults(pagination.getCount() + 1);
        }
    }
}
