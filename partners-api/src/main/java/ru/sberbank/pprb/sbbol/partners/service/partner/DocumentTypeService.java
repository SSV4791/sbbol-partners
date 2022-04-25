package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeChange;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeCreate;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeFilter;
import ru.sberbank.pprb.sbbol.partners.model.DocumentTypeResponse;
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

    /**
     * Создание нового типа документа
     *
     * @param document типы документа Контакта
     * @return Документ
     */
    DocumentTypeResponse saveDocument(DocumentTypeCreate document);

    /**
     * Обновление типа документа Контакта
     *
     * @param document новые данные документа Контакта
     * @return Документ
     */
    DocumentTypeResponse updateDocument(DocumentTypeChange document);

    /**
     * Удаление документа Контакта
     *
     * @param id уникальный идентификатор документа Контакта
     * @return void
     */
    void deleteDocument(String id);
}
