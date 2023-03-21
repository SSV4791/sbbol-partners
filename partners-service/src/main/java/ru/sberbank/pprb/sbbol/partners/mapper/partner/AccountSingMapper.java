package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.SignEntity;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartySignData;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignDetail;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignInfo;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;

@Loggable
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AccountSingMapper extends BaseMapper {

    @Named("toSignAccount")
    @Mapping(target = "entityId", expression = "java(sign.getEntityUuid().toString())")
    @Mapping(target = "accountId", expression = "java(sign.getAccountUuid().toString())")
    @Mapping(target = "digest", source = "digest")
    @Mapping(target = "sign", source = "sign")
    @Mapping(target = "externalDataFileId", source = "externalDataFileId")
    @Mapping(target = "externalDataSignFileId", source = "externalDataSignFileId")
    @Mapping(target = "dateTimeOfSign", source = "dateTimeOfSign")
    @Mapping(target = "signProfileId", source = "signProfileId")
    AccountSignDetail toSignAccount(SignEntity sign);

    @Mapping(target = "digitalId", source = "digitalId")
    @Mapping(target = "accountSignDetail", source = "sign", qualifiedByName = "toSignAccount")
    AccountSignInfo toSignAccount(SignEntity sign, String digitalId);

    @Mapping(target = "entityUuid", expression = "java(mapUuid(sing.getEntityId()))")
    @Mapping(target = "accountUuid", expression = "java(mapUuid(sing.getAccountId()))")
    @Mapping(target = "partnerUuid", source = "partnerUuid")
    @Mapping(target = "digitalId", source = "digitalId")
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    SignEntity toSing(AccountSignDetail sing, UUID partnerUuid, String digitalId);

    @Mapping(target = "pprbGuid", source = "accountUuid")
    @Mapping(target = "signProfileId", source = "signProfileId")
    @Mapping(target = "signDate", source = "dateTimeOfSign", qualifiedByName = "toSignDate")
    @Mapping(target = "base64sign", source = "sign")
    @Mapping(target = "digest", source = "digest")
    @Mapping(target = "dcsId", expression = "java(\"default\")")
    CounterpartySignData toCounterpartySignData(SignEntity signEntity);

    @Named("toSignDate")
    default Date toSignDate(OffsetDateTime dateTimeOfSign) {
        return Date.from(dateTimeOfSign.toInstant());
    }
}
