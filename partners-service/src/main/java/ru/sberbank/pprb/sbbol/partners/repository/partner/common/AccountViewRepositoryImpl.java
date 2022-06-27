package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BudgetMaskEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.GkuInnEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;
import ru.sberbank.pprb.sbbol.partners.repository.partner.BudgetMaskDictionaryRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

public class AccountViewRepositoryImpl extends BaseRepository<AccountEntity, AccountsFilter> implements AccountViewRepository {

    private static final String ACCOUNT_ATTRIBUTE = "account";
    private static final String PARTNER_ATTRIBUTE = "partner";
    private static final String PARTNER_UUID_ATTRIBUTE = "partnerUuid";

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
                builder.like(root.get(ACCOUNT_ATTRIBUTE), "%" + search.getSearch() + "%")
            );
        }
        if (filter.getPartnerIds() != null) {
            predicates.add(root.get(PARTNER_UUID_ATTRIBUTE).in(filter.getPartnerIds().stream().map(UUID::fromString).collect(Collectors.toSet())));
        }
        if (filter.getAccountIds() != null) {
            predicates.add(root.get("uuid").in(filter.getAccountIds().stream().map(UUID::fromString).collect(Collectors.toSet())));
        }
        if (isNotEmpty(filter.getState())) {
            predicates.add(builder.equal(root.get("state"), filter.getState()));
        }
        if (Boolean.TRUE.equals(filter.getIsBudget())) {
            var masks = budgetMaskDictionaryRepository.findAll();
            List<Predicate> maskPredicate = new ArrayList<>(masks.size());
            for (BudgetMaskEntity mask : masks) {
                maskPredicate.add(builder.or(builder.like(builder.upper(root.get(ACCOUNT_ATTRIBUTE)), mask.getCondition().toLowerCase(Locale.getDefault()))));
            }
            predicates.add(builder.or(maskPredicate.toArray(Predicate[]::new)));
        }
        if (Boolean.TRUE.equals(filter.getIsHousingServicesProvider())) {
            Join<AccountEntity, PartnerEntity> partner = root.join(PARTNER_ATTRIBUTE);
            Join<PartnerEntity, GkuInnEntity> gku = partner.join("gkuInnEntity");
            predicates.add(gku.get("inn").isNotNull());
        }
        if (isNotEmpty(filter.getPartnerSearch())) {
            Join<AccountEntity, PartnerEntity> partner = root.join(PARTNER_ATTRIBUTE);
            var expression1 = builder.concat(partner.get("orgName"), partner.get("secondName"));
            var expression2 = builder.concat(expression1, partner.get("firstName"));
            var expression3 = builder.concat(expression2, partner.get("middleName"));
            var expression4 = builder.concat(expression3, partner.get("inn"));
            var expression5 = builder.concat(expression4, partner.get("kpp"));
            var expression = builder.concat(expression5, root.get(ACCOUNT_ATTRIBUTE));
            predicates.add(builder.like(builder.upper(expression), "%" + filter.getPartnerSearch().toUpperCase() + "%"));
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
