package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity_;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.PartnerType;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class RenterUpdaterRepositoryImpl extends BaseRepository<PartnerEntity, Pagination> implements RenterUpdaterRepository {

    public RenterUpdaterRepositoryImpl() {
        super(PartnerEntity.class);
    }

    @Override
    public List<PartnerEntity> findRenterByFilter(Pagination pagination) {
        return filter(pagination);
    }

    @Override
    void createPredicate(
        CriteriaBuilder builder,
        CriteriaQuery<PartnerEntity> criteria,
        List<Predicate> predicates,
        Root<PartnerEntity> root,
        Pagination pagination
    ) {
        predicates.add(builder.equal(root.get(PartnerEntity_.TYPE), PartnerType.RENTER));
    }

    @Override
    List<Order> defaultOrder(CriteriaBuilder builder, Root<?> root) {
        return List.of(
            builder.desc(root.get(PartnerEntity_.CREATE_DATE))
        );
    }

    @Override
    void pagination(TypedQuery<PartnerEntity> query, Pagination pagination) {
        if (pagination != null) {
            query.setFirstResult(pagination.getOffset());
            query.setMaxResults(pagination.getCount() + 1);
        }
    }
}
