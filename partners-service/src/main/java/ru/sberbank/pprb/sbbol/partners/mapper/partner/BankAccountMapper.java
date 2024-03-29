package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankAccountEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.BankAccount;
import ru.sberbank.pprb.sbbol.partners.model.BankAccountCreate;

@Loggable
@Mapper(uses = {BaseMapper.class})
public interface BankAccountMapper {

    @Mapping(target = "id", source = "uuid")
    @Mapping(target = "bankId", source = "bank.uuid")
    @Mapping(target = "bankAccount", source = "account")
    BankAccount toBankAccount(BankAccountEntity bankAccount);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "bank", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "account", source = "bankAccount")
    BankAccountEntity toBankAccount(BankAccountCreate bankAccount);

    @Mapping(target = "uuid", source = "id")
    @Mapping(target = "bank", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "account", source = "bankAccount")
    BankAccountEntity toBankAccount(BankAccount bankAccount);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "bank", ignore = true)
    @Mapping(target = "account", source = "bankAccount")
    void updateBankAccount(BankAccount bankAccount, @MappingTarget BankAccountEntity bankAccountEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "bank", ignore = true)
    @Mapping(target = "account", source = "bankAccount")
    void patchBankAccount(BankAccount bankAccount, @MappingTarget BankAccountEntity bankAccountEntity);
}
