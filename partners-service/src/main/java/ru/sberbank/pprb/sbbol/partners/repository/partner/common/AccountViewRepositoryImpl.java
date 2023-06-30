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

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper.saveSearchString;

public class AccountViewRepositoryImpl
    extends BaseRepository<AccountEntity, AccountsFilter> implements AccountViewRepository {

    private final BudgetMaskDictionaryRepository budgetMaskDictionaryRepository;

    public AccountViewRepositoryImpl(
        EntityManager entityManager,
        BudgetMaskDictionaryRepository budgetMaskDictionaryRepository
    ) {
        super(entityManager, AccountEntity.class);
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
        addPartnerTypePredicate(builder, predicates, root);
        addStatePredicate(builder, predicates, root, filter);
        addSearchPredicate(builder, predicates, root, filter);
        addBudgetPredicate(builder, predicates, root, filter);
        addGkuPredicate(predicates, root, filter);
        addPartnerSearchPredicate(builder, predicates, root, filter);
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

    private void addPartnerTypePredicate(CriteriaBuilder builder, List<Predicate> predicates, Root<AccountEntity> root) {
        Join<AccountEntity, PartnerEntity> partner = root.join(AccountEntity_.PARTNER);
        predicates.add(builder.equal(partner.get(PartnerEntity_.TYPE), PartnerType.PARTNER));
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

    private void addGkuPredicate(List<Predicate> predicates, Root<AccountEntity> root, AccountsFilter filter) {
        if (Boolean.TRUE.equals(filter.getIsHousingServicesProvider())) {
            Join<AccountEntity, PartnerEntity> partner = root.join(AccountEntity_.PARTNER);
            Join<PartnerEntity, GkuInnEntity> gku = partner.join(PartnerEntity_.GKU_INN_ENTITY);
            predicates.add(gku.get(GkuInnEntity_.INN).isNotNull());
        }
    }

    private void addPartnerSearchPredicate(CriteriaBuilder builder, List<Predicate> predicates, Root<AccountEntity> root, AccountsFilter filter) {
        if (isNotEmpty(filter.getPartnerSearch())) {
            Join<AccountEntity, PartnerEntity> partner = root.join(AccountEntity_.PARTNER);
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

    @Override
    public List<Order> defaultOrder(CriteriaBuilder builder, Root<?> root) {
        return List.of(
            builder.desc(root.get(AccountEntity_.DIGITAL_ID)),
            builder.desc(root.get(AccountEntity_.CREATE_DATE))
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
