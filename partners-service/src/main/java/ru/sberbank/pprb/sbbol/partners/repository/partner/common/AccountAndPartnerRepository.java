package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.model.AccountAndPartnerRequest;

import java.util.List;

public interface AccountAndPartnerRepository {

    List<AccountEntity> findByRequest(AccountAndPartnerRequest request);
}
