package ru.sberbank.pprb.sbbol.partners.legacy;

import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartyCheckRequisites;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartyFilter;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartyView;
import ru.sberbank.pprb.sbbol.partners.legacy.model.Counterparty;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartyCheckRequisitesResult;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartySignData;
import ru.sberbank.pprb.sbbol.partners.legacy.model.ListResponse;

import java.util.List;
import java.util.Set;

/**
 * Адаптер работы с легаси
 */
public interface LegacySbbolAdapter {

    /**
     * Удаление контрагента по pprbGuid
     *
     * @param digitalId идентификатор договора ДБО организации в фабрике ППРБ
     * @param pprbGuid  идентификатор контрагента в ППРБ
     * @param xRequestId  идентификатор запроса
     */
    void delete(String digitalId, String pprbGuid, String xRequestId);

    /**
     * Добавить нового контрагента
     *
     * @param digitalId    идентификатор договора ДБО организации в фабрике ППРБ
     * @param counterparty данные контрагента
     * @param xRequestId  идентификатор запроса
     * @return Донные сохраненного контрагента
     */
    Counterparty create(String digitalId, Counterparty counterparty, String xRequestId);

    /**
     * Изменение существующего контрагента
     *
     * @param digitalId    идентификатор договора ДБО организации в фабрике ППРБ
     * @param counterparty данные контрагента
     * @param xRequestId  идентификатор запроса
     * @return Донные измененного контрагента
     */
    Counterparty update(String digitalId, Counterparty counterparty, String xRequestId);

    /**
     * Получить контрагентов организации
     *
     * @param digitalId идентификатор договора ДБО организации в фабрике ППРБ
     * @return список контрагентов
     */
    List<CounterpartyView> list(String digitalId);

    /**
     * Получить контрагента по pprbGuid
     *
     * @param digitalId идентификатор договора ДБО организации в фабрике ППРБ
     * @param pprbGuid  идентификатор контрагента в ППРБ
     * @return Донные контрагента
     */
    Counterparty getByPprbGuid(String digitalId, String pprbGuid);

    /**
     * Сохранение подписи контрагента
     *
     * @param digitalUserId идентификатор пользователя подписавшего документ
     * @param signData      данные подписи контрагента
     * @param xRequestId    идентификатор запроса
     */
    void saveSign(String digitalUserId, CounterpartySignData signData, String xRequestId);

    /**
     * Удаление подписи контрагента
     *
     * @param digitalId идентификатор договора ДБО организации в фабрике ППРБ
     * @param pprbGuid  идентификатор контрагента в ППРБ
     * @param xRequestId  идентификатор запроса
     */
    void removeSign(String digitalId, String pprbGuid, String xRequestId);

    /**
     * Получить контрагентов организации с пагинацией
     *
     * @param digitalId идентификатор договора ДБО организации в фабрике ППРБ
     * @param filter    фильтр контрагентов
     * @return список контрагентов
     */
    ListResponse<CounterpartyView> viewRequest(String digitalId, CounterpartyFilter filter);

    /**
     * Получить признак миграции клиента
     *
     * @param digitalId идентификатор договора ДБО организации в фабрике ППРБ
     * @return мигрирован клиент или нет
     */
    boolean checkNotMigration(String digitalId);

    /**
     * Получить список ИНН поставщиков ЖКУ
     *
     * @param digitalId        идентификатор договора ДБО организации в фабрике ППРБ
     * @param counterpartyInns список ИНН контрагентов для проверки
     * @return список ИНН поставщиков ЖКУ
     */
    Set<String> getHousingInn(String digitalId, Set<String> counterpartyInns);

    /**
     * Проверка контрагента по реквизитам
     *
     * @param request запрос на проверку
     * @return результат проверки контрагента
     */
    CounterpartyCheckRequisitesResult checkRequisites(CounterpartyCheckRequisites request);
}
