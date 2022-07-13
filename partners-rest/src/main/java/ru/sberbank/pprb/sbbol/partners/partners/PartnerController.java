package ru.sberbank.pprb.sbbol.partners.partners;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.PartnersApi;
import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validation;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;
import ru.sberbank.pprb.sbbol.partners.model.PartnersFilter;
import ru.sberbank.pprb.sbbol.partners.model.PartnersResponse;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerService;
import ru.sberbank.pprb.sbbol.partners.validation.PartnerCreateValidatorImpl;
import ru.sberbank.pprb.sbbol.partners.validation.PartnerUpdateValidatorImpl;
import ru.sberbank.pprb.sbbol.partners.validation.PartnersFilterValidationImpl;

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
    public ResponseEntity<PartnersResponse> list(@Validation(type = PartnersFilterValidationImpl.class) PartnersFilter partnersFilter) {
        return ResponseEntity.ok(partnerService.getPartners(partnersFilter));
    }

    @Override
    public ResponseEntity<Partner> create(@Validation(type = PartnerCreateValidatorImpl.class) PartnerCreate partner) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partnerService.savePartner(partner));
    }

    @Override
    public ResponseEntity<Void> delete(String digitalId, List<String> ids) {
        partnerService.deletePartners(digitalId, ids);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Partner> update(@Validation(type = PartnerUpdateValidatorImpl.class) Partner partner) {
        return ResponseEntity.ok(partnerService.updatePartner(partner));
    }
}
