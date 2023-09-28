package ru.sberbank.pprb.sbbol.partners.service.mapper.fraud;

import ru.sberbank.pprb.sbbol.antifraud.api.analyze.DboOperation;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.counterparty.CounterPartyClientDefinedAttributes;
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

import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.BaseFraudMetaDataMapper.ANALYZE_REQUEST_TYPE;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.BaseFraudMetaDataMapper.DBO_OPERATION_NAME;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.BaseFraudMetaDataMapper.toChannelIndicator;

public abstract class AbstractFraudMetaDataMapperTest extends BaseUnitConfiguration {

    protected abstract BaseFraudMetaDataMapper getFraudMetaDataMapper();

    protected void checkMessageHeader(
        MessageHeader actualMessageHeader,
        FraudMetaData metaData
    ) {
        assertThat(actualMessageHeader)
            .isNotNull();
        assertThat(actualMessageHeader.getRequestType())
            .isEqualTo(ANALYZE_REQUEST_TYPE);
        assertThat(actualMessageHeader.getTimeStamp())
            .isEqualTo(metaData.getEventData().getTimeOfOccurrence().toLocalDateTime());
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
        assertThat(actualEventData.getEventData().getTimeOfOccurrence())
            .isEqualTo(eventData.getTimeOfOccurrence().toLocalDateTime());
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

    protected void checkCounterPartyClientDefinedAttributes(
        CounterPartyClientDefinedAttributes actualClientDefinedAttributeList,
        FraudClientData clientData,
        PartnerEntity partnerEntity,
        FraudDeviceRequest requestData
    ) {
        assertThat(actualClientDefinedAttributeList)
            .isNotNull();
        assertThat(actualClientDefinedAttributeList.getCounterpartyId())
            .isEqualTo(partnerEntity.getUuid().toString());
        assertThat(actualClientDefinedAttributeList.getDigitalId())
            .isEqualTo(clientData.getDigitalId());
        assertThat(actualClientDefinedAttributeList.getEpkId())
            .isEqualTo(clientData.getEpkId());
        assertThat(actualClientDefinedAttributeList.getReceiverName())
            .isEqualTo(getFraudMetaDataMapper().getPartnerName(partnerEntity));
        assertThat(actualClientDefinedAttributeList.getReceiverInn())
            .isEqualTo(partnerEntity.getInn());
        assertThat(actualClientDefinedAttributeList.getPayerInn())
            .isEqualTo(clientData.getInn());
        assertThat(actualClientDefinedAttributeList.getPayerName())
            .isEqualTo(clientData.getOrgName());
        assertThat(actualClientDefinedAttributeList.getOsbNumber())
            .isEqualTo(clientData.getGosbNumber());
        assertThat(actualClientDefinedAttributeList.getVspNumber())
            .isEqualTo(clientData.getVspNumber());
        assertThat(actualClientDefinedAttributeList.getDboOperationName())
            .isEqualTo(DBO_OPERATION_NAME);
        assertThat(actualClientDefinedAttributeList.getPrivateIpAddress())
            .isEqualTo(requestData.getPrivateIpAddress());
    }
}
