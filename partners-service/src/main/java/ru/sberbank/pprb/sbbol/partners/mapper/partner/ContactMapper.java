package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.partners.entity.partner.ContactEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.LegalType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.Contact;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;

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

    @Mapping(target = "uuid", expression = "java(mapUuid(contact.getId()))")
    @Mapping(target = "partnerUuid", expression = "java(mapUuid(contact.getPartnerId()))")
    @Mapping(target = "type", source = "legalForm", qualifiedByName = "toLegalType")
    ContactEntity toContact(Contact contact);

    @Named("toLegalType")
    static LegalType toLegalType(LegalForm legalType) {
        return legalType != null ? LegalType.valueOf(legalType.getValue()) : null;
    }

    @Named("updateContact")
    @Mapping(target = "uuid", expression = "java(mapUuid(contact.getId()))")
    @Mapping(target = "partnerUuid", expression = "java(mapUuid(contact.getPartnerId()))")
    void updateContact(Contact contact, @MappingTarget() ContactEntity contactEntity);
}
