package ru.sberbank.pprb.sbbol.partners.service.replication;

import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.SignEntity;
import ru.sberbank.pprb.sbbol.partners.model.Account;

import java.util.List;
import java.util.UUID;

public interface ReplicationService {

    /**
     * Сохранение контрагентов в СББОЛ
     *
     * @param accounts Счёта на обновление
     */
    void saveCounterparty(List<Account> accounts);

    /**
     * Сохранение контрагента в СББОЛ
     *
     * @param account Счёт на обновление
     */
    void saveCounterparty(Account account);

    /**
     * Удаление контрагентов в СББОЛ
     *
     * @param accounts Счета на удаление
     */
    void deleteCounterparties(List<AccountEntity> accounts);

    /**
     * Удаление контрагента в СББОЛ
     *
     * @param account Счёт на удаление
     */
    void deleteCounterparty(AccountEntity account);

    /**
     * Сохранение подписи контрагента в СББОЛ
     *
     * @param digitalId Цифровой идентификатор клиента
     * @param sign Подпись по контрагенту
     */
    void saveSign(String digitalId, String digitalUserId, SignEntity sign);

    /**
     * Удаление подписи контрагента в СББОЛ
     *
     * @param digitalId Цифровой идентификатор клиента
     * @param accountUuid Идентификатор контрагента
     */
    void deleteSign(String digitalId, UUID accountUuid);
}

