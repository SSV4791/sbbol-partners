package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import org.springframework.util.StringUtils;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity_;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BudgetMaskEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.GkuInnEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.GkuInnEntity_;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity_;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.PartnerType;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.SearchDateTime;
import ru.sberbank.pprb.sbbol.partners.repository.partner.BudgetMaskDictionaryRepository;

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

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper.saveSearchString;

public class AccountViewRepositoryImpl
    extends BaseRepository<AccountEntity, AccountsFilter> implements AccountViewRepository {

    private final BudgetMaskDictionaryRepository budgetMaskDictionaryRepository;

    public AccountViewRepositoryImpl(
        BudgetMaskDictionaryRepository budgetMaskDictionaryRepository
    ) {
        super(AccountEntity.class);
        this.budgetMaskDictionaryRepository = budgetMaskDictionaryRepository;
    }

    @Override
    public List<AccountEntity> findByFilter(AccountsFilter filter) {
        return filter(filter);
    }

    @Override
    void createPredicate(
        CriteriaBuilder builder,
        CriteriaQuery<AccountEntity> criteria,
        List<Predicate> predicates,
        Root<AccountEntity> root,
        AccountsFilter filter
    ) {
        addDigitalIdPredicate(builder, predicates, root, filter);
        addUuidPredicate(builder, predicates, root, filter);
        addPartnerUuidPredicate(builder, predicates, root, filter);
        addAccountTypePredicate(builder, predicates, root);
        if (isNotEmpty(filter.getPartnerSearch()) || Boolean.TRUE.equals(filter.getIsHousingServicesProvider())) {
            Join<AccountEntity, PartnerEntity> partner = root.join(AccountEntity_.PARTNER);
            addPartnerSearchPredicate(builder, predicates, root, partner, filter);
            addGkuPredicate(predicates, filter, partner);
        }
        addStatePredicate(builder, predicates, root, filter);
        addSearchPredicate(builder, predicates, root, filter);
        addChangeDatePredicate(builder, predicates, root, filter);
        addBudgetPredicate(builder, predicates, root, filter);
    }

    private void addDigitalIdPredicate(CriteriaBuilder builder, List<Predicate> predicates, Root<AccountEntity> root, AccountsFilter filter) {
        predicates.add(builder.equal(root.get(AccountEntity_.DIGITAL_ID), filter.getDigitalId()));
    }

    private void addUuidPredicate(CriteriaBuilder builder, List<Predicate> predicates, Root<AccountEntity> root, AccountsFilter filter) {
        inPredicate(builder, predicates, root, AccountEntity_.UUID, filter.getAccountIds());
    }

    private void addPartnerUuidPredicate(CriteriaBuilder builder, List<Predicate> predicates, Root<AccountEntity> root, AccountsFilter filter) {
        inPredicate(builder, predicates, root, AccountEntity_.PARTNER_UUID, filter.getPartnerIds());
    }

    //без join-a на таблицу Partner
    private void addAccountTypePredicate(CriteriaBuilder builder, List<Predicate> predicates, Root<AccountEntity> root) {
        predicates.add(builder.equal(root.get(AccountEntity_.PARTNER_TYPE), PartnerType.PARTNER));
    }

    private void addPartnerSearchPredicate(
        CriteriaBuilder builder,
        List<Predicate> predicates,
        Root<AccountEntity> root,
        Join<AccountEntity, PartnerEntity> partner,
        AccountsFilter filter
    ) {
        if (isNotEmpty(filter.getPartnerSearch())) {
            var expression = builder.concat(
                builder.coalesce(partner.get(PartnerEntity_.SEARCH), ""),
                builder.coalesce(root.get(AccountEntity_.ACCOUNT), "")
            );
            predicates.add(
                builder.like(
                    builder.upper(expression), "%" + filter.getPartnerSearch().toUpperCase(Locale.getDefault()) + "%")
            );
        }
    }

    private void addStatePredicate(CriteriaBuilder builder, List<Predicate> predicates, Root<AccountEntity> root, AccountsFilter filter) {
        if (isNotEmpty(filter.getState())) {
            predicates.add(builder.equal(root.get(AccountEntity_.STATE), filter.getState()));
        }
    }

    private void addSearchPredicate(CriteriaBuilder builder, List<Predicate> predicates, Root<AccountEntity> root, AccountsFilter filter) {
        var filterSearch = filter.getSearch();
        if (filterSearch != null && StringUtils.hasText(filterSearch.getSearch())) {
            var searchPattern = saveSearchString(filterSearch.getSearch())
                .toLowerCase(Locale.getDefault());
            predicates.add(
                builder.like(
                    builder.lower(root.get(AccountEntity_.SEARCH)),
                    "%" + searchPattern + "%"
                )

            );
        }
    }

    private void addChangeDatePredicate(CriteriaBuilder builder, List<Predicate> predicates, Root<AccountEntity> root, AccountsFilter filter) {
        var filterChangeDate = filter.getChangeDate();
        if (filterChangeDate != null) {
            SearchDateTime.ConditionEnum condition = filterChangeDate.getCondition();
            predicates.add(
                switch (condition) {
                    case LESS ->
                        builder.lessThan(root.get(AccountEntity_.LAST_MODIFIED_DATE), filterChangeDate.getDate());
                    default ->
                        builder.greaterThan(root.get(AccountEntity_.LAST_MODIFIED_DATE), filterChangeDate.getDate());
                }
            );
        }
    }

    private void addBudgetPredicate(CriteriaBuilder builder, List<Predicate> predicates, Root<AccountEntity> root, AccountsFilter filter) {
        if (Boolean.TRUE.equals(filter.getIsBudget())) {
            var masks = budgetMaskDictionaryRepository.findAll();
            List<Predicate> maskPredicate = new ArrayList<>(masks.size());
            for (BudgetMaskEntity mask : masks) {
                maskPredicate.add(
                    builder.or(
                        builder.like(
                            builder.upper(root.get(AccountEntity_.ACCOUNT)), mask.getCondition().toLowerCase(Locale.getDefault())
                        )
                    )
                );
            }
            predicates.add(builder.or(maskPredicate.toArray(Predicate[]::new)));
        }
    }

    private void addGkuPredicate(List<Predicate> predicates, AccountsFilter filter, Join<AccountEntity, PartnerEntity> partner) {
        if (Boolean.TRUE.equals(filter.getIsHousingServicesProvider())) {
            Join<PartnerEntity, GkuInnEntity> gku = partner.join(PartnerEntity_.GKU_INN_ENTITY);
            predicates.add(gku.get(GkuInnEntity_.INN).isNotNull());
        }
    }

    @Override
    public List<Order> defaultOrder(CriteriaBuilder builder, Root<?> root) {
        return List.of(
            builder.desc(root.get(AccountEntity_.LAST_MODIFIED_DATE))
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
