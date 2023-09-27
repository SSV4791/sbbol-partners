package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.model.AccountSignInfoRequisites;

import java.util.List;

public interface AccountSearchRepository {

    /**
     * Получение счетов
     *
     * @param accountSignRequisites Реквизиты счета для поиска
     * @return Счёта
     */
    List<AccountEntity> findByRequisites(AccountSignInfoRequisites accountSignRequisites);
}
