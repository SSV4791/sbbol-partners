package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.DocumentTypeEntity;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeFilter;

import java.util.List;

public interface DocumentDictionaryViewRepository {

    /**
     * Получение типов документов по фильтру
     *
     * @param filter фильтр
     * @return Типы документов
     */
    List<DocumentTypeEntity> findByFilter(DocumentTypeFilter filter);
}
