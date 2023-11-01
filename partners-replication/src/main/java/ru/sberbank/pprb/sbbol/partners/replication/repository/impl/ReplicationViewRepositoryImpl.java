package ru.sberbank.pprb.sbbol.partners.replication.repository.impl;

import ru.sberbank.pprb.sbbol.partners.replication.entity.ReplicationEntity;
import ru.sberbank.pprb.sbbol.partners.replication.entity.ReplicationEntity_;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityStatus;
import ru.sberbank.pprb.sbbol.partners.replication.repository.ReplicationViewRepository;
import ru.sberbank.pprb.sbbol.partners.replication.repository.model.ReplicationFilter;

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

    protected ReplicationViewRepositoryImpl() {
        super(ReplicationEntity.class);
    }

    @Override
    void createPredicate(
        CriteriaBuilder builder,
        CriteriaQuery<ReplicationEntity> criteria,
        List<Predicate> predicates,
        Root<ReplicationEntity> root,
        ReplicationFilter filter
    ) {
        predicates.add(builder.notEqual(root.get(ReplicationEntity_.ENTITY_STATUS), ReplicationEntityStatus.SUCCESS));
        if (nonNull(filter.getSessionId())) {
            predicates.add(builder.or(
                builder.isNull(root.get(ReplicationEntity_.SESSION_ID)),
                builder.notEqual(root.get(ReplicationEntity_.SESSION_ID), filter.getSessionId())
            ));
        } else {
            predicates.add(builder.notEqual(root.get(ReplicationEntity_.SESSION_ID), filter.getSessionId()));
        }
        predicates.add(builder.le(root.get(ReplicationEntity_.RETRY), filter.getMaxRetry()));
    }

    @Override
    List<Order> defaultOrder(CriteriaBuilder builder, Root<?> root) {
        return List.of(
            builder.asc(root.get(ReplicationEntity_.LAST_MODIFIED_DATE))
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
