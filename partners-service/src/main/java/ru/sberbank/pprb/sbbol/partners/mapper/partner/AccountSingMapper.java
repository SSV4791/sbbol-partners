package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.AccountSign;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AccountSingMapper extends BaseMapper {

    @Mapping(target = "accountId", expression = "java(account.getUuid().toString())")
    @Mapping(target = "signId", source = "signCollectionId")
    AccountSign toSignAccount(AccountEntity account);

    @Named("updateSignAccount")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "signCollectionId", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "partnerUuid", ignore = true)
    @Mapping(target = "banks", ignore = true)
    @Mapping(target = "uuid", expression = "java(mapUuid(account.getAccountId()))")
    void updateSignAccount(AccountSign account, @MappingTarget() AccountEntity accountEntity);
}
