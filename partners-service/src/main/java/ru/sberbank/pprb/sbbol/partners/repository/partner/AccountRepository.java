package ru.sberbank.pprb.sbbol.partners.repository.partner;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
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
     * @param id        Идентификатор документа
     * @return счёт Партнер
     */
    AccountEntity getByDigitalIdAndId(String digitalId, UUID id);

    /**
     * Получение списка счетов Партнеров c сортировкой
     *
     * @param digitalId Идентификатор личного кабинета
     * @param sort      Параметры сортировки
     * @return Список счётов партнеров
     */
    Slice<AccountEntity> findAllByDigitalId(String digitalId, Sort sort);

    /**
     * Получение списка счётов Партнера c пагинацией
     *
     * @param digitalId Идентификатор личного кабинета
     * @param pageable  Параметры пагинации
     * @return Список счётов партнера
     */
    Slice<AccountEntity> findAllByDigitalId(String digitalId, Pageable pageable);
}
