package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentTypeEntity;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsFilter;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DocumentViewRepositoryImpl implements DocumentViewRepository, BaseRepository<DocumentEntity> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<DocumentEntity> findByFilter(DocumentsFilter filter) {
        var builder = entityManager.getCriteriaBuilder();
        var criteria = builder.createQuery(DocumentEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        var root = criteria.from(DocumentEntity.class);
        predicates.add(builder.equal(root.get("digitalId"), filter.getDigitalId()));
        if (filter.getUnifiedIds() != null) {
            predicates.add(root.get("unifiedUuid").in(filter.getUnifiedIds().stream().map(UUID::fromString).collect(Collectors.toList())));
        }
        if (filter.getDocumentType() != null) {
            Join<DocumentEntity, DocumentTypeEntity> type = root.join("type");
            predicates.add(builder.equal(type.get("systemName"), (filter.getDocumentType())));
        }
        defaultSelect(criteria, root, builder, predicates);
        var query = entityManager.createQuery(criteria);
        var pagination = filter.getPagination();
        if (pagination != null) {
            query.setFirstResult(pagination.getOffset());
            query.setMaxResults(pagination.getCount());
        }
        return query.getResultList();
    }
}
