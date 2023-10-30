package ru.sberbank.pprb.sbbol.partners.repository.partner;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PhoneEntity;
import ru.sberbank.pprb.sbbol.partners.repository.partner.common.PhoneViewRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PhoneRepository extends CrudRepository<PhoneEntity, UUID>, PhoneViewRepository {

    /**
     * Получение телефона
     *
     * @param digitalId Идентификатор личного кабинета
     * @param uuid      Идентификатор документа
     * @return Телефон
     */
    Optional<PhoneEntity> getByDigitalIdAndUuid(String digitalId, UUID uuid);
}
