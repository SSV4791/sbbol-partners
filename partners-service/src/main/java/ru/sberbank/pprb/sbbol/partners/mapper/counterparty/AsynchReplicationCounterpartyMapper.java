package ru.sberbank.pprb.sbbol.partners.mapper.counterparty;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.legacy.model.Counterparty;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartySignData;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.service.replication.dto.AsynchReplicationCounterparty;

@Loggable
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AsynchReplicationCounterpartyMapper extends BaseMapper {

    @Mapping(target = "operation", source = "operation")
    @Mapping(target = "digitalId", source = "digitalId")
    @Mapping(target = "counterparty", source = "counterparty")
    @Mapping(target = "signData", source = "signData")
    AsynchReplicationCounterparty mapToAsynchReplicationCounterparty(
        AsynchReplicationCounterparty.Operation operation,
        String digitalId,
        Counterparty counterparty,
        CounterpartySignData signData
    );

    @Mapping(target = "operation", source = "operation")
    @Mapping(target = "digitalId", source = "digitalId")
    @Mapping(target = "counterparty.pprbGuid", source = "counterpartyId")
    AsynchReplicationCounterparty mapToAsynchReplicationCounterparty(
        AsynchReplicationCounterparty.Operation operation,
        String digitalId,
        String counterpartyId
    );
}
