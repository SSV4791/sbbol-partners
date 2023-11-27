package ru.sberbank.pprb.sbbol.partners.repository.partner;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AccountStateType;
import ru.sberbank.pprb.sbbol.partners.repository.partner.common.AccountAndPartnerRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.common.AccountSearchRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.common.AccountViewRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.common.FullMatchingAccountAndPartnerRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends
    CrudRepository<AccountEntity, UUID>,
    AccountViewRepository,
    AccountAndPartnerRepository,
    FullMatchingAccountAndPartnerRepository,
    AccountSearchRepository {

    /**
     * Получение счётов Партнера
     *
     * @param digitalId Идентификатор личного кабинета
     * @return счёта Партнера
     */
    List<AccountEntity> findByDigitalId(String digitalId);

    /**
     * Получение счёта Партнера
     *
     * @param digitalId Идентификатор личного кабинета
     * @param uuid      Идентификатор счёта
     * @return счёт Партнер
     */
    Optional<AccountEntity> getByDigitalIdAndUuid(String digitalId, UUID uuid);

    /**
     * Получение счетов Партнера
     *
     * @param digitalId   Идентификатор личного кабинета
     * @param partnerUuid Идентификатор партнера
     * @return счетов Партнера
     */
    List<AccountEntity> findByDigitalIdAndPartnerUuid(String digitalId, UUID partnerUuid);

    /**
     * Поиск счетов Партнеров
     *
     * @param digitalId Идентификатор личного кабинета
     * @param state     Статус счёта
     * @return Счёта партнера
     */
    List<AccountEntity> findByDigitalIdAndState(String digitalId, AccountStateType state);

    /**
     * Поиск счетов Партнеров c признаком приоритетных
     *
     * @param digitalId   Идентификатор личного кабинета
     * @param partnerUuid Идентификатор партнера
     * @return Счёта партнера
     */
    List<AccountEntity> findByDigitalIdAndPartnerUuidAndPriorityAccountIsTrue(String digitalId, UUID partnerUuid);

    /**
     * Поиск счетов Партнера по ключевым полям
     *
     * @param digitalId Идентификатор личного кабинета
     * @param search    Данный для поиска
     */
    AccountEntity findByDigitalIdAndSearch(String digitalId, String search);

    /**
     * Поиск счетов Партнера по ключевым полям
     *
     * @param digitalId Идентификатор личного кабинета
     * @param search    Данный для поиска
     * @param classDto  Тип возвращаемого объекта
     */
    <T> T findByDigitalIdAndSearch(String digitalId, String search, Class<T> classDto);
}
