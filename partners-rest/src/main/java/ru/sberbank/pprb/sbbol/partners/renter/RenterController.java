package ru.sberbank.pprb.sbbol.partners.renter;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.renter.model.Renter;
import ru.sberbank.pprb.sbbol.renter.model.RenterFilter;
import ru.sberbank.pprb.sbbol.renter.model.RenterIdentifier;
import ru.sberbank.pprb.sbbol.renter.model.RenterListResponse;
import ru.sberbank.pprb.sbbol.renter.model.Version;
import ru.sberbank.pprb.sbbol.partners.service.renter.RenterService;
import ru.sberbank.pprb.sbbol.renter.RenterApi;

import javax.validation.Valid;

@RestController
public class RenterController implements RenterApi {

    private final RenterService renterService;

    public RenterController(RenterService renterService) {
        this.renterService = renterService;
    }

    @Override
    public ResponseEntity<Renter> createRenter(@Valid Renter renter) {
        return ResponseEntity.ok(renterService.createRenter(renter));
    }

    @Override
    public ResponseEntity<Renter> getRenter(@Valid RenterIdentifier renterIdentifier) {
        return ResponseEntity.ok(renterService.getRenter(renterIdentifier));
    }

    @Override
    public ResponseEntity<RenterListResponse> getRenters(@Valid RenterFilter renterFilter) {
        return ResponseEntity.ok(renterService.getRenters(renterFilter));
    }

    @Override
    public ResponseEntity<Renter> updateRenter(@Valid Renter renter) {
        return ResponseEntity.ok(renterService.updateRenter(renter));
    }

    @Override
    public ResponseEntity<Version> version() {
        return ResponseEntity.ok(new Version().ver("1.0.0"));
    }
}
