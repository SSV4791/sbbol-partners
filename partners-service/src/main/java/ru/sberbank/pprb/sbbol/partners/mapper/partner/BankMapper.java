package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.decorator.BankMapperDecorator;
import ru.sberbank.pprb.sbbol.partners.model.Bank;
import ru.sberbank.pprb.sbbol.partners.model.BankChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.BankCreate;

@Loggable
@Mapper(uses = {BaseMapper.class, BankAccountMapper.class})
@DecoratedWith(BankMapperDecorator.class)
public interface BankMapper {

    @Mapping(target = "id", source = "uuid", qualifiedByName = "mapUuid")
    @Mapping(target = "accountId", source = "account.uuid", qualifiedByName = "mapUuid")
    Bank toBank(BankEntity bank);

    @Mapping(target = "account", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    BankEntity toBank(BankCreate bank);

    Bank toBank(BankChangeFullModel bankChangeFullModel);

    @Mapping(target = "uuid", source = "id", qualifiedByName = "mapUuid")
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    BankEntity toBank(Bank bank);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "bankAccount", ignore = true)
    void updateBank(Bank bank, @MappingTarget BankEntity bankEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "bankAccount", ignore = true)
    void patchBank(Bank bank, @MappingTarget BankEntity bankEntity);

    @AfterMapping
    default void mapBidirectional(@MappingTarget BankEntity bank) {
        var bankAccount = bank.getBankAccount();
        if (bankAccount != null) {
            bankAccount.setBank(bank);
        }
    }
}
