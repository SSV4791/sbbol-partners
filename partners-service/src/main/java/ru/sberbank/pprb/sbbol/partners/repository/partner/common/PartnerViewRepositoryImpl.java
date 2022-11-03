package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import org.springframework.util.StringUtils;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BudgetMaskEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.GkuInnEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.GkuInnEntity_;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity_;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AccountStateType;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.LegalType;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.PartnerType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerMapper;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.BudgetMaskDictionaryRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class PartnerViewRepositoryImpl
    extends BaseRepository<PartnerEntity, PartnersFilter> implements PartnerViewRepository {

    private final AccountRepository accountRepository;
    private final BudgetMaskDictionaryRepository dictionaryRepository;
    private final PartnerMapper partnerMapper;

    public PartnerViewRepositoryImpl(
        EntityManager entityManager,
        AccountRepository accountRepository,
        BudgetMaskDictionaryRepository dictionaryRepository,
        PartnerMapper partnerMapper
    ) {
        super(entityManager, PartnerEntity.class);
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
        predicates.add(builder.equal(root.get(PartnerEntity_.DIGITAL_ID), filter.getDigitalId()));
        predicates.add(builder.equal(root.get(PartnerEntity_.TYPE), PartnerType.PARTNER));
        var filterSearch = filter.getSearch();
        if (filterSearch != null && StringUtils.hasText(filterSearch.getSearch())) {
            var searchPattern = partnerMapper.prepareSearchString(filterSearch.getSearch())
                .toLowerCase(Locale.getDefault());
            predicates.add(
                builder.like(
                    builder.lower(root.get(PartnerEntity_.SEARCH)),
                    "%" + searchPattern + "%"
                )
            );
        }
        if (filter.getAccountSignType() != null) {
            var accounts = switch (filter.getAccountSignType()) {
                case SIGNED -> accountRepository.findByDigitalIdAndState(filter.getDigitalId(), AccountStateType.SIGNED);
                case NOT_SIGNED -> accountRepository.findByDigitalIdAndState(filter.getDigitalId(), AccountStateType.NOT_SIGNED);
            };
            predicates.add(root.get(PartnerEntity_.UUID).in(accounts.stream().map(AccountEntity::getPartnerUuid).collect(Collectors.toList())));
        }
        if (filter.getPartnersFilter() != null) {
            switch (filter.getPartnersFilter()) {
                case GKU -> {
                    Join<PartnerEntity, GkuInnEntity> join = root.join(PartnerEntity_.GKU_INN_ENTITY, JoinType.LEFT);
                    predicates.add(builder.equal(root.get(PartnerEntity_.INN), join.get(GkuInnEntity_.INN)));
                }
                case BUDGET -> {
                    var masks = dictionaryRepository.findAll().stream().map(BudgetMaskEntity::getCondition).collect(Collectors.toList());
                    var budgetAccount = accountRepository.findBudgetAccounts(filter.getDigitalId(), masks);
                    predicates.add(root.get(PartnerEntity_.UUID)
                        .in(budgetAccount.stream().map(AccountEntity::getPartnerUuid).collect(Collectors.toList())));
                }
                case ENTREPRENEUR -> predicates.add(builder.equal(root.get(PartnerEntity_.LEGAL_TYPE), LegalType.ENTREPRENEUR));
                case PHYSICAL_PERSON -> predicates.add(builder.equal(root.get(PartnerEntity_.LEGAL_TYPE), LegalType.PHYSICAL_PERSON));
                case LEGAL_ENTITY -> predicates.add(builder.equal(root.get(PartnerEntity_.LEGAL_TYPE), LegalType.LEGAL_ENTITY));
            }
        }
    }

    @Override
    List<Order> defaultOrder(CriteriaBuilder builder, Root<?> root) {
        return List.of(
            builder.desc(root.get(PartnerEntity_.DIGITAL_ID)),
            builder.desc(root.get(PartnerEntity_.CREATE_DATE))
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
