package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity_;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity_;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.PartnerType;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignInfoRequisites;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Locale;

import static ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper.saveSearchString;

public class AccountSearchRepositoryImpl extends BaseRepository<AccountEntity, AccountSignInfoRequisites> implements AccountSearchRepository {

    public AccountSearchRepositoryImpl() {
        super(AccountEntity.class);
    }

    @Override
    public List<AccountEntity> findByRequisites(AccountSignInfoRequisites accountSignInfoRequisites) {
        return filter(accountSignInfoRequisites);
    }

    @Override
    void createPredicate(
        CriteriaBuilder builder,
        CriteriaQuery<AccountEntity> criteria,
        List<Predicate> predicates,
        Root<AccountEntity> root,
        AccountSignInfoRequisites accountSignInfoRequisites
    ) {
        addDigitalIdPredicate(builder, predicates, root, accountSignInfoRequisites);
        addSearchPredicate(builder, predicates, root, accountSignInfoRequisites);
        addPartnerTypePredicate(builder, predicates, root);
    }

    @Override
    List<Order> defaultOrder(CriteriaBuilder builder, Root<?> root) {
        return List.of(
            builder.desc(root.get(AccountEntity_.DIGITAL_ID)),
            builder.desc(root.get(AccountEntity_.STATE))
        );
    }

    @Override
    void pagination(TypedQuery<AccountEntity> query, AccountSignInfoRequisites accountSignInfoRequisites) {
        // Do nothing because filter does not have pagination
    }

    private void addPartnerTypePredicate(CriteriaBuilder builder, List<Predicate> predicates, Root<AccountEntity> root) {
        Join<AccountEntity, PartnerEntity> partner = root.join(AccountEntity_.PARTNER);
        predicates.add(builder.equal(partner.get(PartnerEntity_.TYPE), PartnerType.PARTNER));
    }

    private void addDigitalIdPredicate(
        CriteriaBuilder builder,
        List<Predicate> predicates,
        Root<AccountEntity> root,
        AccountSignInfoRequisites accountSignInfoRequisites
    ) {
        predicates.add(builder.equal(root.get(AccountEntity_.DIGITAL_ID), accountSignInfoRequisites.getDigitalId()));
    }

    private void addSearchPredicate(
        CriteriaBuilder builder,
        List<Predicate> predicates,
        Root<AccountEntity> root,
        AccountSignInfoRequisites accountSignInfoRequisites
    ) {
        if (accountSignInfoRequisites != null) {
            var searchPattern = saveSearchString(accountSignInfoRequisites.getAccount(), accountSignInfoRequisites.getBic())
                .toLowerCase(Locale.getDefault());
            predicates.add(
                builder.like(
                    builder.lower(root.get(AccountEntity_.SEARCH)),
                    "%" + searchPattern + "%"
                )
            );
        }
    }
}
