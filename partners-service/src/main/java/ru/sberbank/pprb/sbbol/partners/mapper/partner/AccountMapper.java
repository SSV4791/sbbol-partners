package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.DecoratedWith;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.decorator.AccountMapperDecorator;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.AccountWithPartnerResponse;
import ru.sberbank.pprb.sbbol.partners.service.partner.BudgetMaskService;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Loggable
@Mapper(uses = {BankMapper.class})
@DecoratedWith(AccountMapperDecorator.class)
public interface AccountMapper extends BaseMapper {

    @InheritConfiguration
    List<Account> toAccounts(List<AccountEntity> accounts);

    @Mapping(target = "id", expression = "java(account.getUuid() == null ? null : account.getUuid().toString())")
    @Mapping(target = "partnerId", expression = "java(account.getPartnerUuid() == null ? null : account.getPartnerUuid().toString())")
    @Mapping(target = "budget", ignore = true)
    Account toAccount(AccountEntity account);

    AccountChange toAccount(AccountChangeFullModel account, String digitalId, String partnerId);

    AccountCreate toAccountCreate(AccountChangeFullModel account, String digitalId, String partnerId);

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
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "priorityAccount", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "search", ignore = true)
    @Mapping(target = "partner", ignore = true)
    AccountEntity toAccount(AccountCreateFullModel account, String digitalId, UUID partnerUuid);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "partnerUuid", expression = "java(mapUuid(account.getPartnerId()))")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "priorityAccount", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "search", ignore = true)
    @Mapping(target = "partner", ignore = true)
    AccountEntity toAccount(AccountCreate account);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "partnerUuid", expression = "java(mapUuid(account.getPartnerId()))")
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
    @Mapping(target = "partnerUuid", source = "partnerId", qualifiedByName = "toUUID")
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "priorityAccount", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "search", ignore = true)
    @Mapping(target = "partner", ignore = true)
    @Mapping(target = "bank", ignore = true)
    void patchAccount(AccountChange account, @MappingTarget AccountEntity accountEntity);

    @Named("toUUID")
    default UUID toUUID(String id) {
        return mapUuid(id);
    }

    @AfterMapping
    default void mapBidirectional(@MappingTarget AccountEntity account) {
        var searchSubString = saveSearchString(
            account.getPartnerUuid().toString(),
            account.getAccount()
        );
        var bank = account.getBank();
        if (bank != null) {
            bank.setAccount(account);
            searchSubString = saveSearchString(
                searchSubString,
                bank.getBic()
            );
            var bankAccount = bank.getBankAccount();
            if (bankAccount != null) {
                searchSubString = saveSearchString(
                    searchSubString,
                    bankAccount.getAccount()
                );
            }
        }
        account.setSearch(searchSubString);
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
    @Mapping(target = "id", expression = "java(accountDto.getPartner().getUuid().toString())")
    @Mapping(target = "digitalId", source = "partner.digitalId")
    @Mapping(target = "legalForm", source = "partner.legalType")
    @Mapping(target = "orgName", source = "partner.orgName")
    @Mapping(target = "firstName", source = "partner.firstName")
    @Mapping(target = "secondName", source = "partner.secondName")
    @Mapping(target = "middleName", source = "partner.middleName")
    @Mapping(target = "inn", source = "partner.inn")
    @Mapping(target = "kpp", source = "partner.kpp")
    @Mapping(target = "comment", source = "partner.comment")
    AccountWithPartnerResponse toAccountWithPartner(AccountEntity accountDto);

    default List<AccountWithPartnerResponse> toAccountsWithPartner(PartnerEntity partner) {
        return List.of(toAccountWithPartner(partner));
    }

    @Mapping(target = "id",
        expression = "java(partner.getUuid() == null ? null : partner.getUuid().toString())")
    @Mapping(target = "legalForm", source = "legalType")
    @Mapping(target = "account", ignore = true)
    AccountWithPartnerResponse toAccountWithPartner(PartnerEntity partner);
}
