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

    @Mapping(target = "uuid", expression = "java(contact.getId() != null ? contact.getId().toString() : null)")
    @Mapping(target = "partnerUuid", expression = "java(contact.getPartnerUuid() != null ? contact.getPartnerUuid().toString() : null)")
    @Mapping(target = "legalForm", source = "type", qualifiedByName = "toLegalType")
    Contact toContact(ContactEntity contact);

    @Named("toLegalType")
    static Contact.LegalFormEnum toLegalType(LegalType legalType) {
        return legalType != null ? Contact.LegalFormEnum.valueOf(legalType.name()) : null;
    }

    @Mapping(target = "id", expression = "java(mapUuid(contact.getUuid()))")
    @Mapping(target = "partnerUuid", expression = "java(mapUuid(contact.getPartnerUuid()))")
    @Mapping(target = "type", source = "legalForm", qualifiedByName = "toLegalType")
    ContactEntity toContact(Contact contact);

    @Named("toLegalType")
    static LegalType toLegalType(Contact.LegalFormEnum legalType) {
        return legalType != null ? LegalType.valueOf(legalType.getValue()) : null;
    }

    @Named("updateContact")
    @Mapping(target = "id", expression = "java(mapUuid(contact.getUuid()))")
    @Mapping(target = "partnerUuid", expression = "java(mapUuid(contact.getPartnerUuid()))")
    void updateContact(Contact contact, @MappingTarget() ContactEntity contactEntity);
}
