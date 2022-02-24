package ru.sberbank.pprb.sbbol.partners.repository.partner;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AccountStateType;
import ru.sberbank.pprb.sbbol.partners.repository.partner.common.AccountBudgetViewRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.common.AccountSignViewRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.common.AccountViewRepository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountRepository extends CrudRepository<AccountEntity, UUID>, AccountViewRepository, AccountSignViewRepository, AccountBudgetViewRepository {

    /**
     * Получение счёта Партнера
     *
     * @param digitalId Идентификатор личного кабинета
     * @param uuid      Идентификатор счёта
     * @return счёт Партнер
     */
    AccountEntity getByDigitalIdAndUuid(String digitalId, UUID uuid);

    /**
     * Поиск счетов Партнеров
     *
     * @param digitalId Идентификатор личного кабинета
     * @param state     Статус счёта
     * @return Счёта партнера
     */
    List<AccountEntity> findByDigitalIdAndState(String digitalId, AccountStateType state);
}
