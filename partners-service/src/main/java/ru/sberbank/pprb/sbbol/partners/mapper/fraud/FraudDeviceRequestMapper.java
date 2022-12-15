package ru.sberbank.pprb.sbbol.partners.mapper.fraud;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.counterparty.CounterPartyDeviceRequest;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.model.FraudDeviceRequest;

@Loggable
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface FraudDeviceRequestMapper {

    @Mapping(target = "devicePrint", source = "devicePrint")
    @Mapping(target = "mobSdkData", source = "mobileSdkData")
    @Mapping(target = "httpAccept", source = "httpAccept")
    @Mapping(target = "httpAcceptChars", source = "httpAcceptChars")
    @Mapping(target = "httpAcceptEncoding", source = "httpAcceptEncoding")
    @Mapping(target = "httpAcceptLanguage", source = "httpAcceptLanguage")
    @Mapping(target = "httpReferrer", source = "httpReferrer")
    @Mapping(target = "ipAddress", source = "ipAddress")
    @Mapping(target = "userAgent", source = "userAgent")
    CounterPartyDeviceRequest mapToCounterPartyDeviceRequest(FraudDeviceRequest deviceRequest);
}
