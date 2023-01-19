package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentEntity_;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentTypeEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentTypeEntity_;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsFilter;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class DocumentViewRepositoryImpl extends BaseRepository<DocumentEntity, DocumentsFilter> implements DocumentViewRepository {

    public DocumentViewRepositoryImpl(EntityManager entityManager) {
        super(entityManager, DocumentEntity.class);
    }

    @Override
    public List<DocumentEntity> findByFilter(DocumentsFilter filter) {
        return filter(filter);
    }

    @Override
    void createPredicate(
        CriteriaBuilder builder,
        CriteriaQuery<DocumentEntity> criteria,
        List<Predicate> predicates,
        Root<DocumentEntity> root,
        DocumentsFilter filter
    ) {
        predicates.add(builder.equal(root.get(DocumentEntity_.DIGITAL_ID), filter.getDigitalId()));
        inPredicate(builder, predicates, root, DocumentEntity_.UNIFIED_UUID, filter.getUnifiedIds());
        if (filter.getDocumentType() != null) {
            Join<DocumentEntity, DocumentTypeEntity> type = root.join(DocumentEntity_.TYPE);
            predicates.add(builder.equal(type.get(DocumentTypeEntity_.SYSTEM_NAME), (filter.getDocumentType())));
        }
    }

    @Override
    public List<Order> defaultOrder(CriteriaBuilder builder, Root<?> root) {
        return List.of(
            builder.desc(root.get(DocumentEntity_.DIGITAL_ID)),
            builder.desc(root.get(DocumentEntity_.UUID))
        );
    }

    @Override
    void pagination(TypedQuery<DocumentEntity> query, DocumentsFilter filter) {
        if (filter.getPagination() != null) {
            var pagination = filter.getPagination();
            query.setFirstResult(pagination.getOffset());
            query.setMaxResults(pagination.getCount() + 1);
        }
    }
}
