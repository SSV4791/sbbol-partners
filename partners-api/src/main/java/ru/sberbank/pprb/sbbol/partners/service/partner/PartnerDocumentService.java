package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.model.Document;
import ru.sberbank.pprb.sbbol.partners.model.DocumentResponse;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsFilter;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;

/**
 * Сервис по работе с документами Партнера
 */
public interface PartnerDocumentService {

    /**
     * Получение документа Партнера
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param id        Идентификатор счёта
     * @return Документ
     */
    DocumentResponse getDocument(String digitalId, String id);

    /**
     * Получение списка документов партнеров по заданному фильтру
     *
     * @param documentsFilter фильтр для поиска документов Партнера
     * @return список документов партнера, удовлетворяющих заданному фильтру
     */
    DocumentsResponse getDocuments(DocumentsFilter documentsFilter);

    /**
     * Создание нового документа Партнера
     *
     * @param document данные документа Партнера
     * @return Документ
     */
    DocumentResponse saveDocument(Document document);

    /**
     * Обновление документа Партнера
     *
     * @param document новые данные документа Партнера
     * @return Документ
     */
    DocumentResponse updateDocument(Document document);

    /**
     * Удаление документа Партнера
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param id        Идентификатор документа Партнера
     */
    Error deleteDocument(String digitalId, String id);
}
