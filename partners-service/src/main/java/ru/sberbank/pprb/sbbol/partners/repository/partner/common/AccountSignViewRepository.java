package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.model.AccountsSignFilter;

import java.util.List;

public interface AccountSignViewRepository {

    /**
     * Получение информации о подписях счетов Партнера
     *
     * @param filter Фильтр для запроса информации о подписях счетов
     * @return Список счетов удовлетворяющих фильтру
     */
    List<AccountEntity> findByFilter(AccountsSignFilter filter);
}
