package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentEntity;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsFilter;

import java.util.List;

public interface DocumentViewRepository {

    List<DocumentEntity> findByFilter(DocumentsFilter filter);
}
