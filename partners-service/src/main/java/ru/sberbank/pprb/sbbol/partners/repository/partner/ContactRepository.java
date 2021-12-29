package ru.sberbank.pprb.sbbol.partners.repository.partner;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactEntity;
import ru.sberbank.pprb.sbbol.partners.repository.partner.common.ContactViewRepository;

import java.util.UUID;

@Repository
public interface ContactRepository extends CrudRepository<ContactEntity, UUID>, ContactViewRepository {

    /**
     * Получение контакта Партнера
     *
     * @param digitalId Идентификатор личного кабинета
     * @param id        Идентификатор документа
     * @return контакт Партнер
     */
    ContactEntity getByDigitalIdAndId(String digitalId, UUID id);

    /**
     * Получение списка контактов Партнеров c сортировкой
     *
     * @param digitalId   Идентификатор личного кабинета
     * @param partnerUuid Идентификатор личного кабинета
     * @param sort        Параметры сортировки
     * @return Список контактов партнеров
     */
    Slice<ContactEntity> findAllByDigitalIdAndPartnerUuid(String digitalId, UUID partnerUuid, Sort sort);

    /**
     * Получение списка контактов Партнера c пагинацией
     *
     * @param digitalId   Идентификатор личного кабинета
     * @param partnerUuid Идентификатор личного кабинета
     * @param pageable    Параметры пагинации
     * @return Список контактов партнера
     */
    Slice<ContactEntity> findAllByDigitalIdAndPartnerUuid(String digitalId, UUID partnerUuid, Pageable pageable);
}
