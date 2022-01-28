package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.renter.model.RenterFilter;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class PartnerViewRepositoryImpl implements PartnerViewRepository, BaseRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<PartnerEntity> findByFilter(RenterFilter filter) {
        var builder = entityManager.getCriteriaBuilder();
        var criteria = builder.createQuery(PartnerEntity.class);
        var root = criteria.from(PartnerEntity.class);
        List<Predicate> predicates = List.of(builder.equal(root.get("digitalId"), filter.getDigitalId()));
        defaultOrder(builder, root);
        criteria.select(root).where(builder.and(predicates.toArray(Predicate[]::new)));
        var query = entityManager.createQuery(criteria);
        var pagination = filter.getPagination();
        if (pagination != null) {
            query.setFirstResult(pagination.getOffset());
            query.setMaxResults(pagination.getCount());
        }
        return query.getResultList();
    }
}
