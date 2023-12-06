package ru.sberbank.pprb.sbbol.partners.renter;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.service.migration.RenterMigrationService;
import ru.sberbank.pprb.sbbol.partners.service.renter.RenterService;
import ru.sberbank.pprb.sbbol.renter.SbbolPartnersApi;
import ru.sberbank.pprb.sbbol.renter.model.Renter;
import ru.sberbank.pprb.sbbol.renter.model.RenterFilter;
import ru.sberbank.pprb.sbbol.renter.model.RenterIdentifier;
import ru.sberbank.pprb.sbbol.renter.model.RenterListResponse;
import ru.sberbank.pprb.sbbol.renter.model.Version;

import javax.validation.Valid;

@RestController
public class RenterController implements SbbolPartnersApi {

    private final RenterService renterService;
    private final RenterMigrationService renterMigrationService;

    public RenterController(RenterService renterService, RenterMigrationService renterMigrationService) {
        this.renterService = renterService;
        this.renterMigrationService = renterMigrationService;
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
    public ResponseEntity<Void> startMigration() {
        renterMigrationService.startMigration();
        return ResponseEntity.ok().build();
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
