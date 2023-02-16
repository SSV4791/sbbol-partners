package ru.sberbank.pprb.sbbol.partners.replication.repository.impl;

import ru.sberbank.pprb.sbbol.partners.replication.entity.ReplicationEntity;
import ru.sberbank.pprb.sbbol.partners.replication.entity.ReplicationEntity_;
import ru.sberbank.pprb.sbbol.partners.replication.repository.ReplicationViewRepository;
import ru.sberbank.pprb.sbbol.partners.replication.repository.model.ReplicationFilter;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

import static java.util.Objects.nonNull;

public class ReplicationViewRepositoryImpl
    extends BaseRepository<ReplicationEntity, ReplicationFilter>
    implements ReplicationViewRepository {

    protected ReplicationViewRepositoryImpl(EntityManager entityManager) {
        super(entityManager, ReplicationEntity.class);
    }

    @Override
    void createPredicate(
        CriteriaBuilder builder,
        CriteriaQuery<ReplicationEntity> criteria,
        List<Predicate> predicates,
        Root<ReplicationEntity> root,
        ReplicationFilter filter
    ) {
        if (nonNull(filter.getDigitalId())) {
            predicates.add(builder.equal(root.get(ReplicationEntity_.DIGITAL_ID), filter.getDigitalId()));
        }
        if (nonNull(filter.getEntityId())) {
            predicates.add(builder.equal(root.get(ReplicationEntity_.ENTITY_ID), filter.getEntityId()));
        }
        if (nonNull(filter.getEntityType())) {
            predicates.add(builder.equal(root.get(ReplicationEntity_.ENTITY_TYPE), filter.getEntityType()));
        }
        if (nonNull(filter.getEntityStatus())) {
            predicates.add(builder.equal(root.get(ReplicationEntity_.ENTITY_STATUS), filter.getEntityStatus()));
        }
        predicates.add(builder.equal(root.get(ReplicationEntity_.RETRY), filter.getPartition()));
    }

    @Override
    List<Order> defaultOrder(CriteriaBuilder builder, Root<?> root) {
        return List.of(
            builder.asc(root.get(ReplicationEntity_.CREATE_DATE))
        );
    }

    @Override
    void pagination(TypedQuery<ReplicationEntity> query, ReplicationFilter filter) {
        if (filter.getPagination() != null) {
            var pagination = filter.getPagination();
            query.setFirstResult(pagination.getOffset());
            query.setMaxResults(pagination.getCount());
        }
    }

    @Override
    public List<ReplicationEntity> findByFilter(ReplicationFilter filter) {
        return filter(filter);
    }
}
