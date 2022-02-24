package ru.sberbank.pprb.sbbol.partners.service.partner;

import ru.sberbank.pprb.sbbol.partners.aspect.logger.Logged;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PhoneMapper;
import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.model.PhoneResponse;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ContactRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PhoneRepository;

import java.util.UUID;

@Logged(printRequestResponse = true)
public class ContactPhoneServiceImpl extends PhoneServiceImpl {

    private final ContactRepository contactRepository;

    public ContactPhoneServiceImpl(ContactRepository contactRepository, PhoneRepository phoneRepository, PhoneMapper phoneMapper) {
        super(phoneRepository, phoneMapper);
        this.contactRepository = contactRepository;
    }

    @Override
    public PhoneResponse updatePhone(Phone phone) {
        var uuid = UUID.fromString(phone.getUnifiedId());
        var partner = contactRepository.getByUuid(uuid);
        if (partner == null) {
            throw new EntryNotFoundException("contact", uuid);
        }
        return super.savePhone(phone);
    }
}
