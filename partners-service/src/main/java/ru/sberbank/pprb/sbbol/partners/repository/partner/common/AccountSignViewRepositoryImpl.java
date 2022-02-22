package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignFilter;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AccountSignViewRepositoryImpl extends BaseRepository<AccountEntity, AccountsSignFilter> implements AccountSignViewRepository {

    public AccountSignViewRepositoryImpl(EntityManager entityManager) {
        super(entityManager, AccountEntity.class);
    }

    @Override
    public List<AccountEntity> findByFilter(AccountsSignFilter filter) {
        return filter(filter);
    }

    @Override
    void createPredicate(CriteriaBuilder builder, CriteriaQuery<AccountEntity> criteria, List<Predicate> predicates, Root<AccountEntity> root, AccountsSignFilter filter) {
        predicates.add(builder.equal(root.get("digitalId"), filter.getDigitalId()));
        if (filter.getPartnerId() != null) {
            predicates.add(builder.equal(root.get("partnerUuid"), UUID.fromString(filter.getPartnerId())));
        }
        if (!CollectionUtils.isEmpty(filter.getAccountsId())) {
            predicates.add(root.get("uuid").in(filter.getAccountsId().stream().map(UUID::fromString).collect(Collectors.toList())));
        }
    }

    @Override
    public List<Order> defaultOrder(CriteriaBuilder builder, Root<?> root) {
        return List.of(
            builder.desc(root.get("digitalId")),
            builder.desc(root.get("uuid"))
        );
    }

    @Override
    void pagination(TypedQuery<AccountEntity> query, AccountsSignFilter filter) {
        if (filter.getPagination() != null) {
            var pagination = filter.getPagination();
            query.setFirstResult(pagination.getOffset());
            query.setMaxResults(pagination.getCount() + 1);
        }
    }
}
