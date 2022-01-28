package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.LegalType;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.PartnerCitizenshipType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
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

    @Mapping(target = "id", expression = "java(partner.getUuid().toString())")
    @Mapping(target = "legalForm", source = "legalType", qualifiedByName = "toLegalType")
    @Mapping(target = "citizenship", source = "citizenship", qualifiedByName = "toCitizenshipType")
    Partner toPartner(PartnerEntity partner);

    @Named("toLegalType")
    static LegalForm toLegalType(LegalType legalType) {
        return legalType != null ? LegalForm.valueOf(legalType.name()) : null;
    }

    @Named("toCitizenshipType")
    static Partner.CitizenshipEnum toCitizenshipType(PartnerCitizenshipType citizenshipType) {
        return citizenshipType != null ? Partner.CitizenshipEnum.valueOf(citizenshipType.name()) : null;
    }

    @Mapping(target = "uuid", expression = "java(mapUuid(partner.getId()))")
    @Mapping(target = "type", constant = "PARTNER")
    @Mapping(target = "legalType", source = "legalForm", qualifiedByName = "toLegalType")
    @Mapping(target = "citizenship", source = "citizenship", qualifiedByName = "toCitizenshipType")
    PartnerEntity toPartner(Partner partner);

    @Named("toLegalType")
    static LegalType toLegalType(LegalForm legalType) {
        return legalType != null ? LegalType.valueOf(legalType.getValue()) : null;
    }

    @Named("toCitizenshipType")
    static PartnerCitizenshipType toCitizenshipType(Partner.CitizenshipEnum citizenshipEnum) {
        return citizenshipEnum != null ? PartnerCitizenshipType.valueOf(citizenshipEnum.getValue()) : null;
    }

    @Mapping(target = "uuid", expression = "java(mapUuid(partner.getId()))")
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "legalType", source = "legalForm", qualifiedByName = "toLegalType")
    @Mapping(target = "citizenship", source = "citizenship", qualifiedByName = "toCitizenshipType")
    void updatePartner(Partner partner, @MappingTarget() PartnerEntity partnerEntity);

    @AfterMapping
    default void mapBidirectional(@MappingTarget PartnerEntity partner) {
        var phones = partner.getPhones();
        if (phones != null) {
            for (var phone : phones) {
                if (phone != null) {
                    phone.setPartner(partner);
                }
            }
        }
        var emails = partner.getEmails();
        if (emails != null) {
            for (var email : emails) {
                if (email != null) {
                    email.setPartner(partner);
                }
            }
        }
    }
}
