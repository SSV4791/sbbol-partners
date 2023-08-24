package ru.sberbank.pprb.sbbol.partners.mapper.fraud;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.counterparty.CounterPartyClientDefinedAttributes;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.counterparty.CounterPartyEventData;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.counterparty.CounterPartySendToAnalyzeRq;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.model.FraudEventData;
import ru.sberbank.pprb.sbbol.partners.model.FraudMetaData;
import ru.sberbank.pprb.sbbol.partners.model.fraud.FraudEventType;

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
public interface DeletedPartnerFraudMetaDataMapper extends BaseFraudMetaDataMapper {

    @InheritConfiguration
    @Mapping(
        target = "clientDefinedAttributeList",
        expression = "java(toCounterPartyClientDefinedAttributesForDeletedPartner(metaData, partner))"
    )
    CounterPartySendToAnalyzeRq mapToCounterPartySendToAnalyzeRq(FraudMetaData metaData, @Context PartnerEntity partner);

    @Named("toCounterPartyClientDefinedAttributesForDeletedPartner")
    default CounterPartyClientDefinedAttributes toCounterPartyClientDefinedAttributesForDeletedPartner(
        FraudMetaData metaData,
        PartnerEntity partner
    ) {
        var counterPartyClientDefinedAttributes = toCounterPartyClientDefinedAttributes(metaData, partner);
        if (counterPartyClientDefinedAttributes == null) {
            return null;
        }
        if (partner != null) {
            counterPartyClientDefinedAttributes.setUserComment(partner.getComment());
        }
        if (metaData.getDeviceRequest() != null) {
            counterPartyClientDefinedAttributes.setSenderIpAddress(metaData.getDeviceRequest().getIpAddress());
        }
        if (metaData.getClientData() != null) {
            counterPartyClientDefinedAttributes.setSenderEmail(metaData.getClientData().getEmail());
            counterPartyClientDefinedAttributes.setSenderLogin(metaData.getClientData().getLogin());
            counterPartyClientDefinedAttributes.setSenderPhone(metaData.getClientData().getPhone());
        }
        counterPartyClientDefinedAttributes.setSenderSource(PPRB_BROWSER);
        return counterPartyClientDefinedAttributes;
    }

    @Named("toEventData")
    default CounterPartyEventData toEventData(FraudEventData eventData) {
        return toEventData(FraudEventType.DELETE_PARTNER, eventData);
    }

    @AfterMapping
    default void afterMappingCounterPartySendToAnalyzeRq(@MappingTarget CounterPartySendToAnalyzeRq rq, @Context PartnerEntity partner) {
        var identificationData = rq.getIdentificationData();
        if (nonNull(identificationData) && nonNull(partner) && nonNull(partner.getUuid())) {
            identificationData.setClientTransactionId(partner.getUuid().toString());
        }
    }
}
