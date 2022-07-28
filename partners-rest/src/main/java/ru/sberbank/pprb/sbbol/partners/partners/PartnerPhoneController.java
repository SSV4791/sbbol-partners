package ru.sberbank.pprb.sbbol.partners.partners;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.PartnerPhoneApi;
import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.model.PhoneCreate;
import ru.sberbank.pprb.sbbol.partners.model.PhonesFilter;
import ru.sberbank.pprb.sbbol.partners.model.PhonesResponse;
import ru.sberbank.pprb.sbbol.partners.service.partner.PhoneService;

import java.util.List;

@RestController
public class PartnerPhoneController implements PartnerPhoneApi {

    private final PhoneService partnerPhoneService;

    public PartnerPhoneController(PhoneService partnerPhoneService) {
        this.partnerPhoneService = partnerPhoneService;
    }

    @Override
    public ResponseEntity<Phone> create(PhoneCreate phone) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partnerPhoneService.savePhone(phone));
    }

    @Override
    public ResponseEntity<Void> delete(String digitalId, List<String> ids) {
        partnerPhoneService.deletePhones(digitalId, ids);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<PhonesResponse> list(PhonesFilter phonesFilter) {
        return ResponseEntity.ok(partnerPhoneService.getPhones(phonesFilter));
    }

    @Override
    public ResponseEntity<Phone> update(Phone phone) {
        return ResponseEntity.ok(partnerPhoneService.updatePhone(phone));
    }
}
