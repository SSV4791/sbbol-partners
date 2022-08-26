package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeFilter;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsTypeResponse;

/**
 * Сервис по работе с типами документов
 */
public interface DocumentTypeService {

    /**
     * Получение списка документов по заданному фильтру
     *
     * @param filter фильтр для поиска типов документов
     * @return список типов документов, удовлетворяющих заданному фильтру
     */
    DocumentsTypeResponse getDocuments(DocumentTypeFilter filter);
}
