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
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerMapper;
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

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;

public class FullMatchingAccountAndPartnerRepositoryImpl extends BaseRepository<AccountEntity, AccountAndPartnerRequest>
    implements FullMatchingAccountAndPartnerRepository {

    private final PartnerMapper partnerMapper;

    protected FullMatchingAccountAndPartnerRepositoryImpl(
        PartnerMapper partnerMapper
    ) {
        super(AccountEntity.class);
        this.partnerMapper = partnerMapper;
    }

    @Override
    public List<AccountEntity> findByAllRequestAttributes(AccountAndPartnerRequest request) {
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

        Join<AccountEntity, PartnerEntity> partnerJoin = root.join(AccountEntity_.PARTNER, JoinType.INNER);
        addPartnerInnPredicate(builder, predicates, partnerJoin, request.getInn());
        addPartnerKppPredicate(builder, predicates, partnerJoin, request.getKpp());
        addPartnerNamePredicate(builder, predicates, partnerJoin, request.getName());
        addPartnerTypePredicate(builder, predicates, partnerJoin, PartnerType.PARTNER);

        Join<AccountEntity, BankEntity> bankJoin = root.join(AccountEntity_.BANK, JoinType.INNER);
        addBankBicPredicate(builder, predicates, bankJoin, request.getBic());

        Join<BankEntity, BankAccountEntity> bankAccountJoin = bankJoin.join(BankEntity_.BANK_ACCOUNT, JoinType.LEFT);
        addBankAccountPredicate(builder, predicates, bankAccountJoin, request.getBankAccount());
    }

    @Override
    List<Order> defaultOrder(CriteriaBuilder builder, Root<?> root) {
        return List.of(
            builder.desc(root.get(AccountEntity_.CREATE_DATE))
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
        String accountNumber
    ) {
        if (StringUtils.isNotEmpty(accountNumber)) {
            predicates.add(builder.equal(root.get(AccountEntity_.ACCOUNT), accountNumber));
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

    private void addPartnerKppPredicate(
        CriteriaBuilder builder,
        List<Predicate> predicates,
        Join<AccountEntity, PartnerEntity> partnerJoin,
        String kpp
    ) {
        if (StringUtils.isNotEmpty(kpp)) {
            predicates.add(builder.equal(partnerJoin.get(PartnerEntity_.KPP), kpp));
        } else {
            predicates.add(builder.or(
                builder.equal(partnerJoin.get(PartnerEntity_.KPP), StringUtils.EMPTY),
                builder.isNull(partnerJoin.get(PartnerEntity_.KPP))
            ));
        }
    }

    private void addPartnerNamePredicate(
        CriteriaBuilder builder,
        List<Predicate> predicates,
        Join<AccountEntity, PartnerEntity> partnerJoin,
        String partnerName
    ) {
        var partnerNameSearchPattern = partnerMapper.saveSearchString(partnerName);
        predicates.add(
            builder.like(
                builder.function("replace",
                    String.class,
                    builder.lower(partnerJoin.get(PartnerEntity_.SEARCH)),
                    builder.literal(SPACE),
                    builder.literal(EMPTY)
                ),
                "%" + partnerNameSearchPattern + "%"
            )
        );
    }

    private void addPartnerTypePredicate(
        CriteriaBuilder builder,
        List<Predicate> predicates,
        Join<AccountEntity, PartnerEntity> partnerJoin,
        PartnerType partnerType
    ) {
        predicates.add(builder.equal(partnerJoin.get(PartnerEntity_.TYPE), partnerType));
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
