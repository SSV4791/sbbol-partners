package ru.sberbank.pprb.sbbol.migration.gku.repository.common;

import org.springframework.data.domain.PageRequest;
import ru.sberbank.pprb.sbbol.partners.entity.partner.GkuInnEntity;

import java.util.List;

public interface MigrationGkuDeleteRepository {

    List<GkuInnEntity> findAllOldValue(PageRequest pageable);
}
