package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankAccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.Bank;
import ru.sberbank.pprb.sbbol.partners.model.BankAccount;
import ru.sberbank.pprb.sbbol.partners.model.BankAccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.BankCreate;
import ru.sberbank.pprb.sbbol.partners.service.partner.BudgetMaskService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Loggable
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AccountMapper extends BaseMapper {

    @InheritConfiguration
    List<Account> toAccounts(List<AccountEntity> accounts);

    @Mapping(target = "id", expression = "java(account.getUuid() == null ? null : account.getUuid().toString())")
    @Mapping(target = "partnerId", expression = "java(account.getPartnerUuid() == null ? null : account.getPartnerUuid().toString())")
    @Mapping(target = "budget", ignore = true)
    Account toAccount(AccountEntity account);

    @InheritConfiguration(name = "toAccount")
    Account toAccount(AccountEntity account, @Context BudgetMaskService budgetMaskService);

    @Mapping(target = "id", expression = "java(bank.getUuid() == null ? null : bank.getUuid().toString())")
    @Mapping(target = "accountId", expression = "java(bank.getAccount().getUuid() == null ? null : bank.getAccount().getUuid().toString())")
    @Mapping(target = "mediary", source = "intermediary")
    Bank toBank(BankEntity bank);

    @Mapping(target = "id", expression = "java(bankAccount.getUuid() == null ? null : bankAccount.getUuid().toString())")
    @Mapping(target = "bankId", expression = "java(bankAccount.getBank().getUuid() ==null ? null : bankAccount.getBank().getUuid().toString())")
    @Mapping(target = "bankAccount", source = "account")
    BankAccount toBankAccount(BankAccountEntity bankAccount);


    default List<AccountEntity> toAccounts(Set<AccountCreateFullModel> accounts, String digitalId, UUID partnerUuid) {
        if (CollectionUtils.isEmpty(accounts)) {
            return Collections.emptyList();
        }
        return accounts.stream()
            .map(value -> toAccount(value, digitalId, partnerUuid))
            .collect(Collectors.toList());
    }

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "partnerUuid", source = "partnerUuid")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "priorityAccount", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "search", ignore = true)
    @Mapping(target = "digitalId", source = "digitalId")
    @Mapping(target = "account", source = "account.account")
    AccountEntity toAccount(AccountCreateFullModel account, String digitalId, UUID partnerUuid);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "partnerUuid", expression = "java(mapUuid(account.getPartnerId()))")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "priorityAccount", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "search", ignore = true)
    AccountEntity toAccount(AccountCreate account);

    @Mapping(target = "intermediary", source = "mediary")
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    BankEntity toBank(BankCreate bank);

    @Mapping(target = "uuid", expression = "java(mapUuid(bank.getId()))")
    @Mapping(target = "intermediary", source = "mediary")
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    BankEntity toBank(Bank bank);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "bank", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "account", source = "bankAccount")
    BankAccountEntity toBankAccount(BankAccountCreate bankAccount);

    @Mapping(target = "uuid", expression = "java(mapUuid(bankAccount.getId()))")
    @Mapping(target = "bank", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "account", source = "bankAccount")
    BankAccountEntity toBankAccount(BankAccount bankAccount);

    @Mapping(target = "uuid", expression = "java(mapUuid(account.getId()))")
    @Mapping(target = "partnerUuid", expression = "java(mapUuid(account.getPartnerId()))")
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "priorityAccount", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "search", ignore = true)
    void updateAccount(AccountChange account, @MappingTarget() AccountEntity accountEntity);

    @Mapping(target = "uuid", expression = "java(mapUuid(bank.getId()))")
    @Mapping(target = "intermediary", source = "mediary")
    @Mapping(target = "lastModifiedDate", ignore = true)
    void updateBank(Bank bank, @MappingTarget() BankEntity bankEntity);

    @Mapping(target = "uuid", expression = "java(mapUuid(bankAccount.getId()))")
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "account", source = "bankAccount")
    void updateBankAccount(BankAccount bankAccount, @MappingTarget() BankAccountEntity bankAccountEntity);

    @AfterMapping
    default void mapBidirectional(@MappingTarget AccountEntity account) {
        var join =
            String.join(",", account.getDigitalId(), account.getPartnerUuid().toString(), account.getAccount());

        var bank = account.getBank();
        if (bank != null) {
            bank.setAccount(account);
            join = String.join(",", join, bank.getBic());
            var bankAccount = bank.getBankAccount();
            if (bankAccount != null) {
                join = String.join(",", join, bankAccount.getAccount());
            }
        }
        account.setSearch(join);
    }

    @AfterMapping
    default void mapBidirectional(@MappingTarget BankEntity bank) {
        var bankAccount = bank.getBankAccount();
        if (bankAccount != null) {
            bankAccount.setBank(bank);
        }
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

    default Map<String, String> toEventParams(AccountEntity account) {
        if (account == null) {
            return Collections.emptyMap();
        }
        var params = new HashMap<String, String>();
        if (account.getUuid() != null) {
            params.put("uuid", account.getUuid().toString());
        }
        if (account.getVersion() != null) {
            params.put("version", account.getVersion().toString());
        }
        if (account.getDigitalId() != null) {
            params.put("digitalId", account.getDigitalId());
        }
        if (account.getCreateDate() != null) {
            params.put("createDate", account.getCreateDate().toString());
        }
        if (account.getLastModifiedDate() != null) {
            params.put("lastModifiedDate", account.getLastModifiedDate().toString());
        }
        if (account.getPartnerUuid() != null) {
            params.put("partnerUuid", account.getPartnerUuid().toString());
        }
        if (account.getAccount() != null) {
            params.put("account", account.getAccount());
        }
        if (account.getState() != null) {
            params.put("state", account.getState().name());
        }
        if (account.getComment() != null) {
            params.put("comment", account.getComment());
        }
        var bank = account.getBank();
        if (bank == null) {
            return params;
        }
        if (bank.getName() != null) {
            params.put("bankName", bank.getName());
        }
        if (bank.getBic() != null) {
            params.put("bankBic", bank.getBic());
        }
        var bankAccount = account.getBank().getBankAccount();
        if (bankAccount == null) {
            return params;
        }
        if (bankAccount.getAccount() != null) {
            params.put("bankAccount", bankAccount.getAccount());
        }
        return params;
    }
}
