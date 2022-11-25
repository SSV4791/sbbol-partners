package ru.sberbank.pprb.sbbol.partners.service.mapper.fraud;

import org.junit.jupiter.api.Test;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.counterparty.CounterPartyClientDefinedAttributes;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.fraud.FraudClientDataMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.fraud.FraudDeviceRequestMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.fraud.BaseFraudMetaDataMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.fraud.SignedAccountFraudMetaDataMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.fraud.SignedAccountFraudMetaDataMapperImpl;
import ru.sberbank.pprb.sbbol.partners.model.FraudClientData;
import ru.sberbank.pprb.sbbol.partners.model.FraudCryptoProfileData;
import ru.sberbank.pprb.sbbol.partners.model.FraudDeviceRequest;
import ru.sberbank.pprb.sbbol.partners.model.FraudEventData;
import ru.sberbank.pprb.sbbol.partners.model.FraudMetaData;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.BaseFraudMetaDataMapper.PPRB_BROWSER;
import static ru.sberbank.pprb.sbbol.partners.model.fraud.FraudEventType.SIGN_ACCOUNT;

class SignedAccountAbstractFraudMetaDataMapperTest extends AbstractFraudMetaDataMapperTest {

    private SignedAccountFraudMetaDataMapper fraudMapper = new SignedAccountFraudMetaDataMapperImpl(
        new FraudClientDataMapperImpl(),
        new FraudDeviceRequestMapperImpl()
    );

    @Test
    void testMapToCounterPartySendToAnalyzeRq() {
        var metaData = factory.manufacturePojo(FraudMetaData.class);
        var partnerEntity = factory.manufacturePojo(PartnerEntity.class);
        var accountEntity = factory.manufacturePojo(AccountEntity.class);

        var clientData = metaData.getClientData();
        var deviceRequest = metaData.getDeviceRequest();
        var eventData = metaData.getEventData();
        var eventType = SIGN_ACCOUNT;
        var requestData = metaData.getDeviceRequest();
        var cryptoProfileData = metaData.getCryptoProfileData();

        var actualFraudRequest = fraudMapper.mapToCounterPartySendToAnalyzeRq(metaData, partnerEntity, accountEntity);
        assertThat(actualFraudRequest)
            .isNotNull();

        var actualMessageHeader = actualFraudRequest.getMessageHeader();
        checkCounterPartyMessageHeader(actualMessageHeader, metaData);

        var actualIdentificationData = actualFraudRequest.getIdentificationData();
        checkCounterPartyIdentificationData(actualIdentificationData, clientData, accountEntity.getUuid().toString());

        var actualDeviceRequest = actualFraudRequest.getDeviceRequest();
        checkCounterPartyDeviceRequest(actualDeviceRequest, deviceRequest);

        var actualEventDate = actualFraudRequest.getEventData();
        checkCounterPartyEventData(actualEventDate, eventData, eventType);

        var actualChannelIndicator = actualFraudRequest.getChannelIndicator();
        checkChannelIndicator(actualChannelIndicator, metaData.getChannelIndicator());

        var actualClientDefinedChannelIndicator = actualFraudRequest.getClientDefinedChannelIndicator();
        checkClientDefinedChannelIndicator(actualClientDefinedChannelIndicator);

        var actualClientDefinedAttributeList = actualFraudRequest.getClientDefinedAttributeList();
        checkCounterPartyClientDefinedAttributes(actualClientDefinedAttributeList, clientData, partnerEntity,
            accountEntity, eventData, cryptoProfileData, requestData
            );
    }

    protected void checkCounterPartyClientDefinedAttributes(
        CounterPartyClientDefinedAttributes actualClientDefinedAttributeList,
        FraudClientData clientData,
        PartnerEntity partnerEntity,
        AccountEntity accountEntity,
        FraudEventData eventData,
        FraudCryptoProfileData cryptoProfileData,
        FraudDeviceRequest requestData
    ) {
        super.checkCounterPartyClientDefinedAttributes(actualClientDefinedAttributeList, clientData, partnerEntity, requestData);
        assertThat(actualClientDefinedAttributeList.getReceiverBicSwift())
            .isEqualTo(accountEntity.getBank().getBic());
        assertThat(actualClientDefinedAttributeList.getReceiverAccount())
            .isEqualTo(accountEntity.getAccount());
        assertThat(actualClientDefinedAttributeList.getUserComment())
            .isEqualTo(accountEntity.getAccount());
        assertThat(actualClientDefinedAttributeList.getFirstSignTime())
            .isEqualTo(eventData.getTimeOfOccurrence().toLocalDateTime());
        assertThat(actualClientDefinedAttributeList.getFirstSignIpAddress())
            .isEqualTo(requestData.getIpAddress());
        assertThat(actualClientDefinedAttributeList.getFirstSignLogin())
            .isEqualTo(clientData.getLogin());
        assertThat(actualClientDefinedAttributeList.getFirstSignCryptoprofile())
            .isEqualTo(cryptoProfileData.getName());
        assertThat(actualClientDefinedAttributeList.getFirstSignCryptoprofileType())
            .isEqualTo(cryptoProfileData.getType());
        assertThat(actualClientDefinedAttributeList.getFirstSignChannel())
            .isEqualTo(cryptoProfileData.getSignChannel());
        assertThat(actualClientDefinedAttributeList.getFirstSignType())
            .isEqualTo(cryptoProfileData.getTitleName());
        assertThat(actualClientDefinedAttributeList.getFirstSignPhone())
            .isEqualTo(clientData.getPhone());
        assertThat(actualClientDefinedAttributeList.getFirstSignEmail())
            .isEqualTo(clientData.getEmail());
        assertThat(actualClientDefinedAttributeList.getFirstSignSource())
            .isEqualTo(PPRB_BROWSER);
    }

    @Override
    protected BaseFraudMetaDataMapper getFraudMetaDataMapper() {
        return fraudMapper;
    }
}
