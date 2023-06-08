package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.model.Document;
import ru.sberbank.pprb.sbbol.partners.model.DocumentChange;
import ru.sberbank.pprb.sbbol.partners.model.DocumentChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.DocumentCreate;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsFilter;
import ru.sberbank.pprb.sbbol.partners.model.DocumentsResponse;

import java.util.List;
import java.util.Set;

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
    Document getDocument(String digitalId, String id);

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
    Document saveDocument(DocumentCreate document);

    /**
     * Обновление документа
     *
     * @param document новые данные документа
     * @return Документ
     */
    Document updateDocument(DocumentChange document);

    /**
     * Частичное обновление документа
     *
     * @param document новые данные документа
     * @return Документ
     */
    Document patchDocument(DocumentChange document);

    /**
     * Создание/частичное обновление документа партнера
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param partnerId Идентификатор партнера
     * @param document  Документ для создания/частичного обновления
     */
    void saveOrPatchDocument(String digitalId, String partnerId, DocumentChangeFullModel document);

    /**
     * Создание/частичного обновление адресов
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param partnerId Идентификатор партнера
     * @param documents Список документов для создания/частичное обновление
     */
    void saveOrPatchDocuments(String digitalId, String partnerId, Set<DocumentChangeFullModel> documents);

    /**
     * Удаление документа
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param ids       Идентификаторы документов
     */
    void deleteDocuments(String digitalId, List<String> ids);
}
