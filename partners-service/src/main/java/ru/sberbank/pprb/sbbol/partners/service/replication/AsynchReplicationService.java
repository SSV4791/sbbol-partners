package ru.sberbank.pprb.sbbol.partners.service.replication;

import ru.sberbank.pprb.sbbol.partners.legacy.model.Counterparty;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartySignData;

public interface AsynchReplicationService {

    /**
     * Включен/Выключен сервис
     * @return true - сервис включен
     */
    boolean isEnable();

    /**
     * Создание контрагента в СББОЛ
     *
     * @param digitalId Цифровой идентификатор клиента
     * @param counterparty Контрагент на создание
     */
    void createCounterparty(String digitalId, Counterparty counterparty);

    /**
     * Изменение контрагента в СББОЛ
     *
     * @param digitalId Цифровой идентификатор клиента
     * @param counterparty Контрагент на обновление
     */
    void updateCounterparty(String digitalId, Counterparty counterparty);

    /**
     * Удаление контрагента в СББОЛ
     *
     * @param digitalId Цифровой идентификатор клиента
     * @param counterpartyId Идентификатор контрагент на удаление
     */
    void deleteCounterparty(String digitalId, String counterpartyId);

    /**
     * Создание подписи контрагента в СББОЛ
     *
     * @param digitalId Цифровой идентификатор клиента
     * @param signData Данные по подписи
     */
    void createSign(String digitalId, CounterpartySignData signData);

    /**
     * Создание подписи контрагента в СББОЛ
     *
     * @param digitalId Цифровой идентификатор клиента
     * @param counterpartyId Идентификатор контрагента
     */
    void deleteSign(String digitalId, String counterpartyId);
}

