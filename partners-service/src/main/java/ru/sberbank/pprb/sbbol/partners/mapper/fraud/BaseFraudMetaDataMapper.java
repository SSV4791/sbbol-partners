package ru.sberbank.pprb.sbbol.partners.mapper.fraud;

import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.ChannelIndicator;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.ClientDefinedChannelIndicator;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.ClientDefinedEventType;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.request.AnalyzeRequest;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.request.Attribute;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.request.ClientDefinedAttributeList;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.request.EventData;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.request.EventDataList;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.request.MessageHeader;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.response.FullAnalyzeResponse;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.model.FraudChannelInfo;
import ru.sberbank.pprb.sbbol.partners.model.FraudClientData;
import ru.sberbank.pprb.sbbol.partners.model.FraudDeviceRequest;
import ru.sberbank.pprb.sbbol.partners.model.FraudEventData;
import ru.sberbank.pprb.sbbol.partners.model.FraudMetaData;
import ru.sberbank.pprb.sbbol.partners.model.fraud.FraudEventType;

import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper.prepareSearchString;

public interface BaseFraudMetaDataMapper {

    String STRING_ATTRIBUTE_DATA_TYPE = "STRING";

    String DATE_TIME_ATTRIBUTE_DATA_TYPE = "DATETIME";

    String EVENT_TYPE_SIGN_COUNTERPARTY_ACCOUNT = "EDIT_PAYEE";

    String EVENT_TYPE_EDIT_PAYEE = "EDIT_PAYEE";

    String ANALYZE_REQUEST_TYPE = "ANALYZE";

    String DBO_OPERATION_NAME = "PARTNERS";

    String PPRB_BROWSER = "PPRB_BROWSER";

    @Mapping(target = "messageHeader", source = "metaData", qualifiedByName = "toMessageHeader")
    @Mapping(target = "identificationData", source = "clientData")
    @Mapping(target = "deviceRequest", source = "deviceRequest")
    @Mapping(target = "channelIndicator", source = "metaData", qualifiedByName = "toChannelIndicator")
    @Mapping(target = "clientDefinedChannelIndicator", source = "channelInfo", qualifiedByName = "toClientDefinedChannelIndicator")
    @Mapping(target = "eventDataList", source = "metaData", qualifiedByName = "toEventDataList")
    AnalyzeRequest mapToAnalyzeRequest(FraudMetaData metaData);

    @Named("toMessageHeader")
    default MessageHeader toMessageHeader(FraudMetaData metaData) {
        if (isNull(metaData)) {
            return null;
        }
        return new MessageHeader(
            metaData.getEventData().getTimeOfOccurrence().toLocalDateTime(),
            ANALYZE_REQUEST_TYPE
        );
    }

    @Named("toClientDefinedChannelIndicator")
    default String toClientDefinedChannelIndicator(FraudChannelInfo channelInfo) {
        if (isNull(channelInfo)) {
            return ClientDefinedChannelIndicator.PPRB_BROWSER.toString();
        }
        return switch (channelInfo.getClientDefinedChannelIndicator()) {
            case BROWSER -> ClientDefinedChannelIndicator.PPRB_BROWSER.toString();
            case UPG_1C -> ClientDefinedChannelIndicator.PPRB_UPG_1C.toString();
            case UPG_SBB -> ClientDefinedChannelIndicator.PPRB_UPG_SBB.toString();
            case UPG_CORP -> ClientDefinedChannelIndicator.PPRB_UPG_CORP.toString();
        };
    }

    @Named("toChannelIndicator")
    static String toChannelIndicator(FraudMetaData metaData) {
        if (isNull(metaData)) {
            return null;
        }
        return switch (metaData.getChannelInfo().getChannelIndicator()) {
            case WEB -> ChannelIndicator.WEB.toString();
            case MOBILE -> ChannelIndicator.MOBILE.toString();
            case OTHER -> ChannelIndicator.OTHER.toString();
        };
    }

    default EventDataList toEventDataList(FraudEventType eventType, FraudMetaData metaData) {
        var rq = new EventDataList();
        rq.setEventData(toEventData(eventType, metaData.getEventData()));
        rq.setClientDefinedAttributeList(toClientDefinedAttributeList(metaData));
        return rq;
    }

    default ClientDefinedAttributeList toClientDefinedAttributeList(FraudMetaData metaData) {
        List<Attribute> attributes = new LinkedList<>();
        addClientDefinedAttributeList(attributes, metaData.getClientData());
        addClientDefinedAttributeList(attributes, metaData.getDeviceRequest());
        return new ClientDefinedAttributeList(attributes);
    }

    default void addClientDefinedAttributeList(List<Attribute> attributes, FraudClientData clientData) {
        if (isNull(clientData)) {
            return;
        }
        attributes.add(new Attribute("digitalId", clientData.getDigitalId(), STRING_ATTRIBUTE_DATA_TYPE));
        attributes.add(new Attribute("epkId", clientData.getEpkId(), STRING_ATTRIBUTE_DATA_TYPE));
        attributes.add(new Attribute("payerInn", clientData.getInn(), STRING_ATTRIBUTE_DATA_TYPE));
        attributes.add(new Attribute("payerName", clientData.getOrgName(), STRING_ATTRIBUTE_DATA_TYPE));
        attributes.add(new Attribute("osbNumber", clientData.getGosbNumber(), STRING_ATTRIBUTE_DATA_TYPE));
        attributes.add(new Attribute("vspNumber", clientData.getVspNumber(), STRING_ATTRIBUTE_DATA_TYPE));
        attributes.add(new Attribute("dboOperationName", getDboOperationName(), STRING_ATTRIBUTE_DATA_TYPE));
        attributes.add(new Attribute("senderLogin", clientData.getLogin(), STRING_ATTRIBUTE_DATA_TYPE));
        attributes.add(new Attribute("senderEmail", clientData.getEmail(), STRING_ATTRIBUTE_DATA_TYPE));
        attributes.add(new Attribute("senderPhone", clientData.getPhone(), STRING_ATTRIBUTE_DATA_TYPE));
        attributes.add(new Attribute("senderSource", PPRB_BROWSER, STRING_ATTRIBUTE_DATA_TYPE));
    }

    default void addClientDefinedAttributeList(List<Attribute> attributes, FraudDeviceRequest deviceRequest) {
        if (isNull(deviceRequest)) {
            return;
        }
        attributes.add(new Attribute("privateIpAddress", deviceRequest.getPrivateIpAddress(), STRING_ATTRIBUTE_DATA_TYPE));
        attributes.add(new Attribute("senderIpAddress", deviceRequest.getIpAddress(), STRING_ATTRIBUTE_DATA_TYPE));
    }

    default void addClientDefinedAttributeList(List<Attribute> attributes, PartnerEntity partner) {
        if (isNull(partner)) {
            return;
        }
        attributes.add(new Attribute("partnerId", partner.getUuid().toString(), STRING_ATTRIBUTE_DATA_TYPE));
        attributes.add(new Attribute("receiverName", getPartnerName(partner), STRING_ATTRIBUTE_DATA_TYPE));
        attributes.add(new Attribute("receiverInn", partner.getInn(), STRING_ATTRIBUTE_DATA_TYPE));
        attributes.add(new Attribute("userComment", partner.getComment(), STRING_ATTRIBUTE_DATA_TYPE));
    }

    default void addClientDefinedAttributeList(List<Attribute> attributes, AccountEntity account) {
        if (isNull(account)) {
            return;
        }
        if (account.getBank() != null) {
            attributes.add(new Attribute("receiverBicSwift", account.getBank().getBic(), STRING_ATTRIBUTE_DATA_TYPE));
        }
        attributes.add(new Attribute("receiverAccount", account.getAccount(), STRING_ATTRIBUTE_DATA_TYPE));
    }

    default EventData toEventData(FraudEventType eventType, FraudEventData eventData) {
        var ed = new EventData();
        ed.setEventType(toEventType(eventType));
        ed.setClientDefinedEventType(getClientDefinedEventType(eventData, eventType).toString());
        ed.setTimeOfOccurrence(eventData.getTimeOfOccurrence().toLocalDateTime());
        return ed;
    }

    default String toEventType(FraudEventType eventType) {
        return switch (eventType) {
            case SIGN_ACCOUNT -> EVENT_TYPE_SIGN_COUNTERPARTY_ACCOUNT;
            case DELETE_PARTNER -> EVENT_TYPE_EDIT_PAYEE;
        };
    }

    default ClientDefinedEventType getClientDefinedEventType(FraudEventData eventData, FraudEventType eventType) {
        if (nonNull(eventData) && nonNull(eventData.getClientDefinedEventType())) {
            return switch (eventData.getClientDefinedEventType()) {
                case BROWSER_APPROVAL -> ClientDefinedEventType.BROWSER_APPROVAL;
                case BROWSER_REMOVE_PAYEE -> ClientDefinedEventType.BROWSER_REMOVE_PAYEE;
                case UPG_1C_APPROVAL -> ClientDefinedEventType.UPG_1C_PAYDOCRU;
                case UPG_SBB_APPROVAL -> ClientDefinedEventType.UPG_SBB_PAYDOCRU;
                case UPG_CORP_APPROVAL -> ClientDefinedEventType.UPG_CORP_PAYDOCRU;
            };
        }
        return getClientDefinedEventType(eventType);
    }

    private static ClientDefinedEventType getClientDefinedEventType(FraudEventType eventType) {
        return switch (eventType) {
            case SIGN_ACCOUNT -> ClientDefinedEventType.BROWSER_APPROVAL;
            case DELETE_PARTNER -> ClientDefinedEventType.BROWSER_REMOVE_PAYEE;
        };
    }

    default String getDboOperationName() {
        return DBO_OPERATION_NAME;
    }

    default String getAnalyzeResponseActionCode(FullAnalyzeResponse fullAnalyzeResponse) {
        if (isNull(fullAnalyzeResponse) ||
            isNull(fullAnalyzeResponse.getRiskResult()) ||
            isNull(fullAnalyzeResponse.getRiskResult().getTriggeredRule())) {
            return null;
        }
        return fullAnalyzeResponse.getRiskResult().getTriggeredRule().getActionCode();
    }

    default String getAnalyzeResponseDetailledComment(FullAnalyzeResponse fullAnalyzeResponse) {
        if (isNull(fullAnalyzeResponse) ||
            isNull(fullAnalyzeResponse.getRiskResult()) ||
            isNull(fullAnalyzeResponse.getRiskResult().getTriggeredRule())) {
            return null;
        }
        return fullAnalyzeResponse.getRiskResult().getTriggeredRule().getDetailledComment();
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
