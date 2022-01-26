package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.model.AccountsSignFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignResponse;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignStatus;

/**
 * Сервис по работе с подписями счётов Партнера
 */
public interface AccountSignService {

    /**
     * Получение информации о подписи счётов партнеров по заданному фильтру
     *
     * @param filter фильтр для поиска информации о подписей счётов Партнера
     * @return список информации по подписям счётов партнера, удовлетворяющих заданному фильтру
     */
    AccountsSignResponse getAccountsSign(AccountsSignFilter filter);

    /**
     * Обновление информации о подписи счетов Партнера
     *
     * @param accountsSignStatus новые данные о подписях счётов Партнера
     * @return Информация о подписях счетов партнера
     */
    AccountsSignResponse updateAccountSign(AccountsSignStatus accountsSignStatus);
}
