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

        var actualChannelIndicator = actualFraudRequest.getChannelIndicator();
        checkChannelIndicator(actualChannelIndicator, metaData);

        var actualClientDefinedChannelIndicator = actualFraudRequest.getClientDefinedChannelIndicator();
        checkClientDefinedChannelIndicator(actualClientDefinedChannelIndicator, metaData.getChannelInfo());
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
            .isEqualTo(PPRB_BROWSER);
    }

    @Override
    protected BaseFraudMetaDataMapper getFraudMetaDataMapper() {
        return fraudMapper;
    }
}
