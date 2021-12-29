package ru.sberbank.pprb.sbbol.partners.repository.partner;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AddressEntity;
import ru.sberbank.pprb.sbbol.partners.repository.partner.common.AddressViewRepository;

import java.util.UUID;

@Repository
public interface AddressRepository extends CrudRepository<AddressEntity, UUID>, AddressViewRepository {

    /**
     * Получение адреса Партнера
     *
     * @param digitalId Идентификатор личного кабинета
     * @param id        Идентификатор документа
     * @return адреса Партнер
     */
    AddressEntity getByDigitalIdAndId(String digitalId, UUID id);

    /**
     * Получение списка адреса Партнеров c сортировкой
     *
     * @param digitalId   Идентификатор личного кабинета
     * @param unifiedUuid Идентификатор связанной записи
     * @param sort        Параметры сортировки
     * @return Список адреса партнеров
     */
    Slice<AddressEntity> findAllByDigitalIdAndUnifiedUuid(String digitalId, UUID unifiedUuid, Sort sort);

    /**
     * Получение списка адреса Партнера c пагинацией
     *
     * @param digitalId   Идентификатор личного кабинета
     * @param unifiedUuid Идентификатор связанной записи
     * @param pageable    Параметры пагинации
     * @return Список контактов партнера
     */
    Slice<AddressEntity> findAllByDigitalIdAndUnifiedUuid(String digitalId, UUID unifiedUuid, Pageable pageable);
}
