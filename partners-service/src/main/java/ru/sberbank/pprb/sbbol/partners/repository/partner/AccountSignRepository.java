package ru.sberbank.pprb.sbbol.partners.repository.partner;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sberbank.pprb.sbbol.partners.entity.partner.SignEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountSignRepository extends CrudRepository<SignEntity, UUID> {

    /**
     * Получение информации о подписи счёта Партнера
     *
     * @param digitalId Идентификатор личного кабинета
     * @param accountId Идентификатор счёта партнера
     * @return Информация о подписи
     */
    Optional<SignEntity> getByDigitalIdAndAccountUuid(String digitalId, UUID accountId);
}
