package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.model.Pagination;

import java.util.List;

public interface RenterAccountUpdaterService {

    List<PartnerEntity> getRenters(Pagination pagination);

    void updateAccountType(PartnerEntity renter);
}
