package ru.sberbank.pprb.sbbol.partners.partners;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.PartnerPhoneApi;
import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validation;
import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.model.PhoneCreate;
import ru.sberbank.pprb.sbbol.partners.model.PhonesFilter;
import ru.sberbank.pprb.sbbol.partners.model.PhonesResponse;
import ru.sberbank.pprb.sbbol.partners.service.partner.PhoneService;
import ru.sberbank.pprb.sbbol.partners.validation.PhoneCreateValidationImpl;
import ru.sberbank.pprb.sbbol.partners.validation.PhoneUpdateValidationImpl;
import ru.sberbank.pprb.sbbol.partners.validation.PhonesFilterValidationImpl;

@RestController
public class PartnerPhoneController implements PartnerPhoneApi {

    private final PhoneService partnerPhoneService;

    public PartnerPhoneController(PhoneService partnerPhoneService) {
        this.partnerPhoneService = partnerPhoneService;
    }

    @Override
    public ResponseEntity<Phone> create(@Validation(type = PhoneCreateValidationImpl.class) PhoneCreate phone) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partnerPhoneService.savePhone(phone));
    }

    @Override
    public ResponseEntity<Void> delete(String digitalId, String id) {
        partnerPhoneService.deletePhone(digitalId, id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<PhonesResponse> list(@Validation(type = PhonesFilterValidationImpl.class) PhonesFilter phonesFilter) {
        return ResponseEntity.ok(partnerPhoneService.getPhones(phonesFilter));
    }

    @Override
    public ResponseEntity<Phone> update(@Validation(type = PhoneUpdateValidationImpl.class) Phone phone) {
        return ResponseEntity.ok(partnerPhoneService.updatePhone(phone));
    }
}
