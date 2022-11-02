package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.PhoneEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PhoneEntity_;
import ru.sberbank.pprb.sbbol.partners.model.PhonesFilter;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PhoneViewRepositoryImpl extends BaseRepository<PhoneEntity, PhonesFilter> implements PhoneViewRepository {

    public PhoneViewRepositoryImpl(EntityManager entityManager) {
        super(entityManager, PhoneEntity.class);
    }

    @Override
    public List<PhoneEntity> findByFilter(PhonesFilter filter) {
        return filter(filter);
    }

    @Override
    void createPredicate(
        CriteriaBuilder builder,
        CriteriaQuery<PhoneEntity> criteria,
        List<Predicate> predicates,
        Root<PhoneEntity> root,
        PhonesFilter filter
    ) {
        predicates.add(builder.equal(root.get(PhoneEntity_.DIGITAL_ID), filter.getDigitalId()));
        if (filter.getUnifiedIds() != null) {
            predicates.add(
                root.get(PhoneEntity_.UNIFIED_UUID)
                    .in(filter.getUnifiedIds().stream().map(UUID::fromString).collect(Collectors.toList())));
        }
    }

    @Override
    public List<Order> defaultOrder(CriteriaBuilder builder, Root<?> root) {
        return List.of(
            builder.desc(root.get(PhoneEntity_.DIGITAL_ID)),
            builder.desc(root.get(PhoneEntity_.UNIFIED_UUID)),
            builder.desc(root.get(PhoneEntity_.UUID))
        );
    }

    @Override
    void pagination(TypedQuery<PhoneEntity> query, PhonesFilter filter) {
        if (filter.getPagination() != null) {
            var pagination = filter.getPagination();
            query.setFirstResult(pagination.getOffset());
            query.setMaxResults(pagination.getCount() + 1);
        }
    }
}
