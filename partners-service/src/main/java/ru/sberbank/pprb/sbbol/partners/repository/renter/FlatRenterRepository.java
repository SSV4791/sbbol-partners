package ru.sberbank.pprb.sbbol.partners.repository.renter;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.PartnerType;
import ru.sberbank.pprb.sbbol.partners.entity.renter.FlatRenter;

import java.util.UUID;

@Deprecated
@Repository
public interface FlatRenterRepository extends CrudRepository<FlatRenter, UUID> {

    /**
     * Получение Арендатора
     *
     * @param partnerUuid Идентификатор Арендатора
     * @return Плоский рентер для утаревшего API
     */
    FlatRenter getByPartnerUuid(UUID partnerUuid);
}
