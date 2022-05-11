package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.SignEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.AccountSign;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignDetail;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignInfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
    @Mapping(target = "signProfileId", source = "signProfileId")
    AccountSignDetail toSignAccount(SignEntity sign);

    @Mapping(target = "digitalId", source = "digitalId")
    @Mapping(target = "accountSignDetail", source = "sign", qualifiedByName = "toSignAccount")
    AccountSignInfo toSignAccount(SignEntity sign, String digitalId);

    @Mapping(target = "entityUuid", expression = "java(mapUuid(sing.getEntityId()))")
    @Mapping(target = "accountUuid", expression = "java(mapUuid(sing.getAccountId()))")
    @Mapping(target = "partnerUuid", source = "partnerUuid")
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    SignEntity toSing(AccountSignDetail sing, UUID partnerUuid);

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
