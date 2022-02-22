package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.EmailEntity;
import ru.sberbank.pprb.sbbol.partners.model.EmailsFilter;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EmailViewRepositoryImpl extends BaseRepository<EmailEntity, EmailsFilter> implements EmailViewRepository {

    public EmailViewRepositoryImpl(EntityManager entityManager) {
        super(entityManager, EmailEntity.class);
    }

    @Override
    public List<EmailEntity> findByFilter(EmailsFilter filter) {
        return filter(filter);
    }

    @Override
    void createPredicate(CriteriaBuilder builder, CriteriaQuery<EmailEntity> criteria, List<Predicate> predicates, Root<EmailEntity> root, EmailsFilter filter) {
        if (filter.getUnifiedIds() != null) {
            predicates.add(root.get("unifiedUuid").in(filter.getUnifiedIds().stream().map(UUID::fromString).collect(Collectors.toList())));
        }
    }

    @Override
    public List<Order> defaultOrder(CriteriaBuilder builder, Root<?> root) {
        return List.of(
            builder.desc(root.get("unifiedUuid")),
            builder.desc(root.get("uuid"))
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
