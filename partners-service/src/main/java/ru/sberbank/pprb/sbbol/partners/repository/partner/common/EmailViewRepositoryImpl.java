package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.EmailEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.EmailEntity_;
import ru.sberbank.pprb.sbbol.partners.model.EmailsFilter;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class EmailViewRepositoryImpl extends BaseRepository<EmailEntity, EmailsFilter> implements EmailViewRepository {

    public EmailViewRepositoryImpl(EntityManager entityManager) {
        super(entityManager, EmailEntity.class);
    }

    @Override
    public List<EmailEntity> findByFilter(EmailsFilter filter) {
        return filter(filter);
    }

    @Override
    void createPredicate(
        CriteriaBuilder builder,
        CriteriaQuery<EmailEntity> criteria,
        List<Predicate> predicates,
        Root<EmailEntity> root,
        EmailsFilter filter
    ) {
        predicates.add(builder.equal(root.get(EmailEntity_.DIGITAL_ID), filter.getDigitalId()));
        inPredicate(builder, predicates, root, EmailEntity_.UNIFIED_UUID, filter.getUnifiedIds());
    }

    @Override
    public List<Order> defaultOrder(CriteriaBuilder builder, Root<?> root) {
        return List.of(
            builder.desc(root.get(EmailEntity_.UNIFIED_UUID))
        );
    }

    @Override
    void pagination(TypedQuery<EmailEntity> query, EmailsFilter filter) {
        if (filter.getPagination() != null) {
            var pagination = filter.getPagination();
            query.setFirstResult(pagination.getOffset());
            query.setMaxResults(pagination.getCount() + 1);
        }
    }
}
