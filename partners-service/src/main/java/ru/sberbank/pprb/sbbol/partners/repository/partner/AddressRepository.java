package ru.sberbank.pprb.sbbol.partners.repository.partner;

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
     * @param uuid      Идентификатор адреса
     * @return адрес Партнера
     */
    AddressEntity getByDigitalIdAndUuid(String digitalId, UUID uuid);
}
