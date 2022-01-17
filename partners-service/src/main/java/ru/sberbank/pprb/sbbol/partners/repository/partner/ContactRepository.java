package ru.sberbank.pprb.sbbol.partners.repository.partner;

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
     * @param uuid      Идентификатор контакта
     * @return контакт Партнера
     */
    ContactEntity getByDigitalIdAndUuid(String digitalId, UUID uuid);
}
