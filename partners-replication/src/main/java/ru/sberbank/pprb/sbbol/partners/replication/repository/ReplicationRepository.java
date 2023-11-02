package ru.sberbank.pprb.sbbol.partners.replication.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sberbank.pprb.sbbol.partners.replication.entity.ReplicationEntity;
import ru.sberbank.pprb.sbbol.partners.replication.entity.enums.ReplicationEntityType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReplicationRepository
    extends CrudRepository<ReplicationEntity, UUID>, ReplicationViewRepository {

    List<ReplicationEntity> findByEntityId(UUID entityId);

    Optional<ReplicationEntity> getByDigitalIdAndEntityIdAndEntityType(String digitalId, UUID entityId, ReplicationEntityType entityType);
}
