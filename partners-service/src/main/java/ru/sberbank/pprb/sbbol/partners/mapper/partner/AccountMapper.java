package ru.sberbank.pprb.sbbol.partners.mapper.partner;

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

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AccountMapper extends BaseMapper {

    @Mapping(target = "uuid", expression = "java(account.getId() != null ? account.getId().toString() : null)")
    @Mapping(target = "partnerUuid", expression = "java(account.getPartnerUuid() != null ? account.getPartnerUuid().toString() : null)")
    Account toAccount(AccountEntity account);

    @Mapping(target = "uuid", expression = "java(bank.getId() != null ? bank.getId().toString() : null)")
    @Mapping(target = "accountUuid", expression = "java(bank.getHashKey() != null ? bank.getHashKey() : null)")
    @Mapping(target = "mediary", source = "intermediary")
    Bank toBank(BankEntity bank);

    @Mapping(target = "uuid", expression = "java(bankAccount.getId() != null ? bankAccount.getId().toString() : null)")
    @Mapping(target = "bankUuid", expression = "java(bankAccount.getBank() != null ? bankAccount.getBank().getId().toString() : null)")
    BankAccount toBankAccount(BankAccountEntity bankAccount);

    @Mapping(target = "id", expression = "java(mapUuid(account.getUuid()))")
    @Mapping(target = "partnerUuid", expression = "java(mapUuid(account.getPartnerUuid()))")
    AccountEntity toAccount(Account account);

    @Mapping(target = "id", expression = "java(mapUuid(bank.getUuid()))")
    @Mapping(target = "intermediary", source = "mediary")
    @Mapping(target = "account", ignore = true)
    BankEntity toBank(Bank bank);

    @Mapping(target = "id", expression = "java(mapUuid(bankAccount.getUuid()))")
    @Mapping(target = "bank", ignore = true)
    BankAccountEntity toBankAccount(BankAccount bankAccount);

    @Named("updateAccount")
    @Mapping(target = "id", expression = "java(mapUuid(account.getUuid()))")
    @Mapping(target = "partnerUuid", expression = "java(mapUuid(account.getPartnerUuid()))")
    void updateAccount(Account account, @MappingTarget() AccountEntity accountEntity);
}
