package ru.sberbank.pprb.sbbol.partners.repository.partner.migration;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sberbank.pprb.sbbol.partners.entity.partner.IdsHistoryEntity;

import java.util.UUID;

@Repository
public interface IdsHistoryRepository extends CrudRepository<IdsHistoryEntity, UUID> {
}
