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
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.model.FraudMetaData;
import ru.sberbank.pprb.sbbol.partners.model.fraud.FraudEventType;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static ru.sberbank.pprb.sbbol.partners.mapper.fraud.ClientDefinedAttributeType.DBO_OPERATION;

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

    String DBO_OPERATION_NAME_FOR_DELETE_PARTNER = "Удаление контрагента из справочника контрагентов";

    @InheritConfiguration
    AnalyzeRequest mapToAnalyzeRequest(FraudMetaData metaData, @Context PartnerEntity partner);

    @Named("toEventDataList")
    default EventDataList toEventDataList(FraudMetaData metaData) {
        var eventDataList = toEventDataList(FraudEventType.DELETE_PARTNER, metaData);
        addDeletedPartnerClientDefinedAttributeList(eventDataList.getClientDefinedAttributeList().getFact());
        return eventDataList;
    }

    private void addDeletedPartnerClientDefinedAttributeList(List<Attribute> attributes) {
        if (isNull(attributes)) {
            return;
        }
        attributes.add(new Attribute(DBO_OPERATION.getAttributeName(), DBO_OPERATION_NAME_FOR_DELETE_PARTNER, DBO_OPERATION.getAttributeType()));
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
