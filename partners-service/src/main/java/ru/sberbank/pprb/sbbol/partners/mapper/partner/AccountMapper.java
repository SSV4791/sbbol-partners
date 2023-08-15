package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.DecoratedWith;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.decorator.AccountMapperDecorator;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.AccountWithPartnerResponse;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.service.partner.BudgetMaskService;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper.saveSearchString;

@Loggable
@Mapper(uses = {
    BaseMapper.class,
    BankMapper.class
},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
@DecoratedWith(AccountMapperDecorator.class)
public interface AccountMapper {

    @InheritConfiguration
    List<Account> toAccounts(List<AccountEntity> accounts);

    @Mapping(target = "id", source = "uuid")
    @Mapping(target = "partnerId", source = "partnerUuid")
    @Mapping(target = "changeDate", source = "lastModifiedDate")
    @Mapping(target = "budget", ignore = true)
    Account toAccount(AccountEntity account);

    AccountChange toAccount(AccountChangeFullModel account, String digitalId, UUID partnerId);

    AccountCreate toAccountCreate(AccountChangeFullModel account, String digitalId, UUID partnerId);

    @InheritConfiguration(name = "toAccount")
    Account toAccount(AccountEntity account, @Context BudgetMaskService budgetMaskService);

    default List<AccountEntity> toAccounts(Set<AccountCreateFullModel> accounts, String digitalId, UUID partnerUuid) {
        if (CollectionUtils.isEmpty(accounts)) {
            return Collections.emptyList();
        }
        return accounts.stream()
            .map(value -> toAccount(value, digitalId, partnerUuid))
            .collect(Collectors.toList());
    }

    @Mapping(target = "partnerUuid", source = "partnerUuid")
    @Mapping(target = "digitalId", source = "digitalId")
    @Mapping(target = "account", source = "account.account")
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "state", constant = "NOT_SIGNED")
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "priorityAccount", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "search", ignore = true)
    @Mapping(target = "partner", ignore = true)
    @Mapping(target = "idLinks", ignore = true)
    AccountEntity toAccount(AccountCreateFullModel account, String digitalId, UUID partnerUuid);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "partnerUuid", source = "partnerId")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "state", constant = "NOT_SIGNED")
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "priorityAccount", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "search", ignore = true)
    @Mapping(target = "partner", ignore = true)
    @Mapping(target = "idLinks", ignore = true)
    AccountEntity toAccount(AccountCreate account);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "partnerUuid", source = "partnerId")
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "priorityAccount", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "search", ignore = true)
    @Mapping(target = "partner", ignore = true)
    @Mapping(target = "bank", ignore = true)
    void updateAccount(AccountChange account, @MappingTarget AccountEntity accountEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "partnerUuid", source = "partnerId")
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "priorityAccount", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "search", ignore = true)
    @Mapping(target = "partner", ignore = true)
    @Mapping(target = "bank", ignore = true)
    void patchAccount(AccountChange account, @MappingTarget AccountEntity accountEntity);

    @AfterMapping
    default void mapBidirectional(@MappingTarget AccountEntity account) {
        var partnerUUID = account.getPartnerUuid();
        var accountNumber = account.getAccount();
        String bic = null;
        String corAccount = null;
        var bank = account.getBank();
        if (bank != null) {
            bank.setAccount(account);
            bic = bank.getBic();
            var bankAccount = bank.getBankAccount();
            if (bankAccount != null) {
                corAccount = bankAccount.getAccount();
            }
        }
        var search = prepareSearchField(partnerUUID, accountNumber, bic, corAccount);
        account.setSearch(search);
    }

    @AfterMapping
    default void mapBudgetMask(@MappingTarget Account account, @Context BudgetMaskService budgetMaskService) {
        var bank = account.getBank();
        if (bank != null) {
            var bankAccount = bank.getBankAccount();
            if (bankAccount != null) {
                account.setBudget(budgetMaskService.isBudget(account.getAccount(), bank.getBic(), bankAccount.getBankAccount()));
            }
        }
    }

    List<AccountWithPartnerResponse> toAccountsWithPartner(List<AccountEntity> accounts);

    @Mapping(target = "account", source = "accountDto")
    @Mapping(target = "id", source = "partner.uuid")
    @Mapping(target = "digitalId", source = "partner.digitalId")
    @Mapping(target = "legalForm", source = "partner.legalType")
    @Mapping(target = "orgName", source = "partner.orgName")
    @Mapping(target = "firstName", source = "partner.firstName")
    @Mapping(target = "secondName", source = "partner.secondName")
    @Mapping(target = "middleName", source = "partner.middleName")
    @Mapping(target = "inn", source = "partner.inn")
    @Mapping(target = "kpp", source = "partner.kpp")
    @Mapping(target = "comment", source = "partner.comment")
    @Mapping(target = "version", source = "partner.version")
    @Mapping(target = "gku", ignore = true)
    AccountWithPartnerResponse toAccountWithPartner(AccountEntity accountDto);

    default List<AccountWithPartnerResponse> toAccountsWithPartner(Partner partner) {
        return List.of(toAccountWithPartner(partner));
    }

    @Mapping(target = "account", ignore = true)
    AccountWithPartnerResponse toAccountWithPartner(Partner partner);

    default String prepareSearchField(
        UUID partnerUUID,
        String account,
        String bic,
        String corAccount
    ) {
        return saveSearchString(partnerUUID.toString(), account, bic, corAccount);
    }
}
