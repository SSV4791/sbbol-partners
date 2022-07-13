package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.AccountPriority;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsResponse;

import java.util.List;

/**
 * Сервис по работе с счётами Партнера
 */
public interface AccountService {

    /**
     * Получение счёта Партнера
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param id        Идентификатор счёта
     * @return Счёт
     */
    Account getAccount(String digitalId, String id);

    /**
     * Получение списка счётов партнеров по заданному фильтру
     *
     * @param accountsFilter фильтр для поиска счётов Партнера
     * @return список счётов партнера, удовлетворяющих заданному фильтру
     */
    AccountsResponse getAccounts(AccountsFilter accountsFilter);

    /**
     * Создание нового счёта Партнера
     *
     * @param account данные счёта Партнера
     * @return Счёт
     */
    Account saveAccount(AccountCreate account);

    /**
     * Обновление счёта Партнера
     *
     * @param account новые данные счёта Партнера
     * @return Счёт
     */
    Account updateAccount(AccountChange account);

    /**
     * Удаление счёта Партнера
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param ids       Идентификаторы счетов Партнера
     */
    void deleteAccounts(String digitalId, List<String> ids);

    /**
     * Изменение приоритетного счета
     *
     * @param accountPriority данные по приоритетному счёту
     */
    Account changePriority(AccountPriority accountPriority);

}
