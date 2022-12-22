package ru.sberbank.pprb.sbbol.partners.partners;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.PartnersApi;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModelResponse;
import ru.sberbank.pprb.sbbol.partners.model.PartnerDelete;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;
import ru.sberbank.pprb.sbbol.partners.model.PartnersResponse;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerService;

import java.util.List;

@RestController
public class PartnerController implements PartnersApi {

    private final PartnerService partnerService;

    public PartnerController(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    @Override
    public ResponseEntity<Partner> getById(String digitalId, String id) {
        return ResponseEntity.ok(partnerService.getPartner(digitalId, id));
    }

    @Override
    public ResponseEntity<PartnersResponse> list(PartnersFilter partnersFilter) {
        return ResponseEntity.ok(partnerService.getPartners(partnersFilter));
    }

    @Override
    public ResponseEntity<Partner> create(PartnerCreate partner) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partnerService.savePartner(partner));
    }

    @Override
    public ResponseEntity<Void> delete(String digitalId, List<String> ids) {
        partnerService.deletePartners(digitalId, ids);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deletePartners(PartnerDelete partnerDelete) {
        partnerService.deletePartners(partnerDelete);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<PartnerCreateFullModelResponse> createFullModel(PartnerCreateFullModel partnerCreateFullModel) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partnerService.savePartner(partnerCreateFullModel));
    }

    @Override
    public ResponseEntity<Partner> update(Partner partner) {
        return ResponseEntity.ok(partnerService.updatePartner(partner));
    }
}
