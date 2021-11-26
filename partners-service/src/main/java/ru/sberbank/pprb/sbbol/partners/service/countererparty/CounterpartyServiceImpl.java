package ru.sberbank.pprb.sbbol.partners.service.countererparty;

import org.springframework.stereotype.Service;
import ru.sberbank.pprb.sbbol.counterparties.model.CheckPayeeRequisitesResult;
import ru.sberbank.pprb.sbbol.counterparties.model.CounterpartySearchRequest;
import ru.sberbank.pprb.sbbol.partners.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.mapper.counterparty.CounterpartyMapper;
import ru.sberbank.pprb.sbbol.partners.model.CounterpartyCheckRequisitesResult;
import ru.sberbank.pprb.sbbol.partners.service.counterparty.CounterpartyService;

import static ru.sberbank.pprb.sbbol.counterparties.model.CheckPayeeRequisitesResult.StatusEnum.NOTFOUND;
import static ru.sberbank.pprb.sbbol.counterparties.model.CheckPayeeRequisitesResult.StatusEnum.NOTSIGNED;
import static ru.sberbank.pprb.sbbol.counterparties.model.CheckPayeeRequisitesResult.StatusEnum.SIGNED;

@Service
@Deprecated(since = "2.0", forRemoval = true)
public class CounterpartyServiceImpl implements CounterpartyService {

    private final LegacySbbolAdapter legacySbbolAdapter;
    private final CounterpartyMapper counterpartyMapper;

    public CounterpartyServiceImpl(CounterpartyMapper counterpartyMapper, LegacySbbolAdapter legacySbbolAdapter) {
        this.counterpartyMapper = counterpartyMapper;
        this.legacySbbolAdapter = legacySbbolAdapter;
    }

    @Override
    public CheckPayeeRequisitesResult checkRequisites(CounterpartySearchRequest searchRequest) {
        var requisites = counterpartyMapper.toCounterpartyCheckRequisites(searchRequest);
        CounterpartyCheckRequisitesResult checkResult = legacySbbolAdapter.checkRequisites(requisites);

        CheckPayeeRequisitesResult result = new CheckPayeeRequisitesResult();
        if (checkResult == null || checkResult.getPprbGuid() == null) {
            result.setStatus(NOTFOUND);
            result.setPprbGuid(null);
        } else if (Boolean.TRUE.equals(checkResult.getSigned())) {
            result.setStatus(SIGNED);
            result.setPprbGuid(checkResult.getPprbGuid());
        } else {
            result.setStatus(NOTSIGNED);
            result.setPprbGuid(checkResult.getPprbGuid());
        }
        return result;
    }
}
