package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignFilter;

import java.util.List;

public interface AccountViewRepository {

    /**
     * Получение счетов
     *
     * @param filter Фильтр для запроса счетов
     * @return Счёта
     */
    List<AccountEntity> findByFilter(AccountsFilter filter);

    /**
     * Получение информации о подписях счетов Партнера
     *
     * @param filter Фильтр для запроса информации о подписях счетов
     * @return Информация о подписях
     */
    List<AccountEntity> findByFilter(AccountsSignFilter filter);

    /**
     * Получение бюджетных счетов
     *
     * @param digitalId      Идентификатор личного кабинета
     * @param masksCondition Список масок для поиска
     * @return Список бюджетных счетов
     */
    List<AccountEntity> findBudgetAccount(String digitalId, List<String> masksCondition);
}
