package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity_;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankAccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankAccountEntity_;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity_;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity_;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.PartnerType;
import ru.sberbank.pprb.sbbol.partners.model.AccountAndPartnerRequest;

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

    protected AccountAndPartnerRepositoryImpl() {
        super(AccountEntity.class);
    }

    @Override
    public List<AccountEntity> findByRequestAttributes(AccountAndPartnerRequest request) {
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

        addDigitalIdPredicate(builder, predicates, root, request.getDigitalId());
        addAccountPredicate(builder, predicates, root, request.getAccount());
        addAccountTypePredicate(builder, predicates, root);

        Join<AccountEntity, PartnerEntity> partnerJoin = root.join(AccountEntity_.PARTNER, JoinType.INNER);
        addPartnerInnPredicate(builder, predicates, partnerJoin, request.getInn());

        Join<AccountEntity, BankEntity> bankJoin = root.join(AccountEntity_.BANK, JoinType.INNER);
        addBankBicPredicate(builder, predicates, bankJoin, request.getBic());

        Join<BankEntity, BankAccountEntity> bankAccountJoin = bankJoin.join(BankEntity_.BANK_ACCOUNT, JoinType.LEFT);
        addBankAccountPredicate(builder, predicates, bankAccountJoin, request.getBankAccount());
    }

    @Override
    List<Order> defaultOrder(CriteriaBuilder builder, Root<?> root) {
        return List.of(
            builder.desc(root.get(AccountEntity_.LAST_MODIFIED_DATE))
        );
    }

    @Override
    void pagination(TypedQuery<AccountEntity> query, AccountAndPartnerRequest request) {
        // Do nothing because filter does not have pagination
    }

    private void addDigitalIdPredicate(
        CriteriaBuilder builder,
        List<Predicate> predicates,
        Root<AccountEntity> root,
        String digitalId
    ) {
        predicates.add(builder.equal(root.get(AccountEntity_.DIGITAL_ID), digitalId));
    }

    private void addAccountPredicate(
        CriteriaBuilder builder,
        List<Predicate> predicates,
        Root<AccountEntity> root,
        String account
    ) {
        if (StringUtils.isNotEmpty(account)) {
            predicates.add(builder.equal(root.get(AccountEntity_.ACCOUNT), account));
        } else {
            predicates.add(builder.or(
                builder.equal(root.get(AccountEntity_.ACCOUNT), StringUtils.EMPTY),
                builder.isNull(root.get(AccountEntity_.ACCOUNT))
            ));
        }
    }

    private void addPartnerInnPredicate(
        CriteriaBuilder builder,
        List<Predicate> predicates,
        Join<AccountEntity, PartnerEntity> partnerJoin,
        String inn
    ) {
        if (StringUtils.isNotEmpty(inn)) {
            predicates.add(builder.equal(partnerJoin.get(PartnerEntity_.INN), inn));
        } else {
            predicates.add(builder.or(
                builder.equal(partnerJoin.get(PartnerEntity_.INN), StringUtils.EMPTY),
                builder.isNull(partnerJoin.get(PartnerEntity_.INN))
            ));
        }
    }

    private void addAccountTypePredicate(
        CriteriaBuilder builder,
        List<Predicate> predicates,
        Root<AccountEntity> root
    ) {
        predicates.add(builder.equal(root.get(AccountEntity_.PARTNER_TYPE), PartnerType.PARTNER));
    }

    private void addBankBicPredicate(
        CriteriaBuilder builder,
        List<Predicate> predicates,
        Join<AccountEntity, BankEntity> bankJoin,
        String bic
    ) {
        if (StringUtils.isNotEmpty(bic)) {
            predicates.add(builder.equal(bankJoin.get(BankEntity_.BIC), bic));
        } else {
            predicates.add(builder.or(
                builder.equal(bankJoin.get(BankEntity_.BIC), StringUtils.EMPTY),
                builder.isNull(bankJoin.get(BankEntity_.BIC))
            ));
        }
    }

    private void addBankAccountPredicate(
        CriteriaBuilder builder,
        List<Predicate> predicates,
        Join<BankEntity, BankAccountEntity> bankAccountJoin,
        String bankAccount
    ) {
        if (StringUtils.isNotEmpty(bankAccount)) {
            predicates.add(builder.equal(bankAccountJoin.get(BankAccountEntity_.ACCOUNT), bankAccount));
        } else {
            predicates.add(builder.or(
                builder.equal(bankAccountJoin.get(BankAccountEntity_.ACCOUNT), StringUtils.EMPTY),
                builder.isNull(bankAccountJoin.get(BankAccountEntity_.ACCOUNT))
            ));
        }
    }
}
