package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.model.DocumentType;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeResponse;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsTypeResponse;

/**
 * Сервис по работе с типами документов
 */
public interface DocumentTypeService {

    /**
     * Получение списка документов по заданному фильтру
     *
     * @param status фильтр для поиска типов документов
     * @return список документов, удовлетворяющих заданному фильтру
     */
    DocumentsTypeResponse getDocuments(Boolean status);

    /**
     * Создание нового типа документа
     *
     * @param document данные документа Контакта
     * @return Документ
     */
    DocumentTypeResponse saveDocument(DocumentType document);

    /**
     * Обновление документа Контакта
     *
     * @param document новые данные документа Контакта
     * @return Документ
     */
    DocumentTypeResponse updateDocument(DocumentType document);
}
