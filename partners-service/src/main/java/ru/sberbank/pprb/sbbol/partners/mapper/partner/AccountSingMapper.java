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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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

    default Map<String, String> toEventParams(SignEntity sign) {
        if (sign == null) {
            return Collections.emptyMap();
        }
        var params = new HashMap<String, String>();
        if (sign.getUuid() != null) {
            params.put("uuid", sign.getUuid().toString());
        }
        if (sign.getVersion() != null) {
            params.put("version", sign.getVersion().toString());
        }
        if (sign.getEntityUuid() != null) {
            params.put("entityUuid", sign.getEntityUuid().toString());
        }
        if (sign.getDigest() != null) {
            params.put("digest", sign.getDigest());
        }
        if (sign.getSign() != null) {
            params.put("sign", sign.getSign());
        }
        if (sign.getPartnerUuid() != null) {
            params.put("partnerUuid", sign.getPartnerUuid().toString());
        }
        if (sign.getAccountUuid() != null) {
            params.put("accountUuid", sign.getAccountUuid().toString());
        }
        if (sign.getExternalDataFileId() != null) {
            params.put("externalDataFileId", sign.getExternalDataFileId());
        }
        if (sign.getExternalDataSignFileId() != null) {
            params.put("externalDataSignFileId", sign.getExternalDataSignFileId());
        }
        if (sign.getDateTimeOfSign() != null) {
            params.put("dateTimeOfSign", sign.getDateTimeOfSign().toString());
        }
        return params;
    }
}
