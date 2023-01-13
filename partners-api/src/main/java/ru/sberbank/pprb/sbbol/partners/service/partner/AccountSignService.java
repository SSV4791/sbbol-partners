package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.model.AccountSignInfo;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfo;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfoResponse;
import ru.sberbank.pprb.sbbol.partners.model.FraudMetaData;

import java.util.List;

/**
 * Сервис по работе с подписями счётов Партнера
 */
public interface AccountSignService {

    /**
     * Создание информации о подписи счетов Партнера
     *
     * @param accountsSign новые данные о подписях счетов Партнера
     * @param fraudMetaData метаданные для Фрод-мониторинга
     * @return Информация о подписях счетов партнера
     */
    AccountsSignInfoResponse createAccountsSign(AccountsSignInfo accountsSign, FraudMetaData fraudMetaData);

    /**
     * Удаление информации о подписи счёта Партнера
     *
     * @param digitalId Идентификатор личного кабинета
     * @param accountIds Идентификаторы счетов
     */
    void deleteAccountsSign(String digitalId, List<String> accountIds);

    /**
     * Получение информации о подписи счёта Партнера
     *
     * @param digitalId Идентификатор личного кабинета
     * @param accountId Идентификатор счёта
     * @return информация о подписи счёта Партнера
     */
    AccountSignInfo getAccountSign(String digitalId, String accountId);

}
