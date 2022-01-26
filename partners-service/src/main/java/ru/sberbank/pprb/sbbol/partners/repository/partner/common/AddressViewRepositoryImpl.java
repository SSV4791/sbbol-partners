package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.AddressEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AddressType;
import ru.sberbank.pprb.sbbol.partners.model.AddressesFilter;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AddressViewRepositoryImpl implements AddressViewRepository, BaseRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<AddressEntity> findByFilter(AddressesFilter filter) {
        var builder = entityManager.getCriteriaBuilder();
        var criteria = builder.createQuery(AddressEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        var root = criteria.from(AddressEntity.class);
        predicates.add(builder.equal(root.get("digitalId"), filter.getDigitalId()));
        if (filter.getUnifiedIds() != null) {
            predicates.add(root.get("unifiedUuid").in(filter.getUnifiedIds().stream().map(UUID::fromString).collect(Collectors.toList())));
        }
        if (filter.getType() != null) {
            predicates.add(builder.equal(root.get("type"), AddressType.valueOf(filter.getType())));
        }
        criteria.orderBy(defaultOrder(builder, root));
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
