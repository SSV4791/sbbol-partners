package ru.sberbank.pprb.sbbol.partners.mapper.fraud;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.DboOperation;
import ru.sberbank.pprb.sbbol.antifraud.api.analyze.counterparty.CounterPartyIdentificationData;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.model.FraudClientData;

@Loggable
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface FraudClientDataMapper {

    @Mapping(target = "clientTransactionId", ignore = true)
    @Mapping(target = "orgName", source = "terBankCode")
    @Mapping(target = "userName", source = "epkId")
    @Mapping(target = "userLoginName", source = "login")
    @Mapping(target = "dboOperation", expression = "java(getDboOperation())")
    CounterPartyIdentificationData mapToCounterPartyIdentificationData(FraudClientData clientData);

    default DboOperation getDboOperation() {
        return DboOperation.PARTNERS;
    }
}