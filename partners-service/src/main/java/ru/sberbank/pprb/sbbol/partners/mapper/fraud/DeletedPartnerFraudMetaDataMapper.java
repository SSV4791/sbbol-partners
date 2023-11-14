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
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.request.EventDataList;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
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
    AnalyzeRequest mapToAnalyzeRequest(FraudMetaData metaData, @Context PartnerEntity partner);

    @Named("toEventDataList")
    default EventDataList toEventDataList(FraudMetaData metaData) {
        return toEventDataList(FraudEventType.DELETE_PARTNER, metaData);
    }

    @AfterMapping
    default void afterMappingAnalyzeRequest(@MappingTarget AnalyzeRequest rq, @Context PartnerEntity partner) {
        addClientDefinedAttributeList(rq.getEventDataList().getClientDefinedAttributeList().getFact(), partner);
        var identificationData = rq.getIdentificationData();
        if (nonNull(identificationData) && nonNull(partner) && nonNull(partner.getUuid())) {
            identificationData.setClientTransactionId(partner.getUuid().toString());
        }
        addTimeToAnalyzeRequest(rq);
    }
}
