package ru.sberbank.pprb.sbbol.partners.service.replication;

import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.model.Account;

import java.util.List;
import java.util.UUID;

public interface ReplicationService {

    ReplicationServiceType getServiceType();

    /**
     * Создание контрагентов в СББОЛ
     *
     * @param accounts Счёта на создание
     */
    void createCounterparty(List<Account> accounts);

    /**
     * Создание контрагента в СББОЛ
     *
     * @param account Счёт на создание
     */
    void createCounterparty(Account account);

    /**
     * Обновление контрагентов в СББОЛ
     *
     * @param accounts Счёта на обновление
     */
    void updateCounterparty(List<Account> accounts);

    /**
     * Обновление контрагента в СББОЛ
     *
     * @param account Счёт на обновление
     */
    void updateCounterparty(Account account);

    /**
     * Удаление контрагентов в СББОЛ
     *
     * @param accounts Список счетов
     */
    void deleteCounterparties(List<AccountEntity> accounts);

    /**
     * Удаление контрагентов в СББОЛ
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param accountIds Список идентификаторов счетов
     */
    void deleteCounterparties(String digitalId, List<String> accountIds);

    /**
     * Удаление контрагента в СББОЛ
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param accountId Идентификатор счета
     */
    void deleteCounterparty(String digitalId, String accountId);

    /**
     * Сохранение подписи контрагента в СББОЛ
     *
     * @param digitalId Цифровой идентификатор клиента
     * @param accountUuid Идентификатор контрагента
     */
    void saveSign(String digitalId, UUID accountUuid);

    /**
     * Удаление подписи контрагента в СББОЛ
     *
     * @param digitalId Цифровой идентификатор клиента
     * @param accountUuid Идентификатор контрагента
     */
    void deleteSign(String digitalId, UUID accountUuid);

    UUID toUUID (String id);
}

