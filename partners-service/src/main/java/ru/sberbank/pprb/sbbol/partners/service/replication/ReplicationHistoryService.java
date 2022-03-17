package ru.sberbank.pprb.sbbol.partners.service.replication;

import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.model.AccountChange;
import ru.sberbank.pprb.sbbol.partners.model.AccountCreate;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.sbbol.Counterparty;

import java.util.UUID;

public interface ReplicationHistoryService {
    /**
     * Обновлении контрагента в СББОЛ по истории репликации
     *
     * @param partner Партнёр
     */
    void updateCounterparty(Partner partner);

    /**
     * Обновлении партнёра в фабрике по истории репликации
     *
     * @param partner Контрагент
     */
    void updatePartner(Partner partner);

    /**
     * Удалить контрагента по истории репликации
     *
     * @param digitalId Идентификатор личного кабинета
     * @param id        Идентификатор партнёра
     */
    void deleteCounterparty(String digitalId, String id);

    /**
     * Удалить партнёра по истории репликации
     *
     * @param digitalId Идентификатор личного кабинета
     * @param id        Идентификатор контрагента в СББОЛ
     */
    void deletePartner(String digitalId, String id);

    /**
     * Сохранение контрагента в СББОЛ по истории репликации
     *
     * @param partner      Партнёр
     * @param account      Счёт на обновление
     * @param savedAccount Сохранённый счёт
     */
    void saveCounterparty(PartnerEntity partner, AccountCreate account, AccountEntity savedAccount);

    /**
     * Сохранить счёт по истории репликации
     *
     * @param account                  Счёт для сохранения
     * @param sbbolUpdatedCounterparty Обновленный в СББОЛ контрагент
     * @return Идентификатор счёта
     */
    UUID saveAccount(AccountCreate account, Counterparty sbbolUpdatedCounterparty);

    /**
     * Обновить контрагент по истории репликации
     *
     * @param account Счёт для обновления
     */
    void updateCounterparty(AccountChange account);

    /**
     * Обновить счёт по истории репликации
     *
     * @param account                  Счёт для обновления
     * @param sbbolUpdatedCounterparty Контрагент
     * @return Идентификатор счёта
     */
    UUID updateAccount(AccountChange account, Counterparty sbbolUpdatedCounterparty);

    /**
     * Удалить счёт по истории репликации
     *
     * @param digitalId Идентификатор личного кабинета
     * @param id        Идентификатор счёт
     */
    void deleteAccount(String digitalId, String id);
}
