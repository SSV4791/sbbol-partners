package ru.sberbank.pprb.sbbol.partners.repository.partner;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.repository.partner.common.PartnerViewRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.common.RenterViewRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PartnerRepository extends PagingAndSortingRepository<PartnerEntity, UUID>, PartnerViewRepository, RenterViewRepository {

    /**
     * Получение Партнера
     *
     * @param digitalId Идентификатор личного кабинета
     * @param uuid      Идентификатор документа
     * @return Партнер
     */
    Optional<PartnerEntity> getByDigitalIdAndUuid(String digitalId, UUID uuid);

    /**
     * Получение Партнера
     *
     * @param uuid Идентификатор документа
     * @return Партнер
     */
    Optional<PartnerEntity> getByUuid(UUID uuid);
}
