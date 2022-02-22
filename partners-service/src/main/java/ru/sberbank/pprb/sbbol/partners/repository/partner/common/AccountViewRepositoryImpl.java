package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BudgetMaskEntity;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;
import ru.sberbank.pprb.sbbol.partners.repository.partner.BudgetMaskDictionaryRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

public class AccountViewRepositoryImpl extends BaseRepository<AccountEntity, AccountsFilter> implements AccountViewRepository {

    private final BudgetMaskDictionaryRepository budgetMaskDictionaryRepository;

    public AccountViewRepositoryImpl(EntityManager entityManager, BudgetMaskDictionaryRepository budgetMaskDictionaryRepository) {
        super(entityManager, AccountEntity.class);
        this.budgetMaskDictionaryRepository = budgetMaskDictionaryRepository;
    }

    @Override
    public List<AccountEntity> findByFilter(AccountsFilter filter) {
        return filter(filter);
    }

    @Override
    void createPredicate(CriteriaBuilder builder, CriteriaQuery<AccountEntity> criteria, List<Predicate> predicates, Root<AccountEntity> root, AccountsFilter filter) {
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
    }

    @Override
    public List<Order> defaultOrder(CriteriaBuilder builder, Root<?> root) {
        return List.of(
            builder.desc(root.get("digitalId")),
            builder.desc(root.get("uuid"))
        );
    }

    @Override
    void pagination(TypedQuery<AccountEntity> query, AccountsFilter filter) {
        if (filter.getPagination() != null) {
            var pagination = filter.getPagination();
            query.setFirstResult(pagination.getOffset());
            query.setMaxResults(pagination.getCount() + 1);
        }
    }
}
