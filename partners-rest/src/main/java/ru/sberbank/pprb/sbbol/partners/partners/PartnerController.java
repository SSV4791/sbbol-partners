package ru.sberbank.pprb.sbbol.partners.partners;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.PartnersApi;
import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validation;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;
import ru.sberbank.pprb.sbbol.partners.model.PartnerResponse;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;
import ru.sberbank.pprb.sbbol.partners.model.PartnersResponse;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerService;
import ru.sberbank.pprb.sbbol.partners.validation.PartnerCreateValidatorImpl;
import ru.sberbank.pprb.sbbol.partners.validation.PartnerUpdateValidatorImpl;
import ru.sberbank.pprb.sbbol.partners.validation.PartnersFilterValidationImpl;

@RestController
public class PartnerController implements PartnersApi {

    private final PartnerService partnerService;

    public PartnerController(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    @Override
    public ResponseEntity<Void> delete(String digitalId, String id) {
        partnerService.deletePartner(digitalId, id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<PartnerResponse> getById(String digitalId, String id) {
        return ResponseEntity.ok(partnerService.getPartner(digitalId, id));
    }

    @Override
    public ResponseEntity<PartnersResponse> list(@Validation(type = PartnersFilterValidationImpl.class) PartnersFilter partnersFilter) {
        return ResponseEntity.ok(partnerService.getPartners(partnersFilter));
    }

    @Override
    public ResponseEntity<PartnerResponse> create(@Validation(type = PartnerCreateValidatorImpl.class) PartnerCreate partner) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partnerService.savePartner(partner));
    }

    @Override
    public ResponseEntity<PartnerResponse> update(@Validation(type = PartnerUpdateValidatorImpl.class) Partner partner) {
        return ResponseEntity.ok(partnerService.updatePartner(partner));
    }
}
