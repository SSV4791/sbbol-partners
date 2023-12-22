package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.model.Account;
import ru.sberbank.pprb.sbbol.partners.model.AccountAndPartnerRequest;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.AccountPriority;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignInfoRequisites;
import ru.sberbank.pprb.sbbol.partners.model.AccountWithPartnerResponse;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;
import ru.sberbank.pprb.sbbol.partners.model.AccountsResponse;
import ru.sberbank.pprb.sbbol.partners.model.SignType;

import java.util.List;
import java.util.Set;
import java.util.UUID;

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
    Account getAccount(String digitalId, UUID id);

    /**
     * Получение счёта Партнера
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param partnerId Идентификатор партнера
     * @return Список счётов
     */
    List<Account> getAccountsByPartnerId(String digitalId, UUID partnerId);

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
     * Создание новых счётов Партнера
     *
     * @param accounts    счета
     * @param digitalId   Идентификатор личного кабинета клиента
     * @param partnerUuid Идентификатор партнера
     * @return Счёт
     */
    List<Account> saveAccounts(Set<AccountCreateFullModel> accounts, String digitalId, UUID partnerUuid);

    /**
     * Обновление счёта Партнера
     *
     * @param account новые данные счёта Партнера
     * @return Счёт
     */
    Account updateAccount(AccountChange account);

    /**
     * Частичное обновление счёта Партнера
     *
     * @param account новые данные счёта Партнера
     * @return Счёт
     */
    Account patchAccount(AccountChange account);

    /**
     * Создание/частичное обновление счета партнера
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param partnerId Идентификатор партнера
     * @param account   Счет для создания/частичного обновления
     */
    void saveOrPatchAccount(String digitalId, UUID partnerId, AccountChangeFullModel account);

    /**
     * Создание/частичное обновление счетов партнера
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param partnerId Идентификатор партнера
     * @param accounts  Список счетов для создания/частичного обновления
     */
    void saveOrPatchAccounts(String digitalId, UUID partnerId, Set<AccountChangeFullModel> accounts);

    /**
     * Удаление счёта Партнера
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param ids       Идентификаторы счетов Партнера
     */
    void deleteAccounts(String digitalId, List<UUID> ids);

    /**
     * Изменение приоритетного счета
     *
     * @param accountPriority данные по приоритетному счёту
     */
    Account changePriority(AccountPriority accountPriority);

    /**
     * Изменение статуса подписания счета
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param accountId Идентификатор счета Партнера
     * @param signType Статус подписания
     */
    Account changeSignType(String digitalId, UUID accountId, SignType signType);

    /**
     * Получение счетов партнера с партнером по запросу
     *
     * @param request параметры поиска счета с партнером
     */
    List<AccountWithPartnerResponse> getAtRequisites(AccountAndPartnerRequest request);

    /**
     * Получение счета партнера с партнером анализируя все реквизиты запроса
     *
     * @param request параметры поиска счета с партнером
     */
    AccountWithPartnerResponse getAtAllRequisites(AccountAndPartnerRequest request);

    /**
     * Получение счетов партнера по реквизитам
     *
     * @param requisites параметры поиска счетов
     */
    List<Account> getByRequisites(AccountSignInfoRequisites requisites);
}
