package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankAccountEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.BankAccount;
import ru.sberbank.pprb.sbbol.partners.model.BankAccountCreate;

@Loggable
@Mapper
public interface BankAccountMapper extends BaseMapper {

    @Mapping(target = "id", expression = "java(bankAccount.getUuid() == null ? null : bankAccount.getUuid().toString())")
    @Mapping(target = "bankId", expression = "java(bankAccount.getBank().getUuid() ==null ? null : bankAccount.getBank().getUuid().toString())")
    @Mapping(target = "bankAccount", source = "account")
    BankAccount toBankAccount(BankAccountEntity bankAccount);

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

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "bank", ignore = true)
    @Mapping(target = "account", source = "bankAccount")
    void updateBankAccount(BankAccount bankAccount, @MappingTarget BankAccountEntity bankAccountEntity);
}
