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
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.FraudChannelIndicator;
import ru.sberbank.pprb.sbbol.partners.model.FraudMetaData;
import ru.sberbank.pprb.sbbol.partners.model.fraud.FraudEventType;

import java.time.LocalDateTime;

import static java.util.Objects.isNull;

public interface BaseFraudMetaDataMapper extends BaseMapper {

    String EVENT_TYPE_SIGN_COUNTERPARTY_ACCOUNT = "SIGN_COUNTERPARTY_ACCOUNT";

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
    CounterPartySendToAnalyzeRq mapToCounterPartySendToAnalyzeRq(FraudMetaData metaData);

    @Mapping(target = "counterpartyId", expression = "java(partner.getUuid().toString())")
    @Mapping(target = "digitalId", source = "clientData.digitalId")
    @Mapping(target = "epkId", source = "clientData.epkId")
    @Mapping(target = "receiverName", expression = "java(getPartnerName(partner))")
    @Mapping(target = "receiverInn", expression = "java(partner.getInn())")
    @Mapping(target = "payerInn", source = "clientData.inn")
    @Mapping(target = "payerName", source = "clientData.orgName")
    @Mapping(target = "osbNumber", source = "clientData.terBankGosb")
    @Mapping(target = "vspNumber", source = "clientData.terBankVsp")
    @Mapping(target = "dboOperationName", expression = "java(getDboOperationName())")
    @Mapping(target = "privateIpAddress", source = "deviceRequest.ipAddress")
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
        return saveSearchString(
            partner.getOrgName(),
            partner.getSecondName(),
            partner.getFirstName(),
            partner.getMiddleName()
        );
    }
}
