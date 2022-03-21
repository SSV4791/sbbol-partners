package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Logged;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PhoneMapper;
import ru.sberbank.pprb.sbbol.partners.model.PhoneCreate;
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
    @Transactional
    public PhoneResponse savePhone(PhoneCreate phone) {
        var uuid = UUID.fromString(phone.getUnifiedId());
        var partner = contactRepository.getByUuid(uuid);
        if (partner == null) {
            throw new EntryNotFoundException("contact", uuid);
        }
        return super.savePhone(phone);
    }
}
