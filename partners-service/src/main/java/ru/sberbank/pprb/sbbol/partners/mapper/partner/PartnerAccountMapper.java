package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankAccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity;
import ru.sberbank.pprb.sbbol.partners.model.Bank;
import ru.sberbank.pprb.sbbol.partners.model.BankAccount;
import ru.sberbank.pprb.sbbol.partners.model.PartnerAccount;

import java.util.UUID;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PartnerAccountMapper {
    default UUID mapUuid(final String id) {
        return id != null ? UUID.fromString(id) : null;
    }


    @Mapping(target = "uuid", expression = "java(account.getId() != null ? account.getId().toString() : null)")
    @Mapping(target = "partnerUuid", expression = "java(account.getPartner().getId() != null ? account.getPartner().getId().toString() : null)")
    PartnerAccount toAccount(AccountEntity account);

    @Mapping(target = "uuid", expression = "java(bank.getId() != null ? bank.getId().toString() : null)")
    @Mapping(target = "accounts", source = "bankAccounts")
    Bank toBank(BankEntity bank);

    @Mapping(target = "uuid", expression = "java(bankAccount.getId() != null ? bankAccount.getId().toString() : null)")
    BankAccount toBankAccount(BankAccountEntity bankAccount);

    @Mapping(target = "id", expression = "java(mapUuid(account.getUuid()))")
    AccountEntity toAccount(PartnerAccount account);

    @Mapping(target = "id", expression = "java(mapUuid(bank.getUuid()))")
    @Mapping(target = "bankAccounts", source = "accounts")
    @Mapping(target = "account", ignore = true)
    BankEntity toBank(Bank bank);

    @Mapping(target = "id", expression = "java(mapUuid(bankAccount.getUuid()))")
    @Mapping(target = "bank", ignore = true)
    BankAccountEntity toBankAccount(BankAccount bankAccount);

    @Named("updateAccount")
    void updateAccount(PartnerAccount account, @MappingTarget() AccountEntity accountEntity);
}
