package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactEntity_;
import ru.sberbank.pprb.sbbol.partners.model.ContactsFilter;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class ContactViewRepositoryImpl extends BaseRepository<ContactEntity, ContactsFilter> implements ContactViewRepository {

    public ContactViewRepositoryImpl(EntityManager entityManager) {
        super(entityManager, ContactEntity.class);
    }

    @Override
    public List<ContactEntity> findByFilter(ContactsFilter filter) {
        return filter(filter);
    }

    @Override
    void createPredicate(
        CriteriaBuilder builder,
        CriteriaQuery<ContactEntity> criteria,
        List<Predicate> predicates,
        Root<ContactEntity> root,
        ContactsFilter filter
    ) {
        predicates.add(builder.equal(root.get(ContactEntity_.DIGITAL_ID), filter.getDigitalId()));
        predicates.add(builder.equal(root.get(ContactEntity_.PARTNER_UUID), filter.getPartnerId()));
        inPredicate(builder, predicates, root, ContactEntity_.UUID, filter.getIds());
    }

    @Override
    public List<Order> defaultOrder(CriteriaBuilder builder, Root<?> root) {
        return List.of(
            builder.desc(root.get(ContactEntity_.DIGITAL_ID)),
            builder.desc(root.get(ContactEntity_.UUID))
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
