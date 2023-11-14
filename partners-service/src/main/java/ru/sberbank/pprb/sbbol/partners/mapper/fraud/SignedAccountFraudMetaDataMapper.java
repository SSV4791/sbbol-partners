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

@Loggable
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {
        FraudClientDataMapper.class,
        FraudDeviceRequestMapper.class
    },
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface SignedAccountFraudMetaDataMapper extends BaseFraudMetaDataMapper {

    @InheritConfiguration
    AnalyzeRequest mapToAnalyzeRequest(FraudMetaData metaData, @Context PartnerEntity partner, @Context AccountEntity account);

    default void addSignedClientDefinedAttributeList(List<Attribute> attributes, FraudMetaData metaData) {
        if (isNull(metaData)) {
            return;
        }
        OffsetDateTime timeOfOccurrence = metaData.getEventData().getTimeOfOccurrence();
        attributes.add(new Attribute("firstSignTime",
            timeOfOccurrence != null ?
                timeOfOccurrence.toLocalDateTime().toString() :
                OffsetDateTime.now().toLocalDateTime().toString(), DATE_ATTRIBUTE_DATA_TYPE)
        );
        attributes.add(new Attribute("firstSignIpAddress", metaData.getDeviceRequest().getIpAddress(), STRING_ATTRIBUTE_DATA_TYPE));
        attributes.add(new Attribute("firstSignLogin", metaData.getClientData().getLogin(), STRING_ATTRIBUTE_DATA_TYPE));
        attributes.add(new Attribute("firstSignPhone", metaData.getClientData().getPhone(), STRING_ATTRIBUTE_DATA_TYPE));
        attributes.add(new Attribute("firstSignEmail", metaData.getClientData().getEmail(), STRING_ATTRIBUTE_DATA_TYPE));
        attributes.add(new Attribute("firstSignImsi", metaData.getClientData().getImsi(), STRING_ATTRIBUTE_DATA_TYPE));
        attributes.add(new Attribute("firstSignCryptoprofile", metaData.getCryptoProfileData().getName(), STRING_ATTRIBUTE_DATA_TYPE));
        attributes.add(new Attribute("firstSignCryptoprofileType", metaData.getCryptoProfileData().getType(), STRING_ATTRIBUTE_DATA_TYPE));
        attributes.add(new Attribute("firstSignChannel", metaData.getCryptoProfileData().getSignChannel(), STRING_ATTRIBUTE_DATA_TYPE));
        attributes.add(new Attribute("firstSignType", metaData.getCryptoProfileData().getTitleName(), STRING_ATTRIBUTE_DATA_TYPE));
        attributes.add(new Attribute("firstSignSource", PPRB_BROWSER, STRING_ATTRIBUTE_DATA_TYPE));
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
