package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentTypeEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentTypeEntity_;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentTypeLegalFormEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentTypeLegalFormEntity_;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeFilter;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class DocumentDictionaryViewRepositoryImpl
    extends BaseRepository<DocumentTypeEntity, DocumentTypeFilter> implements DocumentDictionaryViewRepository{

    protected DocumentDictionaryViewRepositoryImpl() {
        super(DocumentTypeEntity.class);
    }

    @Override
    void createPredicate(
        CriteriaBuilder builder,
        CriteriaQuery<DocumentTypeEntity> criteria,
        List<Predicate> predicates,
        Root<DocumentTypeEntity> root,
        DocumentTypeFilter filter
    ) {
        if (!CollectionUtils.isEmpty(filter.getLegalForms())) {
            Join<DocumentTypeEntity, DocumentTypeLegalFormEntity> legalForm = root.join(DocumentTypeEntity_.LEGAL_FORMS);
            predicates.add(
                legalForm.get(DocumentTypeLegalFormEntity_.LEGAL_FORM)
                    .in(filter.getLegalForms().stream().map(LegalForm::getValue).collect(toList())));
        }
        predicates.add(builder.equal(root.get(DocumentTypeEntity_.DELETED), filter.getDeleted()));
    }

    @Override
    List<Order> defaultOrder(CriteriaBuilder builder, Root<?> root) {
        return List.of(builder.desc(root.get(DocumentTypeEntity_.SYSTEM_NAME)));
    }

    @Override
    void pagination(TypedQuery<DocumentTypeEntity> query, DocumentTypeFilter filter) {
        if (filter.getPagination() != null) {
            var pagination = filter.getPagination();
            query.setFirstResult(pagination.getOffset());
            query.setMaxResults(pagination.getCount() + 1);
        }
    }

    @Override
    public List<DocumentTypeEntity> findByFilter(DocumentTypeFilter filter) {
        return filter(filter);
    }
}
