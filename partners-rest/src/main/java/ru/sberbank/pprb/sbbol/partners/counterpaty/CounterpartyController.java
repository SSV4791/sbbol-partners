package ru.sberbank.pprb.sbbol.partners.counterpaty;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import ru.sberbank.pprb.sbbol.counterparties.CounterpartiesApi;
import ru.sberbank.pprb.sbbol.counterparties.model.CheckPayeeRequisitesResult;
import ru.sberbank.pprb.sbbol.counterparties.model.CounterpartySearchRequest;
import ru.sberbank.pprb.sbbol.partners.service.counterparty.CounterpartyService;

@Deprecated(since = "2.0", forRemoval = true)
@RestController
public class CounterpartyController implements CounterpartiesApi {

    private final CounterpartyService counterpartyService;

    public CounterpartyController(CounterpartyService counterpartyService) {
        this.counterpartyService = counterpartyService;
    }

    @Override
    public ResponseEntity<CheckPayeeRequisitesResult> check(CounterpartySearchRequest counterpartySearchRequest) {
        return ResponseEntity.ok(counterpartyService.checkRequisites(counterpartySearchRequest));
    }
}
