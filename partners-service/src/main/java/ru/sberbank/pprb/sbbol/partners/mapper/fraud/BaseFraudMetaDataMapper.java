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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.COUNTER_PARTY_ID;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.DIGITAL_ID;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.EPK_ID;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.FIRST_SIGN_TIME;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.OSB_NUMBER;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.PAYER_INN;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.PAYER_NAME;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.PRIVATE_IP_ADDRESS;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.RECEIVER_ACCOUNT;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.RECEIVER_BIC_SWIFT;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.RECEIVER_INN;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.RECEIVER_NAME;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.SENDER_EMAIL;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.SENDER_IP_ADDRESS;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.SENDER_LOGIN;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.SENDER_PHONE;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.SENDER_SOURCE;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.USER_COMMENT;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.VSP_NUMBER;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper.prepareSearchString;

public interface BaseFraudMetaDataMapper {

    String EVENT_TYPE_SIGN_COUNTERPARTY_ACCOUNT = "EDIT_PAYEE";

    String EVENT_TYPE_EDIT_PAYEE = "EDIT_PAYEE";

    String ANALYZE_REQUEST_TYPE = "ANALYZE";

    String PPRB_BROWSER = "PPRB_BROWSER";

    int HOURS = 3;

    String PATTERN_LOCAL_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss.SSS";

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
        attributes.add(new Attribute(DIGITAL_ID.getAttributeName(), clientData.getDigitalId(), DIGITAL_ID.getAttributeType()));
        attributes.add(new Attribute(EPK_ID.getAttributeName(), clientData.getEpkId(), EPK_ID.getAttributeType()));
        attributes.add(new Attribute(PAYER_INN.getAttributeName(), clientData.getInn(), PAYER_INN.getAttributeType()));
        attributes.add(new Attribute(PAYER_NAME.getAttributeName(), clientData.getOrgName(), PAYER_NAME.getAttributeType()));
        attributes.add(new Attribute(OSB_NUMBER.getAttributeName(), clientData.getGosbNumber(), OSB_NUMBER.getAttributeType()));
        attributes.add(new Attribute(VSP_NUMBER.getAttributeName(), clientData.getVspNumber(), VSP_NUMBER.getAttributeType()));
        attributes.add(new Attribute(SENDER_LOGIN.getAttributeName(), clientData.getLogin(), SENDER_LOGIN.getAttributeType()));
        attributes.add(new Attribute(SENDER_EMAIL.getAttributeName(), clientData.getEmail(), SENDER_EMAIL.getAttributeType()));
        attributes.add(new Attribute(SENDER_PHONE.getAttributeName(), clientData.getPhone(), SENDER_PHONE.getAttributeType()));
        attributes.add(new Attribute(SENDER_SOURCE.getAttributeName(), PPRB_BROWSER, SENDER_SOURCE.getAttributeType()));
    }

    default void addClientDefinedAttributeList(List<Attribute> attributes, FraudDeviceRequest deviceRequest) {
        if (isNull(deviceRequest)) {
            return;
        }
        attributes.add(new Attribute(PRIVATE_IP_ADDRESS.getAttributeName(), deviceRequest.getPrivateIpAddress(), PRIVATE_IP_ADDRESS.getAttributeType()));
        attributes.add(new Attribute(SENDER_IP_ADDRESS.getAttributeName(), deviceRequest.getIpAddress(), SENDER_IP_ADDRESS.getAttributeType()));
    }

    default void addClientDefinedAttributeList(List<Attribute> attributes, PartnerEntity partner) {
        if (isNull(partner)) {
            return;
        }
        attributes.add(new Attribute(COUNTER_PARTY_ID.getAttributeName(), partner.getUuid().toString(), COUNTER_PARTY_ID.getAttributeType()));
        attributes.add(new Attribute(RECEIVER_NAME.getAttributeName(), getPartnerName(partner), RECEIVER_NAME.getAttributeType()));
        attributes.add(new Attribute(RECEIVER_INN.getAttributeName(), partner.getInn(), RECEIVER_INN.getAttributeType()));
        attributes.add(new Attribute(USER_COMMENT.getAttributeName(), partner.getComment(), USER_COMMENT.getAttributeType()));
    }

    default void addClientDefinedAttributeList(List<Attribute> attributes, AccountEntity account) {
        if (isNull(account)) {
            return;
        }
        if (account.getBank() != null) {
            attributes.add(new Attribute(RECEIVER_BIC_SWIFT.getAttributeName(), account.getBank().getBic(), RECEIVER_BIC_SWIFT.getAttributeType()));
        }
        attributes.add(new Attribute(RECEIVER_ACCOUNT.getAttributeName(), account.getAccount(), RECEIVER_ACCOUNT.getAttributeType()));
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

    default String getAnalyzeResponseActionCode(FullAnalyzeResponse fullAnalyzeResponse) {
        if (isNull(fullAnalyzeResponse) ||
            isNull(fullAnalyzeResponse.getRiskResult()) ||
            isNull(fullAnalyzeResponse.getRiskResult().getTriggeredRule())) {
            return null;
        }
        return fullAnalyzeResponse.getRiskResult().getTriggeredRule().getActionCode().toUpperCase(Locale.getDefault());
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

    default void addTimeToAnalyzeRequest(AnalyzeRequest rq) {
        if (Objects.nonNull(rq.getEventDataList()) &&
            Objects.nonNull(rq.getEventDataList().getEventData()) &&
            Objects.nonNull(rq.getEventDataList().getEventData().getTimeOfOccurrence())
        ) {
            rq.getEventDataList().getEventData().setTimeOfOccurrence(
                normalizationLocalDateTime(rq.getEventDataList().getEventData().getTimeOfOccurrence())
            );
        }
        if (Objects.nonNull(rq.getMessageHeader()) &&
            Objects.nonNull(rq.getMessageHeader().getTimeStamp())) {
            rq.getMessageHeader().setTimeStamp(
                normalizationLocalDateTime(rq.getMessageHeader().getTimeStamp())
            );
        }
        if (Objects.nonNull(rq.getEventDataList()) &&
            Objects.nonNull(rq.getEventDataList().getClientDefinedAttributeList()) &&
            Objects.nonNull(rq.getEventDataList().getClientDefinedAttributeList().getFact())) {
            rq.getEventDataList().getClientDefinedAttributeList().getFact().stream()
                .filter(attribute -> Objects.equals(attribute.getName(),FIRST_SIGN_TIME.getAttributeName()))
                .filter(attribute -> Objects.nonNull(attribute.getValue()))
                .forEach(attribute -> attribute.setValue(normalizationLocalDateTime(attribute.getValue()).toString()));
        }
    }

    static LocalDateTime normalizationLocalDateTime(LocalDateTime localDateTime) {
        var formatter = DateTimeFormatter.ofPattern(PATTERN_LOCAL_DATE_TIME);
        return LocalDateTime.parse(localDateTime.format(formatter)).plusHours(HOURS);
    }

    static LocalDateTime normalizationLocalDateTime(String localDateTime) {
        return normalizationLocalDateTime(LocalDateTime.parse(localDateTime));
    }
}
