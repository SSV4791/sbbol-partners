package ru.sberbank.pprb.sbbol.partners.service.mapper.fraud;

import org.junit.jupiter.api.Test;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.counterparty.CounterPartyClientDefinedAttributes;

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
import static ru.sberbank.pprb.sbbol.partners.model.fraud.FraudEventType.DELETE_PARTNER;

class DeletedPartnerAbstractFraudMetaDataMapperTest extends AbstractFraudMetaDataMapperTest {

    private DeletedPartnerFraudMetaDataMapper fraudMapper = new DeletedPartnerFraudMetaDataMapperImpl(
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
        var eventType = DELETE_PARTNER;
        var requestData = metaData.getDeviceRequest();

        var actualFraudRequest = fraudMapper.mapToCounterPartySendToAnalyzeRq(metaData, partnerEntity);
        assertThat(actualFraudRequest)
            .isNotNull();

        var actualMessageHeader = actualFraudRequest.getMessageHeader();
        checkCounterPartyMessageHeader(actualMessageHeader, metaData);

        var actualIdentificationData = actualFraudRequest.getIdentificationData();
        checkCounterPartyIdentificationData(actualIdentificationData, clientData, partnerEntity.getUuid().toString());

        var actualDeviceRequest = actualFraudRequest.getDeviceRequest();
        checkCounterPartyDeviceRequest(actualDeviceRequest, deviceRequest);

        var actualEventDate = actualFraudRequest.getEventData();
        checkCounterPartyEventData(actualEventDate, eventData, eventType);

        var actualChannelIndicator = actualFraudRequest.getChannelIndicator();
        checkChannelIndicator(actualChannelIndicator, metaData.getChannelIndicator());

        var actualClientDefinedChannelIndicator = actualFraudRequest.getClientDefinedChannelIndicator();
        checkClientDefinedChannelIndicator(actualClientDefinedChannelIndicator);

        var actualClientDefinedAttributeList = actualFraudRequest.getClientDefinedAttributeList();
        checkCounterPartyClientDefinedAttributes(actualClientDefinedAttributeList, clientData, partnerEntity, requestData);
    }

    protected void checkCounterPartyClientDefinedAttributes(
        CounterPartyClientDefinedAttributes actualClientDefinedAttributeList,
        FraudClientData clientData,
        PartnerEntity partnerEntity,
        FraudDeviceRequest requestData
    ) {
        super.checkCounterPartyClientDefinedAttributes(actualClientDefinedAttributeList, clientData, partnerEntity, requestData);
        assertThat(actualClientDefinedAttributeList.getSenderIpAddress())
            .isEqualTo(requestData.getIpAddress());
        assertThat(actualClientDefinedAttributeList.getSenderLogin())
            .isEqualTo(clientData.getLogin());
        assertThat(actualClientDefinedAttributeList.getSenderPhone())
            .isEqualTo(clientData.getPhone());
        assertThat(actualClientDefinedAttributeList.getSenderEmail())
            .isEqualTo(clientData.getEmail());
        assertThat(actualClientDefinedAttributeList.getSenderSource())
            .isEqualTo(PPRB_BROWSER );
    }

    @Override
    protected BaseFraudMetaDataMapper getFraudMetaDataMapper() {
        return fraudMapper;
    }
}
