package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactEntity;
import ru.sberbank.pprb.sbbol.partners.model.ContactsFilter;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ContactViewRepositoryImpl implements ContactViewRepository, BaseRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ContactEntity> findByFilter(ContactsFilter filter) {
        var builder = entityManager.getCriteriaBuilder();
        var criteria = builder.createQuery(ContactEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        var root = criteria.from(ContactEntity.class);
        predicates.add(builder.equal(root.get("digitalId"), filter.getDigitalId()));
        predicates.add(builder.equal(root.get("partnerUuid"), UUID.fromString(filter.getPartnerUuid())));
        if (filter.getUuid() != null) {
            predicates.add(root.get("id").in(filter.getUuid().stream().map(UUID::fromString).collect(Collectors.toList())));
        }
        criteria.orderBy(defaultOrder(builder, root));
        criteria.select(root).where(builder.and(predicates.toArray(new Predicate[0])));
        var query = entityManager.createQuery(criteria);
        var pagination = filter.getPagination();
        if (pagination != null) {
            query.setFirstResult(pagination.getOffset());
            query.setMaxResults(pagination.getCount());
        }
        return query.getResultList();
    }
}
