package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.AddressEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AddressEntity_;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AddressType;
import ru.sberbank.pprb.sbbol.partners.model.AddressesFilter;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class AddressViewRepositoryImpl extends BaseRepository<AddressEntity, AddressesFilter> implements AddressViewRepository {

    public AddressViewRepositoryImpl(EntityManager entityManager) {
        super(entityManager, AddressEntity.class);
    }

    @Override
    public List<AddressEntity> findByFilter(AddressesFilter filter) {
        return filter(filter);
    }

    @Override
    void createPredicate(
        CriteriaBuilder builder,
        CriteriaQuery<AddressEntity> criteria,
        List<Predicate> predicates,
        Root<AddressEntity> root,
        AddressesFilter filter
    ) {
        predicates.add(builder.equal(root.get(AddressEntity_.DIGITAL_ID), filter.getDigitalId()));
        inPredicate(builder, predicates, root, AddressEntity_.UNIFIED_UUID, filter.getUnifiedIds());
        if (filter.getType() != null) {
            predicates.add(builder.equal(root.get(AddressEntity_.TYPE), AddressType.valueOf(filter.getType().getValue())));
        }
    }

    @Override
    public List<Order> defaultOrder(CriteriaBuilder builder, Root<?> root) {
        return List.of(
            builder.desc(root.get(AddressEntity_.UUID))
        );
    }

    @Override
    void pagination(TypedQuery<AddressEntity> query, AddressesFilter filter) {
        if (filter.getPagination() != null) {
            var pagination = filter.getPagination();
            query.setFirstResult(pagination.getOffset());
            query.setMaxResults(pagination.getCount() + 1);
        }
    }
}
