package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.model.Document;
import ru.sberbank.pprb.sbbol.partners.model.DocumentResponse;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsFilter;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;

/**
 * Сервис по работе с документами
 */
public interface DocumentService {

    /**
     * Получение документа
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param id        Идентификатор документа
     * @return Документ
     */
    DocumentResponse getDocument(String digitalId, String id);

    /**
     * Получение списка документов партнеров по заданному фильтру
     *
     * @param documentsFilter фильтр для поиска документов
     * @return список документов, удовлетворяющих заданному фильтру
     */
    DocumentsResponse getDocuments(DocumentsFilter documentsFilter);

    /**
     * Создание нового документа
     *
     * @param document данные документа
     * @return Документ
     */
    DocumentResponse saveDocument(Document document);

    /**
     * Обновление документа
     *
     * @param document новые данные документа
     * @return Документ
     */
    DocumentResponse updateDocument(Document document);

    /**
     * Удаление документа
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param id        Идентификатор документа
     */
    void deleteDocument(String digitalId, String id);
}
