package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.AfterMapping;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.BankType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.decorator.BankMapperDecorator;
import ru.sberbank.pprb.sbbol.partners.model.Bank;
import ru.sberbank.pprb.sbbol.partners.model.BankCreate;

@Loggable
@Mapper(uses = {BankAccountMapper.class})
@DecoratedWith(BankMapperDecorator.class)
public interface BankMapper extends BaseMapper {

    @Mapping(target = "id", expression = "java(bank.getUuid() == null ? null : bank.getUuid().toString())")
    @Mapping(target = "accountId", expression = "java(bank.getAccount().getUuid() == null ? null : bank.getAccount().getUuid().toString())")
    @Mapping(target = "mediary", source = "type", qualifiedByName = "getMediaryByBankType")
    Bank toBank(BankEntity bank);

    @Named("getMediaryByBankType")
    default boolean getMediaryByBankType(BankType bankType) {
        return bankType == BankType.AGENT;
    }

    @Mapping(target = "account", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    BankEntity toBank(BankCreate bank);

    @Mapping(target = "uuid", expression = "java(mapUuid(bank.getId()))")
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    BankEntity toBank(Bank bank);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    void updateBank(Bank bank, @MappingTarget BankEntity bankEntity);

    @AfterMapping
    default void mapBidirectional(@MappingTarget BankEntity bank) {
        var bankAccount = bank.getBankAccount();
        if (bankAccount != null) {
            bankAccount.setBank(bank);
        }
    }

    default BankType getBankType(Boolean mediary, ru.sberbank.pprb.sbbol.partners.model.BankType bankType) {
        return BankType.DEFAULT;
    };
}