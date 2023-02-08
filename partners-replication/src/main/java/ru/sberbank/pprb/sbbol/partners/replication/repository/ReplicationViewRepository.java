package ru.sberbank.pprb.sbbol.partners.replication.repository;

import ru.sberbank.pprb.sbbol.partners.replication.entity.ReplicationEntity;
import ru.sberbank.pprb.sbbol.partners.replication.repository.model.ReplicationFilter;

import java.util.List;

public interface ReplicationViewRepository {

    List<ReplicationEntity> findByFilter(ReplicationFilter filter);
}
