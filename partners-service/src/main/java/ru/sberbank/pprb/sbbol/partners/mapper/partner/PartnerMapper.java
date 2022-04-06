package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEmailEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerPhoneEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.LegalType;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.PartnerCitizenshipType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.model.Citizenship;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    @Mapping(target = "gku", ignore = true)
    @Mapping(target = "budget", ignore = true)
    @Mapping(target = "id", expression = "java(partner.getUuid() == null ? null : partner.getUuid().toString())")
    @Mapping(target = "legalForm", source = "legalType", qualifiedByName = "toLegalType")
    @Mapping(target = "citizenship", source = "citizenship", qualifiedByName = "toCitizenshipType")
    Partner toPartner(PartnerEntity partner);

    @Named("toLegalType")
    static LegalForm toLegalType(LegalType legalType) {
        return legalType != null ? LegalForm.valueOf(legalType.name()) : null;
    }

    @Named("toCitizenshipType")
    static Citizenship toCitizenshipType(PartnerCitizenshipType citizenshipType) {
        return citizenshipType != null ? Citizenship.valueOf(citizenshipType.name()) : null;
    }

    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "emails", expression = "java(toEmail(partner.getEmails(), partner.getDigitalId()))")
    @Mapping(target = "phones", expression = "java(toPhone(partner.getPhones(), partner.getDigitalId()))")
    @Mapping(target = "type", constant = "PARTNER")
    @Mapping(target = "legalType", source = "legalForm", qualifiedByName = "toLegalType")
    @Mapping(target = "citizenship", source = "citizenship", qualifiedByName = "toCitizenshipType")
    @Mapping(target = "version", ignore = true)
    PartnerEntity toPartner(PartnerCreate partner);

    default List<PartnerEmailEntity> toEmail(List<String> emails, String digitalId) {
        if (CollectionUtils.isEmpty(emails)) {
            return Collections.emptyList();
        }
        return emails.stream()
            .map(value -> {
                var partnerEmail = new PartnerEmailEntity();
                partnerEmail.setEmail(value);
                partnerEmail.setDigitalId(digitalId);
                return partnerEmail;
            }).collect(Collectors.toList());
    }

    default List<PartnerPhoneEntity> toPhone(List<String> phones, String digitalId) {
        if (CollectionUtils.isEmpty(phones)) {
            return Collections.emptyList();
        }
        return phones.stream()
            .map(value -> {
                var partnerPhone = new PartnerPhoneEntity();
                partnerPhone.setPhone(value);
                partnerPhone.setDigitalId(digitalId);
                return partnerPhone;
            }).collect(Collectors.toList());
    }

    @Named("toCitizenshipType")
    static PartnerCitizenshipType toCitizenshipType(Citizenship citizenshipType) {
        return citizenshipType != null ? PartnerCitizenshipType.valueOf(citizenshipType.getValue()) : null;
    }

    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "uuid", expression = "java(mapUuid(partner.getId()))")
    @Mapping(target = "type", constant = "PARTNER")
    @Mapping(target = "legalType", source = "legalForm", qualifiedByName = "toLegalType")
    @Mapping(target = "citizenship", source = "citizenship", qualifiedByName = "toCitizenshipType")
    PartnerEntity toPartner(Partner partner);

    @Named("toLegalType")
    static LegalType toLegalType(LegalForm legalType) {
        return legalType != null ? LegalType.valueOf(legalType.getValue()) : null;
    }

    @Mapping(target = "uuid", expression = "java(mapUuid(partner.getId()))")
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
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
