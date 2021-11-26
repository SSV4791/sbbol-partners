package ru.sberbank.pprb.sbbol.partners;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerResponse;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;
import ru.sberbank.pprb.sbbol.partners.model.PartnersResponse;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerService;

@RestController
public class PartnerController implements PartnersApi {

    private final PartnerService partnerService;

    public PartnerController(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    @Override
    public ResponseEntity<PartnerResponse> delete(String digitalId, String id) {
        return ResponseEntity.ok(partnerService.deletePartner(digitalId, id));
    }

    @Override
    public ResponseEntity<PartnerResponse> getById(String digitalId, String id) {
        return ResponseEntity.ok(partnerService.getPartner(digitalId, id));
    }

    @Override
    public ResponseEntity<PartnersResponse> list(PartnersFilter partnersFilter) {
        return ResponseEntity.ok(partnerService.getPartners(partnersFilter));
    }

    @Override
    public ResponseEntity<PartnerResponse> save(Partner partner) {
        return ResponseEntity.ok(partnerService.savePartner(partner));
    }

    @Override
    public ResponseEntity<PartnerResponse> update(Partner partner) {
        return ResponseEntity.ok(partnerService.updatePartner(partner));
    }
}
