package ru.sberbank.pprb.sbbol.partners.partners;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.PartnerAddressApi;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.model.Address;
import ru.sberbank.pprb.sbbol.partners.model.AddressCreate;
import ru.sberbank.pprb.sbbol.partners.model.AddressesFilter;
import ru.sberbank.pprb.sbbol.partners.model.AddressesResponse;
import ru.sberbank.pprb.sbbol.partners.service.partner.AddressService;

import java.util.List;

@Loggable
@RestController
public class PartnerAddressController implements PartnerAddressApi {

    private final AddressService partnerAddressService;

    public PartnerAddressController(AddressService partnerAddressService) {
        this.partnerAddressService = partnerAddressService;
    }

    @Override
    public ResponseEntity<Address> create(AddressCreate address) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partnerAddressService.saveAddress(address));
    }

    @Override
    public ResponseEntity<Void> delete(String digitalId, List<String> ids) {
        partnerAddressService.deleteAddresses(digitalId, ids);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Address> getById(String digitalId, String id) {
        return ResponseEntity.ok(partnerAddressService.getAddress(digitalId, id));
    }

    @Override
    public ResponseEntity<AddressesResponse> list(AddressesFilter addressesFilter) {
        return ResponseEntity.ok(partnerAddressService.getAddresses(addressesFilter));
    }

    @Override
    public ResponseEntity<Address> update(Address address) {
        return ResponseEntity.ok(partnerAddressService.updateAddress(address));
    }
}
