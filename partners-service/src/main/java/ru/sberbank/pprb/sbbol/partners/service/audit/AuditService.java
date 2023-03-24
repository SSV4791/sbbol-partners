package ru.sberbank.pprb.sbbol.partners.service.audit;

import java.util.Map;

public interface AuditService {

    /**
     * Отправка событий Аудита
     *
     * @param eventType Тип события
     * @param value     передаваемая в аудит сущность
     * @param <T>       Тип сущности
     */
    <T> void send(String eventType, T value);

    /**
     * Отправка событий Аудита
     *
     * @param eventType   Тип события
     * @param eventParams передаваемые в аудит параметры события
     */
    void send(String eventType, Map<String, String> eventParams);
}
