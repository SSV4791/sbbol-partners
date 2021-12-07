package ru.sberbank.pprb.sbbol.partners.service.counterparty;

import ru.sberbank.pprb.sbbol.counterparties.model.CheckPayeeRequisitesResult;
import ru.sberbank.pprb.sbbol.counterparties.model.CounterpartySearchRequest;

/**
 * Сервис работы с контрагентами получений признака подписанности контрагента
 */
@Deprecated(since = "2.0", forRemoval = true)
public interface CounterpartyService {

    CheckPayeeRequisitesResult checkRequisites(CounterpartySearchRequest searchRequest);
}
