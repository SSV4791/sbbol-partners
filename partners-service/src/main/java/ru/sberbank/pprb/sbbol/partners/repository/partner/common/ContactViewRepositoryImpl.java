package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactEntity;
import ru.sberbank.pprb.sbbol.partners.model.ContactsFilter;

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

public class ContactViewRepositoryImpl extends BaseRepository<ContactEntity, ContactsFilter> implements ContactViewRepository {

    public ContactViewRepositoryImpl(EntityManager entityManager) {
        super(entityManager, ContactEntity.class);
    }

    @Override
    public List<ContactEntity> findByFilter(ContactsFilter filter) {
        return filter(filter);
    }

    @Override
    void createPredicate(CriteriaBuilder builder, CriteriaQuery<ContactEntity> criteria, List<Predicate> predicates, Root<ContactEntity> root, ContactsFilter filter) {
        predicates.add(builder.equal(root.get("digitalId"), filter.getDigitalId()));
        predicates.add(builder.equal(root.get("partnerUuid"), UUID.fromString(filter.getPartnerId())));
        if (filter.getIds() != null) {
            predicates.add(root.get("uuid").in(filter.getIds().stream().map(UUID::fromString).collect(Collectors.toList())));
        }
    }

    @Override
    public List<Order> defaultOrder(CriteriaBuilder builder, Root<?> root) {
        return List.of(
            builder.desc(root.get("digitalId")),
            builder.desc(root.get("uuid"))
        );
    }

    @Override
    void pagination(TypedQuery<ContactEntity> query, ContactsFilter filter) {
        if (filter.getPagination() != null) {
            var pagination = filter.getPagination();
            query.setFirstResult(pagination.getOffset());
            query.setMaxResults(pagination.getCount() + 1);
        }
    }
}
