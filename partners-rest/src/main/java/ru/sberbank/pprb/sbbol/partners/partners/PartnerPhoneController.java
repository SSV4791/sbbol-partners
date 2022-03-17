package ru.sberbank.pprb.sbbol.partners.partners;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.PartnerPhoneApi;
import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validation;
import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.model.PhoneCreate;
import ru.sberbank.pprb.sbbol.partners.model.PhoneResponse;
import ru.sberbank.pprb.sbbol.partners.model.PhonesFilter;
import ru.sberbank.pprb.sbbol.partners.model.PhonesResponse;
import ru.sberbank.pprb.sbbol.partners.service.partner.PhoneService;
import ru.sberbank.pprb.sbbol.partners.validation.PhoneCreateValidation;
import ru.sberbank.pprb.sbbol.partners.validation.PhoneValidation;

@RestController
public class PartnerPhoneController implements PartnerPhoneApi {

    private final PhoneService partnerPhoneService;

    public PartnerPhoneController(PhoneService partnerPhoneService) {
        this.partnerPhoneService = partnerPhoneService;
    }

    @Override
    public ResponseEntity<PhoneResponse> create(@Validation(type = PhoneCreateValidation.class) PhoneCreate phone) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partnerPhoneService.savePhone(phone));
    }

    @Override
    public ResponseEntity<Void> delete(String digitalId, String id) {
        partnerPhoneService.deletePhone(digitalId, id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<PhonesResponse> list(PhonesFilter phonesFilter) {
        return ResponseEntity.ok(partnerPhoneService.getPhones(phonesFilter));
    }

    @Override
    public ResponseEntity<PhoneResponse> update(@Validation(type = PhoneValidation.class) Phone phone) {
        return ResponseEntity.ok(partnerPhoneService.updatePhone(phone));
    }
}
