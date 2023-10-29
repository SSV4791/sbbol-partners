package ru.sberbank.pprb.sbbol.partners.repository.partner;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.PartnerType;
import ru.sberbank.pprb.sbbol.partners.repository.partner.common.PartnerViewRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.common.RenterViewRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PartnerRepository extends CrudRepository<PartnerEntity, UUID>, PartnerViewRepository, RenterViewRepository {

    /**
     * Проверка наличия Партнера
     *
     * @param digitalId Идентификатор личного кабинета
     * @param uuid      Идентификатор документа
     */
    boolean existsByDigitalIdAndUuid(String digitalId, UUID uuid);

    /**
     * Получение Партнера
     *
     * @param digitalId Идентификатор личного кабинета
     * @param uuid      Идентификатор документа
     * @return Партнер
     */
    Optional<PartnerEntity> getByDigitalIdAndUuid(String digitalId, UUID uuid);

    /**
     * Получение Партнеров
     *
     * @param uuid Идентификатор документа
     * @return Партнер
     */
    List<PartnerEntity> findAllByUuid(UUID uuid);

    /**
     * Поиск партнера по ключевым параметрам
     *
     * @param digitalId Идентификатор личного кабинета
     * @param search    Данные для поиска партнера
     */
    PartnerEntity findByDigitalIdAndSearchAndType(String digitalId, String search, PartnerType type);
}
