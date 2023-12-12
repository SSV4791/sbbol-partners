package ru.sberbank.pprb.sbbol.partners.partners;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.PartnersApi;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.aspect.validator.FraudValid;
import ru.sberbank.pprb.sbbol.partners.model.ExternalInternalIdLinksResponse;
import ru.sberbank.pprb.sbbol.partners.model.FraudMetaData;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.PartnerFullModelResponse;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;
import ru.sberbank.pprb.sbbol.partners.model.PartnersResponse;
import ru.sberbank.pprb.sbbol.partners.service.ids.history.IdsHistoryService;
import ru.sberbank.pprb.sbbol.partners.service.partner.FraudMonitoringService;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerService;

import java.util.List;
import java.util.UUID;

@Loggable
@RestController
public class PartnerController implements PartnersApi {

    private final FraudMonitoringService fraudMonitoringService;

    private final PartnerService partnerService;
    private final IdsHistoryService idsHistoryService;

    public PartnerController(
        FraudMonitoringService fraudMonitoringService,
        PartnerService partnerService,
        IdsHistoryService idsHistoryService
    ) {
        this.fraudMonitoringService = fraudMonitoringService;
        this.partnerService = partnerService;
        this.idsHistoryService = idsHistoryService;
    }

    @Override
    public ResponseEntity<Partner> getById(String digitalId, UUID id) {
        return ResponseEntity.ok(partnerService.getPartner(digitalId, id));
    }

    @Override
    public ResponseEntity<ExternalInternalIdLinksResponse> getInternalIdByExternalIds(String digitalId, List<UUID> externalIds) {
        return ResponseEntity.ok(idsHistoryService.getPartnersInternalId(digitalId, externalIds));
    }

    @Override
    public ResponseEntity<PartnersResponse> list(PartnersFilter partnersFilter) {
        return ResponseEntity.ok(partnerService.getPartners(partnersFilter));
    }

    @Override
    public ResponseEntity<Partner> create(PartnerCreate partner) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partnerService.savePartner(partner));
    }

    @FraudValid
    @Override
    public ResponseEntity<Void> delete(String digitalId, List<UUID> ids, FraudMetaData fraudMetaData) {
        fraudMonitoringService.deletePartners(digitalId, ids, fraudMetaData);
        partnerService.deletePartners(digitalId, ids, fraudMetaData);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<PartnerFullModelResponse> createFullModel(PartnerCreateFullModel partnerCreateFullModel) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partnerService.savePartner(partnerCreateFullModel));
    }

    @Override
    public ResponseEntity<Partner> update(Partner partner) {
        return ResponseEntity.ok(partnerService.patchPartner(partner));
    }

    @Override
    public ResponseEntity<PartnerFullModelResponse> patchFullModel(PartnerChangeFullModel partnerChangeFullModel) {
        return ResponseEntity.status(HttpStatus.OK).body(partnerService.patchPartner(partnerChangeFullModel));
    }
}
