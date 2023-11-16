package ru.sberbank.pprb.sbbol.partners.service.mapper.fraud;

import org.junit.jupiter.api.Test;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.request.ClientDefinedAttributeList;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.fraud.BaseFraudMetaDataMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.fraud.DeletedPartnerFraudMetaDataMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.fraud.DeletedPartnerFraudMetaDataMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.fraud.FraudClientDataMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.fraud.FraudDeviceRequestMapperImpl;
import ru.sberbank.pprb.sbbol.partners.model.FraudClientData;
import ru.sberbank.pprb.sbbol.partners.model.FraudDeviceRequest;
import ru.sberbank.pprb.sbbol.partners.model.FraudMetaData;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.BaseFraudMetaDataMapper.PPRB_BROWSER;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.DBO_OPERATION;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.SENDER_EMAIL;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.SENDER_IP_ADDRESS;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.SENDER_LOGIN;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.SENDER_PHONE;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.SENDER_SOURCE;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.DeletedPartnerFraudMetaDataMapper.DBO_OPERATION_NAME_FOR_DELETE_PARTNER;
import static ru.sberbank.pprb.sbbol.partners.model.fraud.FraudEventType.DELETE_PARTNER;

class DeletedPartnerAbstractFraudMetaDataMapperTest extends AbstractFraudMetaDataMapperTest {

    private final DeletedPartnerFraudMetaDataMapper fraudMapper = new DeletedPartnerFraudMetaDataMapperImpl(
        new FraudClientDataMapperImpl(),
        new FraudDeviceRequestMapperImpl()
    );

    @Test
    void testMapToCounterPartySendToAnalyzeRq() {
        var metaData = factory.manufacturePojo(FraudMetaData.class);
        var partnerEntity = factory.manufacturePojo(PartnerEntity.class);

        var clientData = metaData.getClientData();
        var deviceRequest = metaData.getDeviceRequest();
        var eventData = metaData.getEventData();

        var actualFraudRequest = fraudMapper.mapToAnalyzeRequest(metaData, partnerEntity);
        assertThat(actualFraudRequest)
            .isNotNull();

        var actualMessageHeader = actualFraudRequest.getMessageHeader();
        checkMessageHeader(actualMessageHeader, metaData);

        var actualIdentificationData = actualFraudRequest.getIdentificationData();
        checkIdentificationData(actualIdentificationData, clientData, partnerEntity.getUuid().toString());

        var actualDeviceRequest = actualFraudRequest.getDeviceRequest();
        checkDeviceRequest(actualDeviceRequest, deviceRequest);

        var actualEventDate = actualFraudRequest.getEventDataList();
        checkEventDataList(actualEventDate, eventData, DELETE_PARTNER);
        checkClientDefinedAttributes(actualEventDate.getClientDefinedAttributeList(), clientData, partnerEntity, deviceRequest);

        var actualChannelIndicator = actualFraudRequest.getChannelIndicator();
        checkChannelIndicator(actualChannelIndicator, metaData);

        var actualClientDefinedChannelIndicator = actualFraudRequest.getClientDefinedChannelIndicator();
        checkClientDefinedChannelIndicator(actualClientDefinedChannelIndicator, metaData.getChannelInfo());
    }

    protected void checkClientDefinedAttributes(
        ClientDefinedAttributeList actualClientDefinedAttributeList,
        FraudClientData clientData,
        PartnerEntity partnerEntity,
        FraudDeviceRequest requestData
    ) {
        super.checkClientDefinedAttributes(actualClientDefinedAttributeList, clientData, partnerEntity, requestData);

        var attributeMap = getClientDefinedAttributeMap(actualClientDefinedAttributeList.getFact());

        assertThat(attributeMap.get(SENDER_IP_ADDRESS.getAttributeName()))
            .isEqualTo(requestData.getIpAddress());
        assertThat(attributeMap.get(SENDER_LOGIN.getAttributeName()))
            .isEqualTo(clientData.getLogin());
        assertThat(attributeMap.get(SENDER_PHONE.getAttributeName()))
            .isEqualTo(clientData.getPhone());
        assertThat(attributeMap.get(SENDER_EMAIL.getAttributeName()))
            .isEqualTo(clientData.getEmail());
        assertThat(attributeMap.get(SENDER_SOURCE.getAttributeName()))
            .isEqualTo(PPRB_BROWSER);
        assertThat(attributeMap.get(DBO_OPERATION.getAttributeName()))
            .isEqualTo(DBO_OPERATION_NAME_FOR_DELETE_PARTNER);
    }

    @Override
    protected BaseFraudMetaDataMapper getFraudMetaDataMapper() {
        return fraudMapper;
    }
}
