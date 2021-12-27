package ru.sberbank.pprb.sbbol.partners.repository.partner.common;

import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.model.AccountsFilter;

import java.util.List;

public interface AccountViewRepository {

    List<AccountEntity> findByFilter(AccountsFilter filter);
}
