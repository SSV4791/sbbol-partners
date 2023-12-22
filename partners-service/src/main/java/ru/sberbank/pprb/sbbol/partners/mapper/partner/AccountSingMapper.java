package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.SignEntity;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartySignData;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignDetail;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignInfo;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignInfoRequisitesResponse;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;

@Loggable
@Mapper(uses = {BaseMapper.class})
public interface AccountSingMapper {

    @Named("toSignAccount")
    @Mapping(target = "entityId", source = "entityUuid")
    @Mapping(target = "accountId", source = "accountUuid")
    @Mapping(target = "digest", source = "digest")
    @Mapping(target = "sign", source = "sign")
    @Mapping(target = "externalDataFileId", source = "externalDataFileId")
    @Mapping(target = "externalDataSignFileId", source = "externalDataSignFileId")
    @Mapping(target = "dateTimeOfSign", source = "dateTimeOfSign")
    @Mapping(target = "signProfileId", source = "signProfileId")
    @Mapping(target = "cryptoProfileId", source = "cryptoProfileId")
    AccountSignDetail toSignAccount(SignEntity sign);

    @Mapping(target = "digitalId", source = "digitalId")
    @Mapping(target = "accountSignDetail", source = "sign", qualifiedByName = "toSignAccount")
    AccountSignInfo toSignAccount(SignEntity sign, String digitalId);

    @Mapping(target = "entityUuid", source = "sing.entityId")
    @Mapping(target = "accountUuid", source = "sing.accountId")
    @Mapping(target = "partnerUuid", source = "partnerUuid")
    @Mapping(target = "digitalId", source = "digitalId")
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    SignEntity toSing(AccountSignDetail sing, UUID partnerUuid, String digitalId);

    @Mapping(target = "pprbGuid", source = "accountUuid")
    @Mapping(target = "signProfileId", source = "signProfileId")
    @Mapping(target = "cryptoProfileId", source = "cryptoProfileId")
    @Mapping(target = "signDate", source = "dateTimeOfSign", qualifiedByName = "toSignDate")
    @Mapping(target = "base64sign", source = "sign")
    @Mapping(target = "digest", source = "digest")
    @Mapping(target = "dcsId", expression = "java(\"default\")")
    CounterpartySignData toCounterpartySignData(SignEntity signEntity);

    @Mapping(target = "accountId", source = "id")
    @Mapping(target = "status", source = "state")
    AccountSignInfoRequisitesResponse toAccountSignRequisitesResponse(Account account);

    @Named("toSignDate")
    default Date toSignDate(OffsetDateTime dateTimeOfSign) {
        return Date.from(dateTimeOfSign.toInstant());
    }
}
