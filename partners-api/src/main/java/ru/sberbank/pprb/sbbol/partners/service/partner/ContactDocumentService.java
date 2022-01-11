package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.model.Document;
import ru.sberbank.pprb.sbbol.partners.model.DocumentResponse;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsFilter;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;

/**
 * Сервис по работе с документами
 */
public interface ContactDocumentService {

    /**
     * Получение документа
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param id        Идентификатор счёта
     * @return Документ
     */
    DocumentResponse getDocument(String digitalId, String id);

    /**
     * Получение списка документов по заданному фильтру
     *
     * @param documentsFilter фильтр для поиска документов
     * @return список документов партнера, удовлетворяющих заданному фильтру
     */
    DocumentsResponse getDocuments(DocumentsFilter documentsFilter);

    /**
     * Создание нового документа Контакта
     *
     * @param document данные документа Контакта
     * @return Документ
     */
    DocumentResponse saveDocument(Document document);

    /**
     * Обновление документа Контакта
     *
     * @param document новые данные документа Контакта
     * @return Документ
     */
    DocumentResponse updateDocument(Document document);

    /**
     * Удаление документа Контакта
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param id        Идентификатор документа Контакта
     */
    Error deleteDocument(String digitalId, String id);
}
