package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEmailEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerPhoneEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.LegalType;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.PartnerCitizenshipType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.StringMapper;
import ru.sberbank.pprb.sbbol.partners.model.Citizenship;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModelResponse;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Loggable
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {
        PartnerEmailMapper.class,
        PartnerPhoneMapper.class,
        StringMapper.class
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
    @Mapping(target = "orgName", source = "orgName", qualifiedByName = "toTrimmed")
    @Mapping(target = "firstName", source = "firstName", qualifiedByName = "toTrimmed")
    @Mapping(target = "secondName", source = "secondName", qualifiedByName = "toTrimmed")
    @Mapping(target = "middleName", source = "middleName", qualifiedByName = "toTrimmed")
    @Mapping(target = "emails", expression = "java(toEmail(partner.getEmails(), partner.getDigitalId()))")
    @Mapping(target = "phones", expression = "java(toPhone(partner.getPhones(), partner.getDigitalId()))")
    @Mapping(target = "type", constant = "PARTNER")
    @Mapping(target = "legalType", source = "legalForm", qualifiedByName = "toLegalType")
    @Mapping(target = "citizenship", source = "citizenship", qualifiedByName = "toCitizenshipType")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "search", ignore = true)
    @Mapping(target = "gkuInnEntity", ignore = true)
    PartnerEntity toPartner(PartnerCreate partner);

    default List<PartnerEmailEntity> toEmail(Set<String> emails, String digitalId) {
        if (CollectionUtils.isEmpty(emails)) {
            return Collections.emptyList();
        } else {
            return emails.stream()
                .map(value -> {
                    var partnerEmail = new PartnerEmailEntity();
                    partnerEmail.setEmail(value);
                    partnerEmail.setDigitalId(digitalId);
                    return partnerEmail;
                }).collect(Collectors.toList());
        }
    }

    default List<PartnerPhoneEntity> toPhone(Set<String> phones, String digitalId) {
        if (CollectionUtils.isEmpty(phones)) {
            return Collections.emptyList();
        } else {
            return phones.stream()
                .map(value -> {
                    var partnerPhone = new PartnerPhoneEntity();
                    partnerPhone.setPhone(value);
                    partnerPhone.setDigitalId(digitalId);
                    return partnerPhone;
                }).collect(Collectors.toList());
        }
    }

    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "orgName", source = "orgName", qualifiedByName = "toTrimmed")
    @Mapping(target = "firstName", source = "firstName", qualifiedByName = "toTrimmed")
    @Mapping(target = "secondName", source = "secondName", qualifiedByName = "toTrimmed")
    @Mapping(target = "middleName", source = "middleName", qualifiedByName = "toTrimmed")
    @Mapping(target = "emails", expression = "java(toEmail(partner.getEmails(), partner.getDigitalId()))")
    @Mapping(target = "phones", expression = "java(toPhone(partner.getPhones(), partner.getDigitalId()))")
    @Mapping(target = "type", constant = "PARTNER")
    @Mapping(target = "legalType", source = "legalForm", qualifiedByName = "toLegalType")
    @Mapping(target = "citizenship", source = "citizenship", qualifiedByName = "toCitizenshipType")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "search", ignore = true)
    @Mapping(target = "gkuInnEntity", ignore = true)
    PartnerEntity toPartner(PartnerCreateFullModel partner);

    @Named("toCitizenshipType")
    static PartnerCitizenshipType toCitizenshipType(Citizenship citizenshipType) {
        return citizenshipType != null ? PartnerCitizenshipType.valueOf(citizenshipType.getValue()) : null;
    }

    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "uuid", expression = "java(mapUuid(partner.getId()))")
    @Mapping(target = "orgName", source = "orgName", qualifiedByName = "toTrimmed")
    @Mapping(target = "firstName", source = "firstName", qualifiedByName = "toTrimmed")
    @Mapping(target = "secondName", source = "secondName", qualifiedByName = "toTrimmed")
    @Mapping(target = "middleName", source = "middleName", qualifiedByName = "toTrimmed")
    @Mapping(target = "type", constant = "PARTNER")
    @Mapping(target = "legalType", source = "legalForm", qualifiedByName = "toLegalType")
    @Mapping(target = "citizenship", source = "citizenship", qualifiedByName = "toCitizenshipType")
    @Mapping(target = "search", ignore = true)
    @Mapping(target = "gkuInnEntity", ignore = true)
    PartnerEntity toPartner(Partner partner);

    @Named("toLegalType")
    static LegalType toLegalType(LegalForm legalType) {
        return legalType != null ? LegalType.valueOf(legalType.getValue()) : null;
    }

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "legalType", source = "legalForm", qualifiedByName = "toLegalType")
    @Mapping(target = "citizenship", source = "citizenship", qualifiedByName = "toCitizenshipType")
    @Mapping(target = "search", ignore = true)
    @Mapping(target = "gkuInnEntity", ignore = true)
    void updatePartner(Partner partner, @MappingTarget() PartnerEntity partnerEntity);

    @AfterMapping
    default void mapBidirectional(@MappingTarget PartnerEntity partner) {
        var searchSubString =
            Stream.of(
                    partner.getInn(),
                    partner.getKpp(),
                    partner.getOrgName(),
                    partner.getSecondName(),
                    partner.getFirstName(),
                    partner.getMiddleName()
                )
                .filter(Objects::nonNull)
                .collect(Collectors.joining(StringUtils.EMPTY));
        partner.setSearch(searchSubString);
        var phones = partner.getPhones();
        if (phones != null) {
            for (var phone : phones) {
                if (phone != null) {
                    phone.setPartner(partner);
                    phone.setDigitalId(partner.getDigitalId());
                }
            }
        }
        var emails = partner.getEmails();
        if (emails != null) {
            for (var email : emails) {
                if (email != null) {
                    email.setPartner(partner);
                    email.setDigitalId(partner.getDigitalId());
                }
            }
        }
    }

    @Mapping(target = "gku", ignore = true)
    @Mapping(target = "budget", ignore = true)
    @Mapping(target = "id", expression = "java(partner.getUuid() == null ? null : partner.getUuid().toString())")
    @Mapping(target = "legalForm", source = "legalType", qualifiedByName = "toLegalType")
    @Mapping(target = "citizenship", source = "citizenship", qualifiedByName = "toCitizenshipType")
    @Mapping(target = "accounts", ignore = true)
    @Mapping(target = "documents", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "contacts", ignore = true)
    PartnerCreateFullModelResponse toPartnerMullResponse(PartnerEntity partner);
}
