package ru.sberbank.pprb.sbbol.partners.partners;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.ContactPhoneApi;
import ru.sberbank.pprb.sbbol.partners.aspect.validation.Validation;
import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.model.PhoneResponse;
import ru.sberbank.pprb.sbbol.partners.model.PhonesFilter;
import ru.sberbank.pprb.sbbol.partners.model.PhonesResponse;
import ru.sberbank.pprb.sbbol.partners.service.partner.PhoneService;
import ru.sberbank.pprb.sbbol.partners.validation.PhoneValidation;

@RestController
public class ContactPhoneController implements ContactPhoneApi {

    private final PhoneService contactPhoneService;

    public ContactPhoneController(PhoneService contactPhoneService) {
        this.contactPhoneService = contactPhoneService;
    }

    @Override
    public ResponseEntity<PhoneResponse> create(@Validation(type = PhoneValidation.class) Phone phone) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contactPhoneService.savePhone(phone));
    }

    @Override
    public ResponseEntity<Void> delete(String digitalId, String id) {
        contactPhoneService.deletePhone(digitalId, id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<PhonesResponse> list(PhonesFilter phonesFilter) {
        return ResponseEntity.ok(contactPhoneService.getPhones(phonesFilter));
    }

    @Override
    public ResponseEntity<PhoneResponse> update(@Validation(type = PhoneValidation.class) Phone phone) {
        return ResponseEntity.ok(contactPhoneService.updatePhone(phone));
    }
}
