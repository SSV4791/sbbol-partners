package ru.sberbank.pprb.sbbol.partners.mapper.partner.decorator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactEmailEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactPhoneEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.ContactEmailMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.ContactMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.ContactPhoneMapper;
import ru.sberbank.pprb.sbbol.partners.model.Contact;
import ru.sberbank.pprb.sbbol.partners.model.ContactChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.Email;
import ru.sberbank.pprb.sbbol.partners.model.Phone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ContactMapperDecorator implements ContactMapper {

    @Autowired
    @Qualifier("delegate")
    private ContactMapper delegate;

    @Autowired
    private ContactPhoneMapper phoneMapper;

    @Autowired
    private ContactEmailMapper emailMapper;

    @Override
    public Contact toContact(ContactChangeFullModel contactChangeFullModel, String digitalId, String partnerId) {
        var contact = delegate.toContact(contactChangeFullModel, digitalId, partnerId);
        Optional.ofNullable(contact.getPhones())
            .ifPresent(phones ->
                phones.forEach(phone -> {
                    phone.setDigitalId(digitalId);
                    phone.unifiedId(contact.getId());
                })
            );
        Optional.ofNullable(contact.getEmails())
            .ifPresent(emails ->
                emails.forEach(email -> {
                    email.setDigitalId(digitalId);
                    email.unifiedId(contact.getId());
                })
            );
        return contact;
    }

    @Override
    public void patchContact(Contact contact, ContactEntity contactEntity) {
        var mergedPhones = mergePhonesForPatchingContact(contactEntity.getPhones(), contact.getPhones());
        if (!CollectionUtils.isEmpty(mergedPhones)) {
            contact.setPhones(new HashSet<>(mergedPhones));
        }
        var mergedEmails = mergeEmailsForPatchingContact(contactEntity.getEmails(), contact.getEmails());
        if (!CollectionUtils.isEmpty(mergedEmails)) {
            contact.setEmails(new HashSet<>(mergedEmails));
        }
        delegate.patchContact(contact, contactEntity);
        delegate.mapBidirectional(contactEntity);
    }

    private List<Phone> mergePhonesForPatchingContact(List<ContactPhoneEntity> initialPhones, Set<Phone> updatedPhones) {
        if (CollectionUtils.isEmpty(initialPhones) && CollectionUtils.isEmpty(updatedPhones)) {
            return Collections.emptyList();
        }
        if (CollectionUtils.isEmpty(initialPhones)) {
            return new ArrayList<>(updatedPhones);
        }
        if (CollectionUtils.isEmpty(updatedPhones)) {
            return initialPhones.stream()
                .map(phoneMapper::toPhone)
                .collect(Collectors.toList());
        }
        var upgradedInitialPhones = initialPhones.stream()
            .filter(initialPhone ->
                updatedPhones.stream()
                    .noneMatch(updatedPhone -> initialPhone.getUuid().toString().equals(updatedPhone.getId()))
            )
            .map(phoneMapper::toPhone)
            .collect(Collectors.toList());
        return Stream.concat(upgradedInitialPhones.stream(), updatedPhones.stream())
            .collect(Collectors.toList());
    }

    private List<Email> mergeEmailsForPatchingContact(List<ContactEmailEntity> initialEmails, Set<Email> updatedEmails) {
        if (CollectionUtils.isEmpty(initialEmails) && CollectionUtils.isEmpty(updatedEmails)) {
            return Collections.emptyList();
        }
        if (CollectionUtils.isEmpty(initialEmails)) {
            return new ArrayList<>(updatedEmails);
        }
        if (CollectionUtils.isEmpty(updatedEmails)) {
            return initialEmails.stream()
                .map(emailMapper::toEmail)
                .collect(Collectors.toList());
        }
        var upgradedInitialEmails = initialEmails.stream()
            .filter(initialEmail ->
                updatedEmails.stream()
                    .noneMatch(updatedEmail -> initialEmail.getUuid().toString().equals(updatedEmail.getId()))
            )
            .map(emailMapper::toEmail)
            .collect(Collectors.toList());
        return Stream.concat(upgradedInitialEmails.stream(), updatedEmails.stream())
            .collect(Collectors.toList());
    }
}
