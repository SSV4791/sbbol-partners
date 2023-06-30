package ru.sberbank.pprb.sbbol.partners.mapper.fraud;

import org.mapstruct.Context;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.ChannelIndicator;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.ClientDefinedChannelIndicator;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.ClientDefinedEventType;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.counterparty.CounterPartyClientDefinedAttributes;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.counterparty.CounterPartyEventData;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.counterparty.CounterPartyMessageHeader;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.counterparty.CounterPartySendToAnalyzeRq;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.model.FraudChannelIndicator;
import ru.sberbank.pprb.sbbol.partners.model.FraudMetaData;
import ru.sberbank.pprb.sbbol.partners.model.fraud.FraudEventType;

import java.time.LocalDateTime;

import static java.util.Objects.isNull;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper.prepareSearchString;

public interface BaseFraudMetaDataMapper {

    String EVENT_TYPE_SIGN_COUNTERPARTY_ACCOUNT = "EDIT_PAYEE";

    String EVENT_TYPE_EDIT_PAYEE = "EDIT_PAYEE";

    String ANALYZE_REQUEST_TYPE = "ANALYZE";

    String DBO_OPERATION_NAME = "PARTNERS";

    String PPRB_BROWSER = "PPRB_BROWSER";

    @Mapping(target = "messageHeader", source = "metaData", qualifiedByName = "toMessageHeader")
    @Mapping(target = "identificationData", source = "clientData")
    @Mapping(target = "deviceRequest", source = "deviceRequest")
    @Mapping(target = "channelIndicator", source = "channelIndicator", qualifiedByName = "toChannelIndicator")
    @Mapping(target = "clientDefinedChannelIndicator", expression = "java(toClientDefinedChannelIndicator())")
    @Mapping(target = "eventData", source = "eventData", qualifiedByName = "toEventData")
    @Mapping(target = "clientDefinedAttributeList", ignore = true)
    CounterPartySendToAnalyzeRq mapToCounterPartySendToAnalyzeRq(FraudMetaData metaData);

    @Mapping(target = "counterpartyId", expression = "java(partner.getUuid().toString())")
    @Mapping(target = "digitalId", source = "clientData.digitalId")
    @Mapping(target = "epkId", source = "clientData.epkId")
    @Mapping(target = "receiverName", expression = "java(getPartnerName(partner))")
    @Mapping(target = "receiverInn", expression = "java(partner.getInn())")
    @Mapping(target = "payerInn", source = "clientData.inn")
    @Mapping(target = "payerName", source = "clientData.orgName")
    @Mapping(target = "osbNumber", source = "clientData.gosbNumber")
    @Mapping(target = "vspNumber", source = "clientData.vspNumber")
    @Mapping(target = "dboOperationName", expression = "java(getDboOperationName())")
    @Mapping(target = "privateIpAddress", source = "deviceRequest.ipAddress")
    @Mapping(target = "userComment", ignore = true)
    @Mapping(target = "receiverBicSwift", ignore = true)
    @Mapping(target = "receiverAccount", ignore = true)
    @Mapping(target = "firstSignTime", ignore = true)
    @Mapping(target = "firstSignIpAddress", ignore = true)
    @Mapping(target = "firstSignLogin", ignore = true)
    @Mapping(target = "firstSignCryptoprofile", ignore = true)
    @Mapping(target = "firstSignCryptoprofileType", ignore = true)
    @Mapping(target = "firstSignChannel", ignore = true)
    @Mapping(target = "firstSignType", ignore = true)
    @Mapping(target = "firstSignImsi", ignore = true)
    @Mapping(target = "firstSignCertId", ignore = true)
    @Mapping(target = "firstSignPhone", ignore = true)
    @Mapping(target = "firstSignEmail", ignore = true)
    @Mapping(target = "firstSignSource", ignore = true)
    @Mapping(target = "firstSignToken", ignore = true)
    @Mapping(target = "senderIpAddress", ignore = true)
    @Mapping(target = "senderLogin", ignore = true)
    @Mapping(target = "senderPhone", ignore = true)
    @Mapping(target = "senderEmail", ignore = true)
    @Mapping(target = "senderSource", ignore = true)
    @Mapping(target = "sbbolGuid", ignore = true)
    @Mapping(target = "reestrId", ignore = true)
    @Mapping(target = "reestrRowCount", ignore = true)
    @Mapping(target = "reestrRowNumber", ignore = true)
    CounterPartyClientDefinedAttributes toCounterPartyClientDefinedAttributes(FraudMetaData metaData, @Context PartnerEntity partner);

    @Named("toMessageHeader")
    default CounterPartyMessageHeader toMessageHeader(FraudMetaData metaData) {
        if (isNull(metaData)) {
            return null;
        }
        return new CounterPartyMessageHeader(
            metaData.getEventData().getTimeOfOccurrence().toLocalDateTime(),
            ANALYZE_REQUEST_TYPE
        );
    }

    default CounterPartyEventData toEventData(FraudEventType eventType, LocalDateTime timeOfOccurrence) {
        var rq = new CounterPartyEventData();
        rq.setEventType(toEventType(eventType));
        rq.setClientDefinedEventType(getClientDefinedEventType(eventType));
        rq.setTimeOfOccurrence(timeOfOccurrence);
        return rq;
    }

    default String toEventType(FraudEventType eventType) {
        return switch (eventType) {
            case SIGN_ACCOUNT -> EVENT_TYPE_SIGN_COUNTERPARTY_ACCOUNT;
            case DELETE_PARTNER -> EVENT_TYPE_EDIT_PAYEE;
        };
    }

    static ClientDefinedEventType getClientDefinedEventType(FraudEventType eventType) {
        return switch (eventType) {
            case SIGN_ACCOUNT -> ClientDefinedEventType.BROWSER_APPROVAL;
            case DELETE_PARTNER -> ClientDefinedEventType.BROWSER_REMOVE_PAYEE;
        };
    }

    @Named("toClientDefinedChannelIndicator")
    default ClientDefinedChannelIndicator toClientDefinedChannelIndicator() {
        return ClientDefinedChannelIndicator.PPRB_BROWSER;
    }

    @Named("toChannelIndicator")
    static ChannelIndicator toChannelIndicator(FraudChannelIndicator channelIndicator) {
        if (isNull(channelIndicator)) {
            return null;
        }
        return switch (channelIndicator) {
            case WEB -> ChannelIndicator.WEB;
            case MOBILE -> ChannelIndicator.MOBILE;
        };
    }

    default String getDboOperationName() {
        return DBO_OPERATION_NAME;
    }

    default String getPartnerName(PartnerEntity partner) {
        return prepareSearchString(
            partner.getOrgName(),
            partner.getSecondName(),
            partner.getFirstName(),
            partner.getMiddleName()
        );
    }
}
