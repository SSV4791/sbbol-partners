package ru.sberbank.pprb.sbbol.partners.audit;


import ru.sberbank.pprb.sbbol.partners.audit.model.Event;

/**
 * Адаптер работы с сервисом Аудит
 */
public interface AuditAdapter {

    /**
     * Отправка событий Аудита
     * @param auditEvent событие аудита
     */
    void send(Event auditEvent);
}
