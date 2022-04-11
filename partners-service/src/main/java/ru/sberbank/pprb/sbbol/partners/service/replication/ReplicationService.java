package ru.sberbank.pprb.sbbol.partners.service.replication;

import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.model.Account;

import java.util.List;

public interface ReplicationService {

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
}
