package ru.sberbank.pprb.sbbol.partners.repository.partner;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;

import java.util.UUID;

@Repository
public interface PartnerRepository extends PagingAndSortingRepository<PartnerEntity, UUID> {

    /**
     * Получение Партнера по идентификатору
     *
     * @param id Идентификатор документа
     * @return Партнер
     */
    PartnerEntity getById(UUID id);

    /**
     * Получение Партнера
     *
     * @param digitalId Идентификатор личного кабинета
     * @param id        Идентификатор документа
     * @return Партнер
     */
    PartnerEntity getByDigitalIdAndId(String digitalId, UUID id);

    /**
     * Получение списка Партнеров c сортировкой
     *
     * @param digitalId Идентификатор личного кабинета
     * @param sort      Параметры сортировки
     * @return Список партнеров
     */
    Slice<PartnerEntity> findAllByDigitalId(String digitalId, Sort sort);

    /**
     * Получение списка Партнеров c пагинацией
     *
     * @param digitalId Идентификатор личного кабинета
     * @param pageable  Параметры пагинации
     * @return Список партнеров
     */
    Slice<PartnerEntity> findAllByDigitalId(String digitalId, Pageable pageable);
}
