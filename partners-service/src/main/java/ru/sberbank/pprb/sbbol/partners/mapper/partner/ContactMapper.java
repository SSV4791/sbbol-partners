package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.DecoratedWith;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactEmailEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactPhoneEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.LegalType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.decorator.ContactMapperDecorator;
import ru.sberbank.pprb.sbbol.partners.model.Contact;
import ru.sberbank.pprb.sbbol.partners.model.ContactChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.ContactCreate;
import ru.sberbank.pprb.sbbol.partners.model.ContactCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Loggable
@Mapper(
    uses = {
        BaseMapper.class,
        ContactEmailMapper.class,
        ContactPhoneMapper.class
    },
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
@DecoratedWith(ContactMapperDecorator.class)
public interface ContactMapper {

    @Mapping(target = "id", source = "uuid")
    @Mapping(target = "partnerId", source = "partnerUuid")
    @Mapping(target = "legalForm", source = "type", qualifiedByName = "toLegalType")
    Contact toContact(ContactEntity contact);

    @Named("toLegalType")
    static LegalForm toLegalType(LegalType legalType) {
        return legalType != null ? LegalForm.valueOf(legalType.name()) : null;
    }

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "partnerUuid", source = "partnerId")
    @Mapping(target = "emails", expression = "java(toEmail(contact.getEmails(), contact.getDigitalId()))")
    @Mapping(target = "phones", expression = "java(toPhone(contact.getPhones(), contact.getDigitalId()))")
    @Mapping(target = "type", source = "legalForm", qualifiedByName = "toLegalType")
    ContactEntity toContact(ContactCreate contact);

    default List<ContactEmailEntity> toEmail(Set<String> emails, String digitalId) {
        if (CollectionUtils.isEmpty(emails)) {
            return Collections.emptyList();
        } else {
            return emails.stream()
                .map(value -> {
                    var contactEmail = new ContactEmailEntity();
                    contactEmail.setEmail(value);
                    contactEmail.setDigitalId(digitalId);
                    return contactEmail;
                }).collect(Collectors.toList());
        }
    }

    default List<ContactPhoneEntity> toPhone(Set<String> phones, String digitalId) {
        if (CollectionUtils.isEmpty(phones)) {
            return Collections.emptyList();
        } else {
            return phones.stream()
                .map(value -> {
                    var contactPhone = new ContactPhoneEntity();
                    contactPhone.setPhone(value);
                    contactPhone.setDigitalId(digitalId);
                    return contactPhone;
                }).collect(Collectors.toList());
        }
    }

    default List<ContactCreate> toContacts(Set<ContactCreateFullModel> contacts, String digitalId, UUID partnerUuid) {
        if (CollectionUtils.isEmpty(contacts)) {
            return Collections.emptyList();
        }
        return contacts.stream()
            .map(value -> toContact(value, digitalId, partnerUuid))
            .collect(Collectors.toList());
    }

    @Mapping(target = "partnerId", source = "partnerUuid")
    @Mapping(target = "digitalId", source = "digitalId")
    @Mapping(target = "emails", source = "contact.emails")
    @Mapping(target = "phones", source = "contact.phones")
    @Mapping(target = "legalForm", source = "contact.legalForm")
    @Mapping(target = "orgName", source = "contact.orgName")
    @Mapping(target = "firstName", source = "contact.firstName")
    @Mapping(target = "secondName", source = "contact.secondName")
    @Mapping(target = "middleName", source = "contact.middleName")
    @Mapping(target = "position", source = "contact.position")
    ContactCreate toContact(ContactCreateFullModel contact, String digitalId, UUID partnerUuid);

    Contact toContact(ContactChangeFullModel contact, String digitalId, UUID partnerId);

    ContactCreate toContactCreate(ContactChangeFullModel contact, String digitalId, UUID partnerId);

    @Mapping(target = "uuid", source = "id")
    @Mapping(target = "partnerUuid", source = "partnerId")
    @Mapping(target = "type", source = "legalForm", qualifiedByName = "toLegalType")
    @Mapping(target = "lastModifiedDate", ignore = true)
    ContactEntity toContact(Contact contact);

    @Named("toLegalType")
    static LegalType toLegalType(LegalForm legalType) {
        return legalType != null ? LegalType.valueOf(legalType.getValue()) : null;
    }

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "partnerUuid", source = "partnerId")
    void updateContact(Contact contact, @MappingTarget() ContactEntity contactEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "partnerUuid", source = "partnerId")
    void patchContact(Contact contact, @MappingTarget() ContactEntity contactEntity);

    @AfterMapping
    default void mapBidirectional(@MappingTarget ContactEntity contact) {
        var phones = contact.getPhones();
        if (phones != null) {
            for (var phone : phones) {
                if (phone != null) {
                    phone.setContact(contact);
                }
            }
        }
        var emails = contact.getEmails();
        if (emails != null) {
            for (var email : emails) {
                if (email != null) {
                    email.setContact(contact);
                }
            }
        }
    }
}
