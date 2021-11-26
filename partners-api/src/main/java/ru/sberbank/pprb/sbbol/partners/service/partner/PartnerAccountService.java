package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.model.PartnerAccount;
import ru.sberbank.pprb.sbbol.partners.model.PartnerAccountResponse;
import ru.sberbank.pprb.sbbol.partners.model.PartnerAccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.PartnerAccountsResponse;

/**
 * Сервис по работе с счётами Партнера
 */
public interface PartnerAccountService {

    /**
     * Получение счёта Партнера
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param id        Идентификатор счёта
     * @return Счёт
     */
    PartnerAccountResponse getAccount(String digitalId, String id);

    /**
     * Получение списка счётов партнеров по заданному фильтру
     *
     * @param accountsFilter фильтр для поиска счётов Партнера
     * @return список счётов партнера, удовлетворяющих заданному фильтру
     */
    PartnerAccountsResponse getAccounts(PartnerAccountsFilter accountsFilter);

    /**
     * Создание нового счёта Партнера
     *
     * @param account данные счёта Партнера
     * @return Счёт
     */
    PartnerAccountResponse saveAccount(PartnerAccount account);

    /**
     * Обновление счёта Партнера
     *
     * @param account новые данные счёта Партнера
     * @return Счёт
     */
    PartnerAccountResponse updateAccount(PartnerAccount account);

    /**
     * Удаление счёта Партнера
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param id        Идентификатор счёта Партнера
     * @return Счёт
     */
    PartnerAccountResponse deleteAccount(String digitalId, String id);
}
