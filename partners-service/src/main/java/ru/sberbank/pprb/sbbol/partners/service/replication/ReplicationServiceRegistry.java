package ru.sberbank.pprb.sbbol.partners.service.replication;

import java.util.Optional;

public interface ReplicationServiceRegistry {

    Optional<ReplicationService> findService(ReplicationServiceType replicationServiceType);
}
