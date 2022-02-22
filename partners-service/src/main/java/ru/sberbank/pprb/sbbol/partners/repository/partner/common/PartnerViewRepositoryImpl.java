package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BudgetMaskEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.GkuInnEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AccountStateType;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.LegalType;
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
import java.util.stream.Collectors;

public class PartnerViewRepositoryImpl extends BaseRepository<PartnerEntity, PartnersFilter> implements PartnerViewRepository {

    private final AccountRepository accountRepository;
    private final BudgetMaskDictionaryRepository dictionaryRepository;

    public PartnerViewRepositoryImpl(EntityManager entityManager, AccountRepository accountRepository, BudgetMaskDictionaryRepository dictionaryRepository) {
        super(entityManager, PartnerEntity.class);
        this.accountRepository = accountRepository;
        this.dictionaryRepository = dictionaryRepository;
    }

    @Override
    public List<PartnerEntity> findByFilter(PartnersFilter filter) {
        return filter(filter);
    }

    @Override
    void createPredicate(CriteriaBuilder builder, CriteriaQuery<PartnerEntity> criteria, List<Predicate> predicates, Root<PartnerEntity> root, PartnersFilter filter) {
        predicates.add(builder.equal(root.get("digitalId"), filter.getDigitalId()));
        if (filter.getSearch() != null) {
            var search = filter.getSearch();
            predicates.add(
                builder.or(
                    builder.like(
                        builder.concat(
                            builder.concat(
                                builder.concat(
                                    builder.concat(
                                        root.get("secondName"),
                                        " "
                                    ),
                                    root.get("firstName")
                                ),
                                " "
                            ),
                            root.get("middleName")
                        ),
                        "%" + search.getSearch() + "%"
                    ),
                    builder.like(root.get("orgName"), "%" + search.getSearch() + "%"),
                    builder.like(root.get("inn"), "%" + search.getSearch() + "%")
                )
            );
        }
        if (filter.getAccountSignType() != null) {
            var accounts = switch (filter.getAccountSignType()) {
                case SIGNED -> accountRepository.findByDigitalIdAndState(filter.getDigitalId(), AccountStateType.valueOf(PartnersFilter.AccountSignTypeEnum.SIGNED.name()));
                case NOT_SIGNED -> accountRepository.findByDigitalIdAndState(filter.getDigitalId(), AccountStateType.valueOf(PartnersFilter.AccountSignTypeEnum.NOT_SIGNED.name()));
            };
            predicates.add(root.get("uuid").in(accounts.stream().map(AccountEntity::getPartnerUuid).collect(Collectors.toList())));
        }
        if (filter.getPartnersType() != null) {
            switch (filter.getPartnersType()) {
                case GKU -> {
                    Join<PartnerEntity, GkuInnEntity> join = root.join("inn", JoinType.LEFT);
                    predicates.add(builder.equal(root.get("inn"), join.get("inn")));
                }
                case BUDGET -> {
                    var allBudgetMasks = dictionaryRepository.findAll();
                    var masks = allBudgetMasks.stream().map(BudgetMaskEntity::getCondition).collect(Collectors.toList());
                    var budgetAccount = accountRepository.findBudgetAccounts(filter.getDigitalId(), masks);
                    predicates.add(root.get("uuid").in(budgetAccount.stream().map(AccountEntity::getPartnerUuid).collect(Collectors.toList())));
                }
                case ENTREPRENEUR -> predicates.add(builder.equal(root.get("legalType"), LegalType.ENTREPRENEUR));
                case PHYSICAL_PERSON -> predicates.add(builder.equal(root.get("legalType"), LegalType.PHYSICAL_PERSON));
                case LEGAL_ENTITY -> predicates.add(builder.equal(root.get("legalType"), LegalType.LEGAL_ENTITY));
            }
        }
    }

    @Override
    List<Order> defaultOrder(CriteriaBuilder builder, Root<?> root) {
        return List.of(
            builder.desc(root.get("digitalId")),
            builder.desc(root.get("uuid"))
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
