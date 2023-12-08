package ru.sberbank.pprb.sbbol.partners.mapper.fraud;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.request.AnalyzeRequest;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.request.Attribute;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.request.EventDataList;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.model.FraudMetaData;
import ru.sberbank.pprb.sbbol.partners.model.fraud.FraudEventType;

import java.time.OffsetDateTime;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
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
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.FIRST_SIGN_TIME;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.FIRST_SIGN_TYPE;

@Loggable
@Mapper(
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {
        FraudClientDataMapper.class,
        FraudDeviceRequestMapper.class
    },
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface SignedAccountFraudMetaDataMapper extends BaseFraudMetaDataMapper {

    String DBO_OPERATION_NAME_FOR_SIGN_ACCOUNT = "Подтверждение счёта контрагента из справочника контрагентов";

    @InheritConfiguration
    AnalyzeRequest mapToAnalyzeRequest(FraudMetaData metaData, @Context PartnerEntity partner, @Context AccountEntity account);

    private void addSignedClientDefinedAttributeList(List<Attribute> attributes, FraudMetaData metaData) {
        if (isNull(metaData)) {
            return;
        }
        attributes.add(new Attribute(DBO_OPERATION.getAttributeName(), DBO_OPERATION_NAME_FOR_SIGN_ACCOUNT, DBO_OPERATION.getAttributeType()));

        OffsetDateTime timeOfOccurrence = metaData.getEventData().getTimeOfOccurrence();
        attributes.add(new Attribute(
            FIRST_SIGN_TIME.getAttributeName(),
            timeOfOccurrence != null ?
                timeOfOccurrence.toLocalDateTime().toString() :
                OffsetDateTime.now().toLocalDateTime().toString(),
            FIRST_SIGN_TIME.getAttributeType())
        );

        attributes.add(new Attribute(FIRST_SIGN_IP_ADDRESS.getAttributeName(), metaData.getDeviceRequest().getIpAddress(), FIRST_SIGN_IP_ADDRESS.getAttributeType()));
        attributes.add(new Attribute(FIRST_SIGN_LOGIN.getAttributeName(),  metaData.getClientData().getLogin(), FIRST_SIGN_LOGIN.getAttributeType()));
        attributes.add(new Attribute(FIRST_SIGN_PHONE.getAttributeName(), metaData.getClientData().getPhone(), FIRST_SIGN_PHONE.getAttributeType()));
        attributes.add(new Attribute(FIRST_SIGN_EMAIL.getAttributeName(), metaData.getClientData().getEmail(), FIRST_SIGN_EMAIL.getAttributeType()));
        attributes.add(new Attribute(FIRST_SIGN_IMSI.getAttributeName(), metaData.getClientData().getImsi(), FIRST_SIGN_IMSI.getAttributeType()));
        attributes.add(new Attribute(FIRST_SIGN_CRYPTOPROFILE.getAttributeName(), metaData.getCryptoProfileData().getName(), FIRST_SIGN_CRYPTOPROFILE.getAttributeType()));
        attributes.add(new Attribute(FIRST_SIGN_CRYPTOPROFILE_TYPE.getAttributeName(), metaData.getCryptoProfileData().getType(), FIRST_SIGN_CRYPTOPROFILE_TYPE.getAttributeType()));
        attributes.add(new Attribute(FIRST_SIGN_CHANNEL.getAttributeName(), metaData.getCryptoProfileData().getSignChannel(), FIRST_SIGN_CHANNEL.getAttributeType()));
        attributes.add(new Attribute(FIRST_SIGN_TYPE.getAttributeName(), metaData.getCryptoProfileData().getTitleName(), FIRST_SIGN_TYPE.getAttributeType()));
        attributes.add(new Attribute(FIRST_SIGN_SOURCE.getAttributeName(), PPRB_BROWSER, FIRST_SIGN_SOURCE.getAttributeType()));
    }

    @Named("toEventDataList")
    default EventDataList toEventDataList(FraudMetaData metaData) {
        var eventDataList = toEventDataList(FraudEventType.SIGN_ACCOUNT, metaData);
        addSignedClientDefinedAttributeList(eventDataList.getClientDefinedAttributeList().getFact(), metaData);
        return eventDataList;
    }

    @AfterMapping
    default void afterMappingAnalyzeRequest(@MappingTarget AnalyzeRequest rq, @Context PartnerEntity partner, @Context AccountEntity account) {
        addClientDefinedAttributeList(rq.getEventDataList().getClientDefinedAttributeList().getFact(), partner);
        addClientDefinedAttributeList(rq.getEventDataList().getClientDefinedAttributeList().getFact(), account);
        var identificationData = rq.getIdentificationData();
        if (nonNull(identificationData) && nonNull(account) && nonNull(account.getUuid())) {
            identificationData.setClientTransactionId(account.getUuid().toString());
        }
        addTimeToAnalyzeRequest(rq);
    }
}
