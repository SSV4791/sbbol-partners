package ru.sberbank.pprb.sbbol.partners.replication.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sberbank.pprb.sbbol.partners.replication.entity.ReplicationEntity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReplicationRepository
    extends CrudRepository<ReplicationEntity, UUID>, ReplicationViewRepository {

    List<ReplicationEntity> findByEntityId(UUID entityId);

    @Modifying
    @Query("delete from ReplicationEntity r where r.createDate < :startExpiredDate")
    void deleteExpiredMessages(@Param("startExpiredDate") OffsetDateTime startExpiredDate);
}
