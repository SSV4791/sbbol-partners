package ru.sberbank.pprb.sbbol.partners.service.mapper.fraud;

import org.junit.jupiter.api.Test;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.request.ClientDefinedAttributeList;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.fraud.BaseFraudMetaDataMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.fraud.FraudClientDataMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.fraud.FraudDeviceRequestMapperImpl;
import ru.sberbank.pprb.sbbol.partners.mapper.fraud.SignedAccountFraudMetaDataMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.fraud.SignedAccountFraudMetaDataMapperImpl;
import ru.sberbank.pprb.sbbol.partners.model.FraudMetaData;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.BaseFraudMetaDataMapper.PPRB_BROWSER;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.DBO_OPERATION;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.FIRST_SIGN_CHANNEL;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.FIRST_SIGN_CRYPTOPROFILE;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.FIRST_SIGN_CRYPTOPROFILE_TYPE;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.FIRST_SIGN_EMAIL;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.FIRST_SIGN_IMSI;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.FIRST_SIGN_IP_ADDRESS;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.FIRST_SIGN_LOGIN;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.FIRST_SIGN_PHONE;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.FIRST_SIGN_SOURCE;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.FIRST_SIGN_TYPE;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.SignedAccountFraudMetaDataMapper.DBO_OPERATION_NAME_FOR_SIGN_ACCOUNT;
import static ru.sberbank.pprb.sbbol.partners.model.fraud.FraudEventType.SIGN_ACCOUNT;

class SignedAccountAbstractFraudMetaDataMapperTest extends AbstractFraudMetaDataMapperTest {

    private final SignedAccountFraudMetaDataMapper fraudMapper = new SignedAccountFraudMetaDataMapperImpl(
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

        var actualFraudRequest = fraudMapper.mapToAnalyzeRequest(metaData, partnerEntity, accountEntity);
        assertThat(actualFraudRequest)
            .isNotNull();

        var actualMessageHeader = actualFraudRequest.getMessageHeader();
        checkMessageHeader(actualMessageHeader, metaData);

        var actualIdentificationData = actualFraudRequest.getIdentificationData();
        checkIdentificationData(actualIdentificationData, clientData, accountEntity.getUuid().toString());

        var actualDeviceRequest = actualFraudRequest.getDeviceRequest();
        checkDeviceRequest(actualDeviceRequest, deviceRequest);

        var actualEventDateList = actualFraudRequest.getEventDataList();
        checkEventDataList(actualEventDateList, eventData, SIGN_ACCOUNT);
        checkClientDefinedAttributes(actualEventDateList.getClientDefinedAttributeList(), metaData, partnerEntity);

        var actualChannelIndicator = actualFraudRequest.getChannelIndicator();
        checkChannelIndicator(actualChannelIndicator, metaData);

        var actualClientDefinedChannelIndicator = actualFraudRequest.getClientDefinedChannelIndicator();
        checkClientDefinedChannelIndicator(actualClientDefinedChannelIndicator, metaData.getChannelInfo());
    }

    @Override
    protected BaseFraudMetaDataMapper getFraudMetaDataMapper() {
        return fraudMapper;
    }

    protected void checkClientDefinedAttributes(
        ClientDefinedAttributeList actualClientDefinedAttributeList,
        FraudMetaData metaData,
        PartnerEntity partnerEntity
    ) {
        var clientData = metaData.getClientData();
        var requestData = metaData.getDeviceRequest();

        super.checkClientDefinedAttributes(actualClientDefinedAttributeList, clientData, partnerEntity, requestData);

        var attributeMap = getClientDefinedAttributeMap(actualClientDefinedAttributeList.getFact());

        assertThat(attributeMap.get(FIRST_SIGN_IP_ADDRESS.getAttributeName()))
            .isEqualTo(requestData.getIpAddress());
        assertThat(attributeMap.get(FIRST_SIGN_LOGIN.getAttributeName()))
            .isEqualTo(clientData.getLogin());
        assertThat(attributeMap.get(FIRST_SIGN_PHONE.getAttributeName()))
            .isEqualTo(clientData.getPhone());
        assertThat(attributeMap.get(FIRST_SIGN_EMAIL.getAttributeName()))
            .isEqualTo(clientData.getEmail());
        assertThat(attributeMap.get(FIRST_SIGN_SOURCE.getAttributeName()))
            .isEqualTo(PPRB_BROWSER);
        assertThat(attributeMap.get(FIRST_SIGN_IMSI.getAttributeName()))
            .isEqualTo(clientData.getImsi());
        assertThat(attributeMap.get(FIRST_SIGN_CRYPTOPROFILE.getAttributeName()))
            .isEqualTo(metaData.getCryptoProfileData().getName());
        assertThat(attributeMap.get(FIRST_SIGN_CRYPTOPROFILE_TYPE.getAttributeName()))
            .isEqualTo(metaData.getCryptoProfileData().getType());
        assertThat(attributeMap.get(FIRST_SIGN_CHANNEL.getAttributeName()))
            .isEqualTo(metaData.getCryptoProfileData().getSignChannel());
        assertThat(attributeMap.get(FIRST_SIGN_TYPE.getAttributeName()))
            .isEqualTo(metaData.getCryptoProfileData().getTitleName());
        assertThat(attributeMap.get(DBO_OPERATION.getAttributeName()))
            .isEqualTo(DBO_OPERATION_NAME_FOR_SIGN_ACCOUNT);
    }
}
