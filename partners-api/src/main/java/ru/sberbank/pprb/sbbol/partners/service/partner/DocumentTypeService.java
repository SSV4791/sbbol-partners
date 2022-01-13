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
     * @param deleted признак типа документа Удален/Не удален
     * @return список типов документов, удовлетворяющих заданному фильтру
     */
    DocumentsTypeResponse getDocuments(Boolean deleted);

    /**
     * Создание нового типа документа
     *
     * @param document типы документа Контакта
     * @return Документ
     */
    DocumentTypeResponse saveDocument(DocumentType document);

    /**
     * Обновление типа документа Контакта
     *
     * @param document новые данные документа Контакта
     * @return Документ
     */
    DocumentTypeResponse updateDocument(DocumentType document);
}
