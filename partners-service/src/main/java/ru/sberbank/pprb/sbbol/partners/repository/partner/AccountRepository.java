package ru.sberbank.pprb.sbbol.partners.repository.partner;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.repository.partner.common.AccountViewRepository;

import java.util.UUID;

@Repository
public interface AccountRepository extends PagingAndSortingRepository<AccountEntity, UUID>, AccountViewRepository {

    /**
     * Получение счёта Партнера
     *
     * @param digitalId Идентификатор личного кабинета
     * @param uuid      Идентификатор документа
     * @return счёт Партнер
     */
    AccountEntity getByDigitalIdAndUuid(String digitalId, UUID uuid);
}