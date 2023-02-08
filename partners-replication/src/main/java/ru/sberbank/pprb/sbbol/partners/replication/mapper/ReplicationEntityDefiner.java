package ru.sberbank.pprb.sbbol.partners.replication.mapper;

import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType;

public interface ReplicationEntityDefiner {

    ReplicationEntityType getReplicationEntityType();

}
