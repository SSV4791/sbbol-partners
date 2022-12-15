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
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
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
public interface SignedAccountFraudMetaDataMapper extends BaseFraudMetaDataMapper {

    @InheritConfiguration
    @Mapping(target = "clientDefinedAttributeList", expression = "java(toCounterPartyClientDefinedAttributesForSignedAccount(metaData, partner, account))")
    CounterPartySendToAnalyzeRq mapToCounterPartySendToAnalyzeRq(FraudMetaData metaData, @Context PartnerEntity partner, @Context AccountEntity account);

    default CounterPartyClientDefinedAttributes toCounterPartyClientDefinedAttributesForSignedAccount(FraudMetaData metaData, PartnerEntity partner, AccountEntity account) {
        var counterPartyClientDefinedAttributes = toCounterPartyClientDefinedAttributes(metaData, partner);
        if (counterPartyClientDefinedAttributes == null) {
            return null;
        }
        if (account != null) {
            if (account.getBank() != null) {
                counterPartyClientDefinedAttributes.setReceiverBicSwift(account.getBank().getBic());
            }
            counterPartyClientDefinedAttributes.setReceiverAccount(account.getAccount());
            counterPartyClientDefinedAttributes.setUserComment(account.getAccount());
        }
        if (metaData.getEventData() != null) {
            counterPartyClientDefinedAttributes.setFirstSignTime(metaData.getEventData().getTimeOfOccurrence().toLocalDateTime());
        }
        if (metaData.getDeviceRequest() != null) {
            counterPartyClientDefinedAttributes.setFirstSignIpAddress(metaData.getDeviceRequest().getIpAddress());
        }
        if (metaData.getClientData() != null) {
            counterPartyClientDefinedAttributes.setFirstSignLogin(metaData.getClientData().getLogin());
            counterPartyClientDefinedAttributes.setFirstSignPhone(metaData.getClientData().getPhone());
            counterPartyClientDefinedAttributes.setFirstSignEmail(metaData.getClientData().getEmail());
        }
        if (metaData.getCryptoProfileData() != null) {
            counterPartyClientDefinedAttributes.setFirstSignCryptoprofile(metaData.getCryptoProfileData().getName());
            counterPartyClientDefinedAttributes.setFirstSignCryptoprofileType(metaData.getCryptoProfileData().getType());
            counterPartyClientDefinedAttributes.setFirstSignChannel(metaData.getCryptoProfileData().getSignChannel());
            counterPartyClientDefinedAttributes.setFirstSignType(metaData.getCryptoProfileData().getTitleName());
        }
        counterPartyClientDefinedAttributes.setFirstSignSource(PPRB_BROWSER);
        return counterPartyClientDefinedAttributes;
    }

    @Named("toEventData")
    default CounterPartyEventData toEventData(FraudEventData eventData) {
        return toEventData(FraudEventType.SIGN_ACCOUNT, eventData.getTimeOfOccurrence().toLocalDateTime());
    }

    @AfterMapping
    default void afterMappingCounterPartySendToAnalyzeRq(@MappingTarget CounterPartySendToAnalyzeRq rq, @Context PartnerEntity partner, @Context AccountEntity account) {
        var identificationData = rq.getIdentificationData();
        if (nonNull(identificationData) && nonNull(account) && nonNull(account.getUuid())) {
            identificationData.setClientTransactionId(account.getUuid().toString());
        }
    }
}
