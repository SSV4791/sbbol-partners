package ru.sberbank.pprb.sbbol.partners.service.partner;

import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.exception.EntryNotFoundException;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PhoneMapper;
import ru.sberbank.pprb.sbbol.partners.model.Phone;
import ru.sberbank.pprb.sbbol.partners.model.PhoneCreate;
import ru.sberbank.pprb.sbbol.partners.repository.partner.ContactRepository;
import ru.sberbank.pprb.sbbol.partners.repository.partner.PhoneRepository;

@Loggable
public class ContactPhoneServiceImpl extends PhoneServiceImpl {

    private final ContactRepository contactRepository;

    public ContactPhoneServiceImpl(
        ContactRepository contactRepository,
        PhoneRepository phoneRepository,
        PhoneMapper phoneMapper
    ) {
        super(phoneRepository, phoneMapper);
        this.contactRepository = contactRepository;
    }

    @Override
    @Transactional
    public Phone savePhone(PhoneCreate phone) {
        var uuid = phone.getUnifiedId();
        var contact = contactRepository.getByDigitalIdAndUuid(phone.getDigitalId(), uuid);
        if (contact.isEmpty()) {
            throw new EntryNotFoundException("contact", uuid);
        }
        return super.savePhone(phone);
    }
}
