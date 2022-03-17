package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactEmailEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactPhoneEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.LegalType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.Contact;
import ru.sberbank.pprb.sbbol.partners.model.ContactCreate;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {
        ContactEmailMapper.class,
        ContactPhoneMapper.class
    },
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface ContactMapper extends BaseMapper {

    @Mapping(target = "id", expression = "java(contact.getUuid().toString())")
    @Mapping(target = "partnerId", expression = "java(contact.getPartnerUuid().toString())")
    @Mapping(target = "legalForm", source = "type", qualifiedByName = "toLegalType")
    Contact toContact(ContactEntity contact);

    @Named("toLegalType")
    static LegalForm toLegalType(LegalType legalType) {
        return legalType != null ? LegalForm.valueOf(legalType.name()) : null;
    }

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "partnerUuid", expression = "java(mapUuid(contact.getPartnerId()))")
    @Mapping(target = "emails", expression = "java(toEmail(contact.getEmails(), contact.getDigitalId()))")
    @Mapping(target = "phones", expression = "java(toPhone(contact.getPhones(), contact.getDigitalId()))")
    @Mapping(target = "type", source = "legalForm", qualifiedByName = "toLegalType")
    ContactEntity toContact(ContactCreate contact);

    default List<ContactEmailEntity> toEmail(List<String> emails, String digitalId) {
        return emails.stream()
            .map(value -> {
                var contactEmail = new ContactEmailEntity();
                contactEmail.setEmail(value);
                contactEmail.setDigitalId(digitalId);
                return contactEmail;
            }).collect(Collectors.toList());
    }


    default List<ContactPhoneEntity> toPhone(List<String> phones, String digitalId) {
        return phones.stream()
            .map(value -> {
                var contactPhone = new ContactPhoneEntity();
                contactPhone.setPhone(value);
                contactPhone.setDigitalId(digitalId);
                return contactPhone;
            }).collect(Collectors.toList());
    }

    @Mapping(target = "uuid", expression = "java(mapUuid(contact.getId()))")
    @Mapping(target = "partnerUuid", expression = "java(mapUuid(contact.getPartnerId()))")
    @Mapping(target = "type", source = "legalForm", qualifiedByName = "toLegalType")
    ContactEntity toContact(Contact contact);

    @Named("toLegalType")
    static LegalType toLegalType(LegalForm legalType) {
        return legalType != null ? LegalType.valueOf(legalType.getValue()) : null;
    }

    @Named("updateContact")
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "uuid", expression = "java(mapUuid(contact.getId()))")
    @Mapping(target = "partnerUuid", expression = "java(mapUuid(contact.getPartnerId()))")
    void updateContact(Contact contact, @MappingTarget() ContactEntity contactEntity);

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
