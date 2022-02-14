package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.model.AccountSignInfo;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfo;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfoResponse;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignResponse;

/**
 * Сервис по работе с подписями счётов Партнера
 */
public interface AccountSignService {

    /**
     * Получение информации о подписи счетов партнеров по заданному фильтру
     *
     * @param filter фильтр для поиска информации о подписей счетов Партнера
     * @return список информации по подписям счетов партнера, удовлетворяющих заданному фильтру
     */
    AccountsSignResponse getAccountsSign(AccountsSignFilter filter);

    /**
     * Создание информации о подписи счетов Партнера
     *
     * @param accountsSign новые данные о подписях счетов Партнера
     * @return Информация о подписях счетов партнера
     */
    AccountsSignInfoResponse createAccountsSign(AccountsSignInfo accountsSign);

    /**
     * Удаление информации о подписи счёта Партнера
     *
     * @param digitalId Идентификатор личного кабинета
     * @param accountId Идентификатор счёта
     */
    void deleteAccountSign(String digitalId, String accountId);

    /**
     * Получение информации о подписи счёта Партнера
     *
     * @param digitalId Идентификатор личного кабинета
     * @param accountId Идентификатор счёта
     * @return информация о подписи счёта Партнера
     */
    AccountSignInfo getAccountSign(String digitalId, String accountId);

}
