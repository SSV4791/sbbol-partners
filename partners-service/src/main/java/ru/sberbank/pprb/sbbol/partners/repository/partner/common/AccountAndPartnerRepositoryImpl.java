package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity_;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankAccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankAccountEntity_;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity_;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity_;
import ru.sberbank.pprb.sbbol.partners.model.AccountAndPartnerRequest;

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
import java.util.Objects;

public class AccountAndPartnerRepositoryImpl extends BaseRepository<AccountEntity, AccountAndPartnerRequest>
    implements AccountAndPartnerRepository {

    protected AccountAndPartnerRepositoryImpl(EntityManager entityManager) {
        super(entityManager, AccountEntity.class);
    }

    @Override
    public List<AccountEntity> findByRequest(AccountAndPartnerRequest request) {
        return filter(request);
    }

    @Override
    void createPredicate(
        CriteriaBuilder builder,
        CriteriaQuery<AccountEntity> criteria,
        List<Predicate> predicates,
        Root<AccountEntity> root,
        AccountAndPartnerRequest request
    ) {
        if (Objects.isNull(request)) {
            return;
        }
        predicates.add(builder.equal(root.get(AccountEntity_.DIGITAL_ID), request.getDigitalId()));
        Join<AccountEntity, PartnerEntity> partnerJoin = root.join(AccountEntity_.PARTNER, JoinType.INNER);
        predicates.add(builder.equal(partnerJoin.get(PartnerEntity_.INN), request.getInn()));

        Join<AccountEntity, BankEntity> bankJoin = root.join(AccountEntity_.BANK, JoinType.INNER);
        predicates.add(builder.equal(bankJoin.get(BankEntity_.BIC), request.getBic()));

        Join<BankEntity, BankAccountEntity> bankAccountJoin = bankJoin.join(BankEntity_.BANK_ACCOUNT, JoinType.INNER);
        predicates.add(builder.equal(bankAccountJoin.get(BankAccountEntity_.ACCOUNT), request.getBankAccount()));
    }

    @Override
    List<Order> defaultOrder(CriteriaBuilder builder, Root<?> root) {
        return List.of(
            builder.desc(root.get(AccountEntity_.DIGITAL_ID)),
            builder.desc(root.get(AccountEntity_.CREATE_DATE))
        );
    }

    @Override
    void pagination(TypedQuery<AccountEntity> query, AccountAndPartnerRequest request) {
        // Do nothing because filter does not have pagination
    }
}
