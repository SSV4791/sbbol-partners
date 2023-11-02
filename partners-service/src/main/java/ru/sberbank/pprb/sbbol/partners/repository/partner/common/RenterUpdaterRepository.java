package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;

import java.util.List;

public interface RenterUpdaterRepository {

    List<PartnerEntity> findRenterByFilter(Pagination filter);
}
