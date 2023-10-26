package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.model.AccountsSignInfo;
import ru.sberbank.pprb.sbbol.partners.model.FraudMetaData;

import java.util.List;
import java.util.UUID;

/**
 * Сервис по работе с Фрод Мониторингом
 */
public interface FraudMonitoringService {

    /**
     * Отправка информации о подписи счетов Партнера во Фрод-Мониторинг
     *
     * @param accountsSign  новые данные о подписях счетов Партнера
     * @param fraudMetaData метаданные для Фрод-мониторинга
     * @return Информация о подписях счетов партнера
     */
    void createAccountsSign(AccountsSignInfo accountsSign, FraudMetaData fraudMetaData);

    /**
     * Отправка информации по Удалению Партнера во Фрод-Мониторинг
     *
     * @param digitalId Идентификатор личного кабинета клиента
     * @param ids       список идентификаторов Партнеров
     */
    void deletePartners(String digitalId, List<UUID> ids, FraudMetaData fraudMetaData);
}
