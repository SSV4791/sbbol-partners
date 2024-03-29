package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity_;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankAccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankAccountEntity_;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity_;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BudgetMaskEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.GkuInnEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.GkuInnEntity_;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity_;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AccountStateType;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.BudgetMaskType;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.LegalType;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.PartnerType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerMapper;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.PartnerFilterType;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;
import ru.sberbank.pprb.sbbol.partners.model.SearchDateTime;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.BudgetMaskDictionaryRepository;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static ru.sberbank.pprb.sbbol.partners.entity.partner.enums.LegalType.ENTREPRENEUR;
import static ru.sberbank.pprb.sbbol.partners.entity.partner.enums.LegalType.LEGAL_ENTITY;
import static ru.sberbank.pprb.sbbol.partners.entity.partner.enums.LegalType.PHYSICAL_PERSON;

public class PartnerViewRepositoryImpl
    extends BaseRepository<PartnerEntity, PartnersFilter> implements PartnerViewRepository {

    private final AccountRepository accountRepository;
    private final BudgetMaskDictionaryRepository dictionaryRepository;
    private final PartnerMapper partnerMapper;

    public PartnerViewRepositoryImpl(
        AccountRepository accountRepository,
        BudgetMaskDictionaryRepository dictionaryRepository,
        PartnerMapper partnerMapper
    ) {
        super(PartnerEntity.class);
        this.accountRepository = accountRepository;
        this.dictionaryRepository = dictionaryRepository;
        this.partnerMapper = partnerMapper;
    }

    @Override
    public List<PartnerEntity> findByFilter(PartnersFilter filter) {
        return filter(filter);
    }

    @Override
    void createPredicate(
        CriteriaBuilder builder,
        CriteriaQuery<PartnerEntity> criteria,
        List<Predicate> predicates,
        Root<PartnerEntity> root,
        PartnersFilter filter
    ) {
        addDigitalIdPredicate(builder, predicates, root, filter);
        addIdsPredicate(builder, predicates, root, filter);
        addTypePredicate(builder, predicates, root);
        addSearchPredicate(builder, predicates, root, filter);
        addAccountSignPredicate(predicates, root, filter);
        addLegalFormPredicate(builder, predicates, root, filter);
        addPartnerFilterPredicate(builder, criteria, predicates, root, filter);
        addChangeDatePredicate(builder, predicates, root, filter);
    }

    private void addDigitalIdPredicate(CriteriaBuilder builder, List<Predicate> predicates, Root<PartnerEntity> root, PartnersFilter filter) {
        predicates.add(builder.equal(root.get(PartnerEntity_.DIGITAL_ID), filter.getDigitalId()));
    }

    private void addIdsPredicate(CriteriaBuilder builder, List<Predicate> predicates, Root<PartnerEntity> root, PartnersFilter filter) {
        inPredicate(builder, predicates, root, PartnerEntity_.UUID, filter.getIds());
    }

    private void addTypePredicate(CriteriaBuilder builder, List<Predicate> predicates, Root<PartnerEntity> root) {
        predicates.add(builder.equal(root.get(PartnerEntity_.TYPE), PartnerType.PARTNER));
    }

    private void addSearchPredicate(CriteriaBuilder builder, List<Predicate> predicates, Root<PartnerEntity> root, PartnersFilter filter) {
        var filterSearch = filter.getSearch();
        if (filterSearch != null && StringUtils.hasText(filterSearch.getSearch())) {
            var searchPattern = partnerMapper.saveSearchString(filterSearch.getSearch());
            predicates.add(
                builder.like(
                    builder.function("replace",
                        String.class,
                        builder.lower(root.get(PartnerEntity_.SEARCH)),
                        builder.literal(SPACE),
                        builder.literal(EMPTY)
                    ),
                    "%" + searchPattern + "%"
                )
            );
        }
    }

    private void addAccountSignPredicate(List<Predicate> predicates, Root<PartnerEntity> root, PartnersFilter filter) {
        if (filter.getAccountSignType() != null) {
            var accounts = switch (filter.getAccountSignType()) {
                case SIGNED ->
                    accountRepository.findByDigitalIdAndState(filter.getDigitalId(), AccountStateType.SIGNED);
                case NOT_SIGNED ->
                    accountRepository.findByDigitalIdAndState(filter.getDigitalId(), AccountStateType.NOT_SIGNED);
            };
            predicates.add(root.get(PartnerEntity_.UUID).in(accounts.stream().map(AccountEntity::getPartnerUuid).collect(Collectors.toList())));
        }
    }

    private void addLegalFormPredicate(CriteriaBuilder builder, List<Predicate> predicates, Root<PartnerEntity> root, PartnersFilter filter) {
        List<LegalForm> legalForms = filter.getLegalForms();
        if (!CollectionUtils.isEmpty(legalForms)) {
            List<Predicate> partnerLegalTypePredicate = new ArrayList<>(legalForms.size());
            for (LegalForm form : legalForms) {
                partnerLegalTypePredicate.add(
                    builder.or(builder.equal(root.get(PartnerEntity_.LEGAL_TYPE), LegalType.of(form)))
                );
            }
            predicates.add(builder.or(partnerLegalTypePredicate.toArray(Predicate[]::new)));
        }
    }

    @SuppressWarnings("java:S1120")
    private void addPartnerFilterPredicate(
        CriteriaBuilder builder,
        CriteriaQuery<PartnerEntity> criteria,
        List<Predicate> predicates,
        Root<PartnerEntity> root,
        PartnersFilter filter
    ) {
        PartnerFilterType partnerFilter = filter.getPartnersFilter();
        if (isNull(partnerFilter)) {
            return;
        }
        switch (partnerFilter) {
            case GKU -> addGkuPredicate(builder, predicates, root);
            case BUDGET -> addBudgetPredicate(builder, criteria, predicates, root);
            case ENTREPRENEUR -> predicates.add(builder.equal(root.get(PartnerEntity_.LEGAL_TYPE), ENTREPRENEUR));
            case PHYSICAL_PERSON -> predicates.add(builder.equal(root.get(PartnerEntity_.LEGAL_TYPE), PHYSICAL_PERSON));
            case LEGAL_ENTITY -> predicates.add(builder.equal(root.get(PartnerEntity_.LEGAL_TYPE), LEGAL_ENTITY));
        }
    }

    private void addChangeDatePredicate(CriteriaBuilder builder, List<Predicate> predicates, Root<PartnerEntity> root, PartnersFilter filter) {
        var filterChangeDate = filter.getChangeDate();
        if (filterChangeDate != null) {
            SearchDateTime.ConditionEnum condition = filterChangeDate.getCondition();
            predicates.add(
                switch (condition) {
                    case LESS ->
                        builder.lessThan(root.get(PartnerEntity_.LAST_MODIFIED_DATE), filterChangeDate.getDate());
                    default ->
                        builder.greaterThan(root.get(PartnerEntity_.LAST_MODIFIED_DATE), filterChangeDate.getDate());
                }
            );
        }
    }

    private void addGkuPredicate(CriteriaBuilder builder, List<Predicate> predicates, Root<PartnerEntity> root) {
        Join<PartnerEntity, GkuInnEntity> join = root.join(PartnerEntity_.GKU_INN_ENTITY, JoinType.INNER);
        predicates.add(builder.equal(root.get(PartnerEntity_.INN), join.get(GkuInnEntity_.INN)));
    }

    private void addBudgetPredicate(
        CriteriaBuilder builder,
        CriteriaQuery<PartnerEntity> criteria,
        List<Predicate> predicates,
        Root<PartnerEntity> root
    ) {
        Subquery<Integer> subQuery = criteria.subquery(Integer.class);
        Root<AccountEntity> accountRoot = subQuery.from(AccountEntity.class);
        Join<AccountEntity, BankEntity> bankJoin = accountRoot.join(AccountEntity_.BANK, JoinType.INNER);
        Join<BankEntity, BankAccountEntity> bankAccountJoin = bankJoin.join(BankEntity_.BANK_ACCOUNT, JoinType.LEFT);
        Predicate gisGmpPredicate = gisGmpPredicate(accountRoot, bankJoin, builder);
        Predicate taxAccountPredicate = taxAccountPredicate(accountRoot, builder);
        Predicate okrPredicate = okrPredicate(accountRoot, bankAccountJoin, builder);
        subQuery
            .select(builder.literal(1))
            .where(
                builder.and(
                    taxAccountPredicate.not(),
                    builder.or(
                        gisGmpPredicate,
                        okrPredicate
                    ),
                    builder.equal(accountRoot.get(AccountEntity_.PARTNER_UUID), root.get(PartnerEntity_.UUID))
                )
            );
        predicates.add(builder.exists(subQuery));
    }

    private Predicate gisGmpPredicate(
        Root<AccountEntity> accountRoot,
        Join<AccountEntity, BankEntity> bankJoin,
        CriteriaBuilder builder
    ) {
        List<BudgetMaskEntity> bicMasks = dictionaryRepository.findAllByType(BudgetMaskType.BIC);
        var maskBicPredicates = new ArrayList<Predicate>(bicMasks.size());
        for (var mask : bicMasks) {
            maskBicPredicates.add(
                builder.or(
                    builder.like(
                        builder.upper(bankJoin.get(BankEntity_.BIC)),
                        mask.getCondition().toUpperCase(Locale.getDefault()))));
        }
        List<BudgetMaskEntity> gisGmlMasks = dictionaryRepository.findAllByType(BudgetMaskType.GIS_GMP_ACCOUNT);
        var gisGmpAccountPredicates = new ArrayList<Predicate>(gisGmlMasks.size());
        for (var mask : gisGmlMasks) {
            gisGmpAccountPredicates.add(
                builder.or(
                    builder.like(
                        builder.upper(
                            accountRoot.get(AccountEntity_.ACCOUNT)),
                        mask.getCondition().toUpperCase(Locale.getDefault()))));
        }
        return builder.and(
            builder.or(maskBicPredicates.toArray(Predicate[]::new)),
            builder.or(gisGmpAccountPredicates.toArray(Predicate[]::new))
        );
    }

    private Predicate taxAccountPredicate(
        Root<AccountEntity> accountRoot,
        CriteriaBuilder builder
    ) {
        var taxAccountMasks = dictionaryRepository.findAllByType(BudgetMaskType.TAX_ACCOUNT_RECEIVER);
        var taxAccountPredicates = new ArrayList<Predicate>(taxAccountMasks.size());
        for (var mask : taxAccountMasks) {
            taxAccountPredicates.add(
                builder.or(
                    builder.like(
                        builder.upper(accountRoot.get(AccountEntity_.ACCOUNT)),
                        mask.getCondition().toUpperCase(Locale.getDefault()))));
        }
        return builder.or(taxAccountPredicates.toArray(Predicate[]::new));
    }

    private Predicate okrPredicate(
        Root<AccountEntity> accountRoot,
        Join<BankEntity, BankAccountEntity> bankAccountJoin,
        CriteriaBuilder builder
    ) {
        var accountMasks = dictionaryRepository.findAllByType(BudgetMaskType.BUDGET_ACCOUNT);
        var budgetAccountPredicates = new ArrayList<Predicate>(accountMasks.size());
        for (var mask : accountMasks) {
            budgetAccountPredicates.add(
                builder.or(
                    builder.like(
                        builder.upper(accountRoot.get(AccountEntity_.ACCOUNT)),
                        mask.getCondition().toUpperCase(Locale.getDefault()))));
        }
        var budgetCorrAccountMasks = dictionaryRepository.findAllByType(BudgetMaskType.BUDGET_CORR_ACCOUNT);
        var budgetCorAccountPredicates = new ArrayList<Predicate>(budgetCorrAccountMasks.size());
        for (var mask : budgetCorrAccountMasks) {
            budgetCorAccountPredicates.add(
                builder.or(
                    builder.like(
                        builder.upper(bankAccountJoin.get(BankAccountEntity_.ACCOUNT)),
                        mask.getCondition().toUpperCase(Locale.getDefault()))));
        }
        return builder.and(
            builder.or(budgetAccountPredicates.toArray(Predicate[]::new)),
            builder.or(budgetCorAccountPredicates.toArray(Predicate[]::new))
        );
    }

    @Override
    List<Order> defaultOrder(CriteriaBuilder builder, Root<?> root) {
        return List.of(
            builder.desc(root.get(PartnerEntity_.LAST_MODIFIED_DATE))
        );
    }

    @Override
    void pagination(TypedQuery<PartnerEntity> query, PartnersFilter filter) {
        var pagination = filter.getPagination();
        if (pagination != null) {
            query.setFirstResult(pagination.getOffset());
            query.setMaxResults(pagination.getCount() + 1);
        }
    }
}
