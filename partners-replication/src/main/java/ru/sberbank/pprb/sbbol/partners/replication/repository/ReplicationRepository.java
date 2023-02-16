package ru.sberbank.pprb.sbbol.partners.replication.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sberbank.pprb.sbbol.partners.replication.entity.ReplicationEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReplicationRepository
    extends CrudRepository<ReplicationEntity, UUID>, ReplicationViewRepository {

    List<ReplicationEntity> findByEntityId(UUID entityId);
}
