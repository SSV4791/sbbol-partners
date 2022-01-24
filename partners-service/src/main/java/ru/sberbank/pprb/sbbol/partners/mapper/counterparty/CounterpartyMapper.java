package ru.sberbank.pprb.sbbol.partners.mapper.counterparty;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.counterparties.model.CounterpartySearchRequest;
import ru.sberbank.pprb.sbbol.partners.model.sbbol.CounterpartyCheckRequisites;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CounterpartyMapper {

    CounterpartyCheckRequisites toCounterpartyCheckRequisites(CounterpartySearchRequest request);
}
