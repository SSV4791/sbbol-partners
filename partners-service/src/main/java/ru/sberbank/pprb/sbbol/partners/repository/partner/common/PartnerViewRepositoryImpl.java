package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BudgetMaskEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.GkuInnEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.LegalType;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;
import ru.sberbank.pprb.sbbol.partners.repository.partner.AccountRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.BudgetMaskDictionaryRepository;
import ru.sberbank.pprb.sbbol.renter.model.RenterFilter;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PartnerViewRepositoryImpl implements PartnerViewRepository, BaseRepository<PartnerEntity> {

    @PersistenceContext
    private EntityManager entityManager;

    private final AccountRepository accountRepository;
    private final BudgetMaskDictionaryRepository dictionaryRepository;

    public PartnerViewRepositoryImpl(AccountRepository accountRepository, BudgetMaskDictionaryRepository dictionaryRepository) {
        this.accountRepository = accountRepository;
        this.dictionaryRepository = dictionaryRepository;
    }

    @Override
    public List<PartnerEntity> findByFilter(RenterFilter filter) {
        var builder = entityManager.getCriteriaBuilder();
        var criteria = builder.createQuery(PartnerEntity.class);
        var root = criteria.from(PartnerEntity.class);
        List<Predicate> predicates = List.of(builder.equal(root.get("digitalId"), filter.getDigitalId()));
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
    public List<PartnerEntity> findByFilter(PartnersFilter filter) {
        var builder = entityManager.getCriteriaBuilder();
        var criteria = builder.createQuery(PartnerEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        var root = criteria.from(PartnerEntity.class);
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
        if (filter.getPartnersType() != null) {
            switch (filter.getPartnersType()) {
                case GKU -> {
                    Join<PartnerEntity, GkuInnEntity> join = root.join("inn", JoinType.LEFT);
                    predicates.add(builder.equal(root.get("inn"), join.get("inn")));
                }
                case BUDGET -> {
                    var allBudgetMasks = dictionaryRepository.findAll();
                    var masks = allBudgetMasks.stream().map(BudgetMaskEntity::getCondition).collect(Collectors.toList());
                    var budgetAccount = accountRepository.findBudgetAccount(filter.getDigitalId(), masks);
                    predicates.add(root.get("uuid").in(budgetAccount.stream().map(AccountEntity::getPartnerUuid).collect(Collectors.toList())));
                }
                case ENTREPRENEUR -> predicates.add(builder.equal(root.get("legalType"), LegalType.ENTREPRENEUR));
                case PHYSICAL_PERSON -> predicates.add(builder.equal(root.get("legalType"), LegalType.PHYSICAL_PERSON));
                case LEGAL_ENTITY -> predicates.add(builder.equal(root.get("legalType"), LegalType.LEGAL_ENTITY));
            }
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
}
