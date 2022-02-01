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
import ru.sberbank.pprb.sbbol.partners.model.Bank;
import ru.sberbank.pprb.sbbol.partners.model.BankAccount;
import ru.sberbank.pprb.sbbol.partners.service.partner.BudgetMaskService;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AccountMapper extends BaseMapper {

    @Mapping(target = "id", expression = "java(account.getUuid().toString())")
    @Mapping(target = "partnerId", expression = "java(account.getPartnerUuid().toString())")
    @Mapping(target = "budget", ignore = true)
    Account toAccount(AccountEntity account, @Context BudgetMaskService budgetMaskService);

    @Mapping(target = "id", expression = "java(bank.getUuid().toString())")
    @Mapping(target = "partnerAccountId", expression = "java(bank.getAccount().getUuid().toString())")
    @Mapping(target = "mediary", source = "intermediary")
    Bank toBank(BankEntity bank);

    @Mapping(target = "id", expression = "java(bankAccount.getUuid().toString())")
    @Mapping(target = "bankId", expression = "java(bankAccount.getBank().getUuid().toString())")
    BankAccount toBankAccount(BankAccountEntity bankAccount);

    @Mapping(target = "uuid", expression = "java(mapUuid(account.getId()))")
    @Mapping(target = "partnerUuid", expression = "java(mapUuid(account.getPartnerId()))")
    @Mapping(target = "signCollectionId", ignore = true)
    AccountEntity toAccount(Account account);

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
    @Mapping(target = "signCollectionId", ignore = true)
    void updateAccount(Account account, @MappingTarget() AccountEntity accountEntity);

    @AfterMapping
    default void mapBidirectional(@MappingTarget AccountEntity account) {
        var banks = account.getBanks();
        if (banks != null) {
            for (var bank : banks) {
                bank.setAccount(account);
            }
        }
    }

    @AfterMapping
    default void mapBidirectional(@MappingTarget BankEntity bank) {
        var bankAccounts = bank.getBankAccounts();
        if (bankAccounts != null) {
            for (var bankAccount : bankAccounts) {
                bankAccount.setBank(bank);
            }
        }
    }

    @AfterMapping
    default void mapBudgetMask(@MappingTarget Account account, @Context BudgetMaskService budgetMaskService) {
        for (Bank bank : account.getBanks()) {
            for (BankAccount bankAccount : bank.getBankAccounts()) {
                account.setBudget(budgetMaskService.isBudget(account.getAccount(), bank.getBic(), bankAccount.getAccount()));
            }
        }
    }
}
