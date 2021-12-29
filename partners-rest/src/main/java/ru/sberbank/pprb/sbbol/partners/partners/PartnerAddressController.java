package ru.sberbank.pprb.sbbol.partners.partners;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.partners.PartnerAddressApi;
import ru.sberbank.pprb.sbbol.partners.model.Address;
import ru.sberbank.pprb.sbbol.partners.model.AddressResponse;
import ru.sberbank.pprb.sbbol.partners.model.AddressesFilter;
import ru.sberbank.pprb.sbbol.partners.model.AddressesResponse;
import ru.sberbank.pprb.sbbol.partners.model.Error;
import ru.sberbank.pprb.sbbol.partners.service.partner.PartnerAddressService;

@RestController
public class PartnerAddressController implements PartnerAddressApi {

    private final PartnerAddressService addressService;

    public PartnerAddressController(PartnerAddressService addressService) {
        this.addressService = addressService;
    }

    @Override
    public ResponseEntity<AddressResponse> change(Address address) {
        return ResponseEntity.ok(addressService.saveAddress(address));
    }

    @Override
    public ResponseEntity<Error> delete(String digitalId, String id) {
        return ResponseEntity.ok(addressService.deleteAddress(digitalId, id));
    }

    @Override
    public ResponseEntity<AddressResponse> getById(String digitalId, String id) {
        return ResponseEntity.ok(addressService.getAddress(digitalId, id));
    }

    @Override
    public ResponseEntity<AddressesResponse> list(AddressesFilter addressesFilter) {
        return ResponseEntity.ok(addressService.getAddresses(addressesFilter));
    }

    @Override
    public ResponseEntity<AddressResponse> update(Address address) {
        return ResponseEntity.ok(addressService.updateAddress(address));
    }
}
