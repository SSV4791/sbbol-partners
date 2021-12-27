package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.LegalType;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.PartnerCitizenshipType;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.PartnerType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.Partner;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {
        PartnerEmailMapper.class,
        PartnerPhoneMapper.class
    },
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface PartnerMapper extends BaseMapper {

    @Mapping(target = "uuid", expression = "java(partner.getId() != null ? partner.getId().toString() : null)")
    @Mapping(target = "partnerType", source = "type", qualifiedByName = "toPartnerType")
    @Mapping(target = "legalForm", source = "legalType", qualifiedByName = "toLegalType")
    @Mapping(target = "citizenship", source = "citizenship", qualifiedByName = "toCitizenshipType")
    Partner toPartner(PartnerEntity partner);

    @Named("toPartnerType")
    static Partner.PartnerTypeEnum toPartnerType(PartnerType partnerType) {
        return partnerType != null ? Partner.PartnerTypeEnum.valueOf(partnerType.name()) : null;
    }

    @Named("toLegalType")
    static Partner.LegalFormEnum toLegalType(LegalType legalType) {
        return legalType != null ? Partner.LegalFormEnum.valueOf(legalType.name()) : null;
    }

    @Named("toCitizenshipType")
    static Partner.CitizenshipEnum toCitizenshipType(PartnerCitizenshipType citizenshipType) {
        return citizenshipType != null ? Partner.CitizenshipEnum.valueOf(citizenshipType.name()) : null;
    }

    @Mapping(target = "id", expression = "java(mapUuid(partner.getUuid()))")
    @Mapping(target = "type ", source = "partnerType", qualifiedByName = "toPartnerType")
    @Mapping(target = "legalType", source = "legalForm", qualifiedByName = "toLegalType")
    @Mapping(target = "citizenship", source = "citizenship", qualifiedByName = "toCitizenshipType")
    PartnerEntity toPartner(Partner partner);

    @Named("toLegalType")
    static LegalType toLegalType(Partner.LegalFormEnum legalType) {
        return legalType != null ? LegalType.valueOf(legalType.getValue()) : null;
    }

    @Named("toPartnerType")
    static PartnerType toPartnerType(Partner.PartnerTypeEnum partnerType) {
        return partnerType != null ? PartnerType.valueOf(partnerType.getValue()) : null;
    }

    @Named("toCitizenshipType")
    static PartnerCitizenshipType toCitizenshipType(Partner.CitizenshipEnum citizenshipEnum) {
        return citizenshipEnum != null ? PartnerCitizenshipType.valueOf(citizenshipEnum.getValue()) : null;
    }

    @Mapping(target = "id", expression = "java(mapUuid(partner.getUuid()))")
    @Mapping(target = "type", source = "partnerType", qualifiedByName = "toPartnerType")
    @Mapping(target = "legalType", source = "legalForm", qualifiedByName = "toLegalType")
    @Mapping(target = "citizenship", source = "citizenship", qualifiedByName = "toCitizenshipType")
    void updatePartner(Partner partner, @MappingTarget() PartnerEntity partnerEntity);
}
