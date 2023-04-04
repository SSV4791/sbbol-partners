package ru.sberbank.pprb.sbbol.migration.gku.repository.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import ru.sberbank.pprb.sbbol.partners.entity.partner.GkuInnEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.GkuInnEntity_;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Root;
import java.time.OffsetDateTime;
import java.util.List;

public class MigrationGkuDeleteRepositoryImpl implements MigrationGkuDeleteRepository {

    @Value("${migrate.gku.date_before:1}")
    private int dayBefore;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<GkuInnEntity> findAllOldValue(PageRequest pageable) {
        var builder = entityManager.getCriteriaBuilder();
        var criteria = builder.createQuery(GkuInnEntity.class);
        Root<GkuInnEntity> root = criteria.from(GkuInnEntity.class);
        OffsetDateTime now = OffsetDateTime.now()
            .minusDays(dayBefore);
        criteria.where(builder.lessThan(root.get(GkuInnEntity_.LAST_MODIFIED_DATE), now));
        criteria.orderBy(builder.desc(root.get(GkuInnEntity_.INN)));
        var query = entityManager.createQuery(criteria);
        if (pageable != null) {
            query.setFirstResult(pageable.getPageNumber());
            query.setMaxResults(pageable.getPageSize());
        }
        return query.getResultList();
    }
}
