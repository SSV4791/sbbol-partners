package ru.sberbank.pprb.sbbol.partners.service.mapper.fraud;

import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.DboOperation;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.request.Attribute;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.request.ClientDefinedAttributeList;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.request.DeviceRequest;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.request.EventDataList;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.request.IdentificationData;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.request.MessageHeader;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.fraud.BaseFraudMetaDataMapper;
import ru.sberbank.pprb.sbbol.partners.model.FraudChannelInfo;
import ru.sberbank.pprb.sbbol.partners.model.FraudClientData;
import ru.sberbank.pprb.sbbol.partners.model.FraudDeviceRequest;
import ru.sberbank.pprb.sbbol.partners.model.FraudEventData;
import ru.sberbank.pprb.sbbol.partners.model.FraudMetaData;
import ru.sberbank.pprb.sbbol.partners.model.fraud.FraudEventType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.BaseFraudMetaDataMapper.ANALYZE_REQUEST_TYPE;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.BaseFraudMetaDataMapper.toChannelIndicator;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.COUNTER_PARTY_ID;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.DIGITAL_ID;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.EPK_ID;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.OSB_NUMBER;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.PAYER_INN;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.PAYER_NAME;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.PRIVATE_IP_ADDRESS;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.RECEIVER_INN;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.RECEIVER_NAME;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.VSP_NUMBER;

public abstract class AbstractFraudMetaDataMapperTest extends BaseUnitConfiguration {

    public static final int HOURS = 3;

    protected abstract BaseFraudMetaDataMapper getFraudMetaDataMapper();

    protected void checkMessageHeader(
        MessageHeader actualMessageHeader,
        FraudMetaData metaData
    ) {
        assertThat(actualMessageHeader)
            .isNotNull();
        assertThat(actualMessageHeader.getRequestType())
            .isEqualTo(ANALYZE_REQUEST_TYPE);
        var expectedTimeStamp = BaseFraudMetaDataMapper.normalizationLocalDateTime(
            metaData.getEventData()
                .getTimeOfOccurrence()
                .toLocalDateTime()
        );
        assertThat(actualMessageHeader.getTimeStamp())
            .isEqualTo(expectedTimeStamp);
    }

    protected void checkIdentificationData(
        IdentificationData actualIdentificationData,
        FraudClientData clientData,
        String clientTransactionId
    ) {
        assertThat(actualIdentificationData)
            .isNotNull();
        assertThat(actualIdentificationData.getDboOperation())
            .isEqualTo(DboOperation.PARTNERS.toString());
        assertThat(actualIdentificationData.getClientTransactionId())
            .isEqualTo(clientTransactionId);
        assertThat(actualIdentificationData.getOrgName())
            .isEqualTo(clientData.getTerBankNumber());
        assertThat(actualIdentificationData.getUserName())
            .isEqualTo(clientData.getEpkId());
        assertThat(actualIdentificationData.getUserLoginName())
            .isEqualTo(clientData.getLogin());
    }

    protected void checkDeviceRequest(
        DeviceRequest actualDeviceRequest,
        FraudDeviceRequest deviceRequest
    ) {
        assertThat(actualDeviceRequest)
            .isNotNull();
        assertThat(actualDeviceRequest.getDevicePrint())
            .isEqualTo(deviceRequest.getDevicePrint());
        assertThat(actualDeviceRequest.getHttpAccept())
            .isEqualTo(deviceRequest.getHttpAccept());
        assertThat(actualDeviceRequest.getHttpAcceptChars())
            .isEqualTo(deviceRequest.getHttpAcceptChars());
        assertThat(actualDeviceRequest.getHttpAcceptEncoding())
            .isEqualTo(deviceRequest.getHttpAcceptEncoding());
        assertThat(actualDeviceRequest.getHttpReferrer())
            .isEqualTo(deviceRequest.getHttpReferrer());
        assertThat(actualDeviceRequest.getHttpAcceptLanguage())
            .isEqualTo(deviceRequest.getHttpAcceptLanguage());
        assertThat(actualDeviceRequest.getIpAddress())
            .isEqualTo(deviceRequest.getIpAddress());
        assertThat(actualDeviceRequest.getUserAgent())
            .isEqualTo(deviceRequest.getUserAgent());
        assertThat(actualDeviceRequest.getMobSdkData())
            .isEqualTo(deviceRequest.getMobileSdkData());
    }

    protected void checkEventDataList(
        EventDataList actualEventData,
        FraudEventData eventData,
        FraudEventType eventType
    ) {
        assertThat(actualEventData)
            .isNotNull();
        assertThat(actualEventData.getEventData())
            .isNotNull();
        assertThat(actualEventData.getEventData().getEventType())
            .isEqualTo(getFraudMetaDataMapper().toEventType(eventType));
        assertThat(actualEventData.getEventData().getClientDefinedEventType())
            .isEqualTo(getFraudMetaDataMapper().getClientDefinedEventType(eventData, eventType).toString());
        var expectedTimeOfOccurrence = BaseFraudMetaDataMapper.normalizationLocalDateTime(
            eventData.getTimeOfOccurrence().toLocalDateTime()
        );
        assertThat(actualEventData.getEventData().getTimeOfOccurrence())
            .isEqualTo(expectedTimeOfOccurrence);
    }

    protected void checkChannelIndicator(
        String actualChannelIndicator,
        FraudMetaData metaData) {
        assertThat(actualChannelIndicator)
            .isEqualTo(toChannelIndicator(metaData));
    }

    protected void checkClientDefinedChannelIndicator(
        String actualClientDefinedChannelIndicator,
        FraudChannelInfo channelInfo
    ) {
        assertThat(actualClientDefinedChannelIndicator)
            .hasToString(channelInfo.getClientDefinedChannelIndicator().getValue());
    }

    protected void checkClientDefinedAttributes(
        ClientDefinedAttributeList actualClientDefinedAttributeList,
        FraudClientData clientData,
        PartnerEntity partnerEntity,
        FraudDeviceRequest requestData
    ) {
        assertThat(actualClientDefinedAttributeList)
            .isNotNull();

        var attributeMap = getClientDefinedAttributeMap(actualClientDefinedAttributeList.getFact());

        assertThat(attributeMap.get(COUNTER_PARTY_ID.getAttributeName()))
            .isEqualTo(partnerEntity.getUuid().toString());
        assertThat(attributeMap.get(DIGITAL_ID.getAttributeName()))
            .isEqualTo(clientData.getDigitalId());
        assertThat(attributeMap.get(EPK_ID.getAttributeName()))
            .isEqualTo(clientData.getEpkId());
        assertThat(attributeMap.get(RECEIVER_NAME.getAttributeName()))
            .isEqualTo(getFraudMetaDataMapper().getPartnerName(partnerEntity));
        assertThat(attributeMap.get(RECEIVER_INN.getAttributeName()))
            .isEqualTo(partnerEntity.getInn());
        assertThat(attributeMap.get(PAYER_INN.getAttributeName()))
            .isEqualTo(clientData.getInn());
        assertThat(attributeMap.get(PAYER_NAME.getAttributeName()))
            .isEqualTo(clientData.getOrgName());
        assertThat(attributeMap.get(OSB_NUMBER.getAttributeName()))
            .isEqualTo(clientData.getGosbNumber());
        assertThat(attributeMap.get(VSP_NUMBER.getAttributeName()))
            .isEqualTo(clientData.getVspNumber());
        assertThat(attributeMap.get(PRIVATE_IP_ADDRESS.getAttributeName()))
            .isEqualTo(requestData.getPrivateIpAddress());
    }

    protected Map<String, String> getClientDefinedAttributeMap(List<Attribute> attributes) {
        if (CollectionUtils.isEmpty(attributes)) {
            return new HashMap<>();
        }
        return attributes.stream()
            .collect(Collectors.toMap(Attribute::getName, Attribute::getValue));
    }
}
