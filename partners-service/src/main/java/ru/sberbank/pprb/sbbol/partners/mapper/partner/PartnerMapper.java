package ru.sberbank.pprb.sbbol.partners.mapper.partner;

import org.jetbrains.annotations.NotNull;
import org.mapstruct.AfterMapping;
import org.mapstruct.DecoratedWith;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.partners.aspect.logger.Loggable;
import ru.sberbank.pprb.sbbol.partners.entity.partner.GkuInnEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEmailEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerPhoneEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.LegalType;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.PartnerCitizenshipType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.StringMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.decorator.PartnerMapperDecorator;
import ru.sberbank.pprb.sbbol.partners.model.Citizenship;
import ru.sberbank.pprb.sbbol.partners.model.LegalForm;
import ru.sberbank.pprb.sbbol.partners.model.Partner;
import ru.sberbank.pprb.sbbol.partners.model.PartnerChangeFullModel;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreate;
import ru.sberbank.pprb.sbbol.partners.model.PartnerCreateFullModel;
import ru.sberbank.pprb.sbbol.partners.model.PartnerFullModelResponse;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper.prepareSearchString;

@Loggable
@Mapper(
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {
        BaseMapper.class,
        PartnerEmailMapper.class,
        PartnerPhoneMapper.class,
        StringMapper.class
    },
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
@DecoratedWith(PartnerMapperDecorator.class)
public interface PartnerMapper {

    @Mapping(target = "id", source = "uuid")
    @Mapping(target = "legalForm", source = "legalType", qualifiedByName = "toLegalType")
    @Mapping(target = "citizenship", source = "citizenship", qualifiedByName = "toCitizenshipType")
    @Mapping(target = "changeDate", source = "lastModifiedDate")
    @Mapping(target = "gku", source = "gkuInnEntity", qualifiedByName = "isGku")
    Partner toPartner(PartnerEntity partner);

    @Named("toLegalType")
    static LegalForm toLegalType(LegalType legalType) {
        return legalType != null ? LegalForm.valueOf(legalType.name()) : null;
    }

    @Named("toCitizenshipType")
    static Citizenship toCitizenshipType(PartnerCitizenshipType citizenshipType) {
        return citizenshipType != null ? Citizenship.valueOf(citizenshipType.name()) : null;
    }

    @Named("isGku")
    static Boolean isGku(GkuInnEntity isGkuInn) {
        return isGkuInn != null;
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
    @Mapping(target = "uuid", source = "id")
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
    @Mapping(target = "orgName", source = "orgName", qualifiedByName = "toTrimmed")
    @Mapping(target = "firstName", source = "firstName", qualifiedByName = "toTrimmed")
    @Mapping(target = "secondName", source = "secondName", qualifiedByName = "toTrimmed")
    @Mapping(target = "middleName", source = "middleName", qualifiedByName = "toTrimmed")
    @Mapping(target = "legalType", source = "legalForm", qualifiedByName = "toLegalType")
    @Mapping(target = "citizenship", source = "citizenship", qualifiedByName = "toCitizenshipType")
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "search", ignore = true)
    @Mapping(target = "gkuInnEntity", ignore = true)
    void updatePartner(Partner partner, @MappingTarget() PartnerEntity partnerEntity);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "orgName", source = "orgName", qualifiedByName = "toTrimmed")
    @Mapping(target = "firstName", source = "firstName", qualifiedByName = "toTrimmed")
    @Mapping(target = "secondName", source = "secondName", qualifiedByName = "toTrimmed")
    @Mapping(target = "middleName", source = "middleName", qualifiedByName = "toTrimmed")
    @Mapping(target = "legalType", source = "legalForm", qualifiedByName = "toLegalType")
    @Mapping(target = "citizenship", source = "citizenship", qualifiedByName = "toCitizenshipType")
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "search", ignore = true)
    @Mapping(target = "gkuInnEntity", ignore = true)
    void patchPartner(PartnerChangeFullModel partner, @MappingTarget() PartnerEntity partnerEntity);

    @AfterMapping
    default void mapBidirectional(@MappingTarget PartnerEntity partner) {
        var searchSubString =
            prepareSearchField(
                partner.getInn(),
                partner.getKpp(),
                partner.getOrgName(),
                partner.getSecondName(),
                partner.getFirstName(),
                partner.getMiddleName()
            );
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

    @Mapping(target = "id", source = "uuid")
    @Mapping(target = "legalForm", source = "legalType", qualifiedByName = "toLegalType")
    @Mapping(target = "citizenship", source = "citizenship", qualifiedByName = "toCitizenshipType")
    @Mapping(target = "changeDate", source = "lastModifiedDate")
    @Mapping(target = "accounts", ignore = true)
    @Mapping(target = "documents", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "contacts", ignore = true)
    @Mapping(target = "gku", source = "gkuInnEntity", qualifiedByName = "isGku")
    PartnerFullModelResponse toPartnerFullResponse(PartnerEntity partner);

    @NotNull
    default String saveSearchString(String... search) {
        return Stream.of(search)
            .filter(Objects::nonNull)
            .map(it -> it.replace(SPACE, EMPTY))
            .map(it -> it.replaceAll("\\\\", "\\\\\\\\"))
            .collect(Collectors.joining(EMPTY))
            .toLowerCase(Locale.ROOT);
    }

    default String prepareSearchField(String inn, String kpp, String orgName, String secondName, String firstName, String middleName) {
        return prepareSearchString(inn, kpp, orgName, secondName, firstName, middleName);
    }
}
