package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountResponse;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;

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
    AccountResponse getAccount(String digitalId, String id);

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
    AccountResponse saveAccount(Account account);

    /**
     * Обновление счёта Партнера
     *
     * @param account новые данные счёта Партнера
     * @return Счёт
     */
    AccountResponse updateAccount(Account account);

    /**
     * Удаление счёта Партнера
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param id        Идентификатор счёта Партнера
     */
    void deleteAccount(String digitalId, String id);
}
