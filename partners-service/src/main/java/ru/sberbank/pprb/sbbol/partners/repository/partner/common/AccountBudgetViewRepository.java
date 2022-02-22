package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;

import java.util.List;

public interface AccountBudgetViewRepository {

    /**
     * Поиск бюджетных счетов
     *
     * @param digitalId       Идентификатор личного кабинета
     * @param masksConditions Список масок для поиска
     * @return Список бюджетных счетов
     */
    List<AccountEntity> findBudgetAccounts(String digitalId, List<String> masksConditions);
}
