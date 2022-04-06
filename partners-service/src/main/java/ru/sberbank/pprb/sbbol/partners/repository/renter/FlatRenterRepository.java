package ru.sberbank.pprb.sbbol.partners.repository.renter;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sberbank.pprb.sbbol.partners.entity.renter.FlatRenter;

import java.util.Optional;
import java.util.UUID;

/**
 * @deprecated {@link ru.sberbank.pprb.sbbol.partners.repository.partner.PartnerRepository}
 */
@Deprecated(forRemoval = true)
@Repository
public interface FlatRenterRepository extends CrudRepository<FlatRenter, UUID> {

    /**
     * Получение Арендатора
     *
     * @param partnerUuid Идентификатор Арендатора
     * @return Плоский рентер для утаревшего API
     */
    Optional<FlatRenter> getByPartnerUuid(UUID partnerUuid);
}
