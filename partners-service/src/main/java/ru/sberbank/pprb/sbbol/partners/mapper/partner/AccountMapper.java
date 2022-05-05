package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankAccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.Bank;
import ru.sberbank.pprb.sbbol.partners.model.BankAccount;
import ru.sberbank.pprb.sbbol.partners.service.partner.BudgetMaskService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AccountMapper extends BaseMapper {

    @Mapping(target = "id", expression = "java(accounts.getUuid() == null ? null :accounts.getUuid().toString())")
    @Mapping(target = "partnerId", expression = "java(accounts.getPartnerUuid() == null ? null : accounts.getPartnerUuid().toString())")
    @Mapping(target = "budget", ignore = true)
    List<Account> toAccounts(List<AccountEntity> accounts);

    @Mapping(target = "id", expression = "java(account.getUuid() == null ? null :account.getUuid().toString())")
    @Mapping(target = "partnerId", expression = "java(account.getPartnerUuid() == null ? null : account.getPartnerUuid().toString())")
    @Mapping(target = "budget", ignore = true)
    Account toAccount(AccountEntity account, @Context BudgetMaskService budgetMaskService);

    @Mapping(target = "id", expression = "java(bank.getUuid() == null ? null : bank.getUuid().toString())")
    @Mapping(target = "accountId", expression = "java(bank.getAccount().getUuid() == null ? null : bank.getAccount().getUuid().toString())")
    @Mapping(target = "mediary", source = "intermediary")
    Bank toBank(BankEntity bank);

    @Mapping(target = "id", expression = "java(bankAccount.getUuid() == null ? null : bankAccount.getUuid().toString())")
    @Mapping(target = "bankId", expression = "java(bankAccount.getBank().getUuid() ==null ? null : bankAccount.getBank().getUuid().toString())")
    BankAccount toBankAccount(BankAccountEntity bankAccount);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "partnerUuid", expression = "java(mapUuid(account.getPartnerId()))")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    AccountEntity toAccount(AccountCreate account);

    @Mapping(target = "uuid", expression = "java(mapUuid(bank.getId()))")
    @Mapping(target = "intermediary", source = "mediary")
    @Mapping(target = "account", ignore = true)
    BankEntity toBank(Bank bank);

    @Mapping(target = "uuid", expression = "java(mapUuid(bankAccount.getId()))")
    @Mapping(target = "bank", ignore = true)
    BankAccountEntity toBankAccount(BankAccount bankAccount);

    @Named("updateAccount")
    @Mapping(target = "uuid", expression = "java(mapUuid(account.getId()))")
    @Mapping(target = "partnerUuid", expression = "java(mapUuid(account.getPartnerId()))")
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "priorityAccount", ignore = true)
    void updateAccount(AccountChange account, @MappingTarget() AccountEntity accountEntity);

    @AfterMapping
    default void mapBidirectional(@MappingTarget AccountEntity account) {
        var bank = account.getBank();
        if (bank != null) {
            bank.setAccount(account);
        }
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
                account.setBudget(budgetMaskService.isBudget(account.getAccount(), bank.getBic(), bankAccount.getAccount()));
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
