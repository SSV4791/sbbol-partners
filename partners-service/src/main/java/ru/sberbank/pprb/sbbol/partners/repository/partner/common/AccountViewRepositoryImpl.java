package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignFilter;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AccountViewRepositoryImpl implements AccountViewRepository, BaseRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<AccountEntity> findByFilter(AccountsFilter filter) {
        var builder = entityManager.getCriteriaBuilder();
        var criteria = builder.createQuery(AccountEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        var root = criteria.from(AccountEntity.class);
        predicates.add(builder.equal(root.get("digitalId"), filter.getDigitalId()));
        if (filter.getPartnerIds() != null) {
            predicates.add(root.get("partnerUuid").in(filter.getPartnerIds().stream().map(UUID::fromString).collect(Collectors.toList())));
        }
        criteria.orderBy(defaultOrder(builder, root));
        criteria.select(root).where(builder.and(predicates.toArray(Predicate[]::new)));
        var query = entityManager.createQuery(criteria);
        var pagination = filter.getPagination();
        if (pagination != null) {
            query.setFirstResult(pagination.getOffset());
            query.setMaxResults(pagination.getCount());
        }
        return query.getResultList();
    }

    @Override
    public List<AccountEntity> findByFilter(AccountsSignFilter filter) {
        var builder = entityManager.getCriteriaBuilder();
        var criteria = builder.createQuery(AccountEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        var root = criteria.from(AccountEntity.class);
        predicates.add(builder.equal(root.get("digitalId"), filter.getDigitalId()));
        if (filter.getPartnerId() != null) {
            predicates.add(builder.equal(root.get("partnerUuid"), UUID.fromString(filter.getPartnerId())));
        }
        if (!CollectionUtils.isEmpty(filter.getAccountsId())) {
            predicates.add(root.get("uuid").in(filter.getAccountsId().stream().map(UUID::fromString).collect(Collectors.toList())));
        }
        criteria.orderBy(defaultOrder(builder, root));
        criteria.select(root).where(builder.and(predicates.toArray(Predicate[]::new)));
        var query = entityManager.createQuery(criteria);
        var pagination = filter.getPagination();
        if (pagination != null) {
            query.setFirstResult(pagination.getOffset());
            query.setMaxResults(pagination.getCount());
        }
        return query.getResultList();
    }
}
