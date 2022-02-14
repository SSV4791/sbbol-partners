package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BudgetMaskEntity;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignFilter;
import ru.sberbank.pprb.sbbol.partners.repository.partner.BudgetMaskDictionaryRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

public class AccountViewRepositoryImpl implements AccountViewRepository, BaseRepository<AccountEntity> {

    @PersistenceContext
    private EntityManager entityManager;

    private final BudgetMaskDictionaryRepository budgetMaskDictionaryRepository;

    public AccountViewRepositoryImpl(BudgetMaskDictionaryRepository budgetMaskDictionaryRepository) {
        this.budgetMaskDictionaryRepository = budgetMaskDictionaryRepository;
    }

    @Override
    public List<AccountEntity> findByFilter(AccountsFilter filter) {
        var builder = entityManager.getCriteriaBuilder();
        var criteria = builder.createQuery(AccountEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        var root = criteria.from(AccountEntity.class);
        predicates.add(builder.equal(root.get("digitalId"), filter.getDigitalId()));
        if (filter.getSearch() != null) {
            var search = filter.getSearch();
            predicates.add(
                builder.like(root.get("account"), "%" + search.getSearch() + "%")
            );
        }
        if (filter.getPartnerIds() != null) {
            predicates.add(root.get("partnerUuid").in(filter.getPartnerIds().stream().map(UUID::fromString).collect(Collectors.toList())));
        }
        if (filter.getIsBudget()) {
            var masks = budgetMaskDictionaryRepository.findAll();
            List<Predicate> maskPredicate = new ArrayList<>(masks.size());
            for (BudgetMaskEntity mask : masks) {
                maskPredicate.add(builder.or(builder.like(builder.upper(root.get("account")), mask.getCondition().toLowerCase(Locale.getDefault()))));
            }
            predicates.add(builder.or(maskPredicate.toArray(Predicate[]::new)));
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
        defaultSelect(criteria, root, builder, predicates);
        var query = entityManager.createQuery(criteria);
        var pagination = filter.getPagination();
        if (pagination != null) {
            query.setFirstResult(pagination.getOffset());
            query.setMaxResults(pagination.getCount());
        }
        return query.getResultList();
    }

    @Override
    public List<AccountEntity> findBudgetAccount(String digitalId, List<String> masksCondition) {
        var builder = entityManager.getCriteriaBuilder();
        var criteria = builder.createQuery(AccountEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        var root = criteria.from(AccountEntity.class);
        predicates.add(builder.equal(root.get("digitalId"), digitalId));
        List<Predicate> maskPredicate = new ArrayList<>();
        for (String mask : masksCondition) {
            maskPredicate.add(builder.or(builder.like(builder.upper(root.get("account")), mask.toLowerCase(Locale.getDefault()))));
        }
        predicates.add(builder.or(maskPredicate.toArray(Predicate[]::new)));
        defaultSelect(criteria, root, builder, predicates);
        var query = entityManager.createQuery(criteria);
        return query.getResultList();
    }
}
