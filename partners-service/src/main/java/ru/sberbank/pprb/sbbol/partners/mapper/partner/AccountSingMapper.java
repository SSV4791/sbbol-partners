package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.SignEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.AccountSign;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignDetail;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignInfo;

import java.util.UUID;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AccountSingMapper extends BaseMapper {

    @Mapping(target = "accountId", expression = "java(account.getUuid().toString())")
    AccountSign toSignAccount(AccountEntity account);

    @Named("toSignAccount")
    @Mapping(target = "entityId", expression = "java(sign.getEntityUuid().toString())")
    @Mapping(target = "accountId", expression = "java(sign.getAccountUuid().toString())")
    @Mapping(target = "digest", source = "digest")
    @Mapping(target = "sign", source = "sign")
    @Mapping(target = "externalDataFileId", source = "externalDataFileId")
    @Mapping(target = "externalDataSignFileId", source = "externalDataSignFileId")
    @Mapping(target = "dateTimeOfSign", source = "dateTimeOfSign")
    AccountSignDetail toSignAccount(SignEntity sign);

    @Mapping(target = "digitalId", source = "digitalId")
    @Mapping(target = "accountSignDetail", source = "sign", qualifiedByName = "toSignAccount")
    AccountSignInfo toSignAccount(SignEntity sign, String digitalId);

    @Mapping(target = "entityUuid", expression = "java(mapUuid(sing.getEntityId()))")
    @Mapping(target = "accountUuid", expression = "java(mapUuid(sing.getAccountId()))")
    @Mapping(target = "partnerUuid", source = "partnerUuid")
    SignEntity toSing(AccountSignDetail sing, UUID partnerUuid);
}
