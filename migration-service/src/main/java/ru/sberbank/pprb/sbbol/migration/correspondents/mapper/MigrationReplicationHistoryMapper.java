package ru.sberbank.pprb.sbbol.migration.correspondents.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.sberbank.pprb.sbbol.migration.correspondents.entity.MigrationReplicationHistoryEntity;
import ru.sberbank.pprb.sbbol.migration.correspondents.entity.MigrationPartnerEntity;

@Mapper(componentModel = "spring")
public interface MigrationReplicationHistoryMapper {

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "partnerUuid", source = "source.uuid")
    @Mapping(target = "accountUuid", source = "source.account.uuid")
    @Mapping(target = "bankUuid", source = "source.account.bank.uuid")
    @Mapping(target = "bankAccountUuid", source = "source.account.bank.bankAccount.uuid")
    @Mapping(target = "emailUuid", source = "source.email.uuid")
    @Mapping(target = "phoneUuid", source = "source.phone.uuid")
    MigrationReplicationHistoryEntity toReplicationHistoryEntity(MigrationPartnerEntity source);

}
