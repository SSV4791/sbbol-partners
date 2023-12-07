package ru.sberbank.pprb.sbbol.migration.correspondents.mapper;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.DecoratedWith;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.migration.correspondents.enums.MigrationLegalType;
import ru.sberbank.pprb.sbbol.migration.correspondents.mapper.decorator.MigrationPartnerMapperDecorator;
import ru.sberbank.pprb.sbbol.migration.correspondents.model.MigrationCorrespondentCandidate;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.IdsHistoryEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEmailEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerPhoneEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AccountStateType;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.LegalType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.sberbank.pprb.sbbol.partners.entity.partner.enums.ParentType.ACCOUNT;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper.mapUuid;
import static ru.sberbank.pprb.sbbol.partners.mapper.partner.common.BaseMapper.saveSearchString;

@Mapper(
    componentModel = "spring",
    uses = {
        BaseMapper.class,
    },
    imports = {
        LegalType.class,
        AccountMapper.class,
        PartnerMapper.class,
        MigrationLegalType.class
    },
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
@DecoratedWith(MigrationPartnerMapperDecorator.class)
public interface MigrationPartnerMapper {

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "type", constant = "PARTNER")
    @Mapping(target = "citizenship", constant = "UNKNOWN")
    @Mapping(target = "inn", expression = "java(stringNotEmpty(source.getInn()))")
    @Mapping(target = "kpp", expression = "java(stringNotEmpty(source.getKpp()))")
    @Mapping(target = "comment", expression = "java(stringNotEmpty(source.getDescription()))")
    @Mapping(target = "legalType", ignore = true)
    @Mapping(target = "version", source = "source.version")
    @Mapping(target = "phones", expression = "java(toPhone(source.getCorrPhoneNumber(), digitalId))")
    @Mapping(target = "emails", expression = "java(toEmail(source.getCorrEmail(), digitalId))")
    @Mapping(target = "orgName", ignore = true)
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "secondName", ignore = true)
    @Mapping(target = "middleName", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "migrationDate", ignore = true)
    PartnerEntity toPartnerEntity(String digitalId, MigrationCorrespondentCandidate source);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "digitalId", source = "digitalId")
    @Mapping(target = "partnerUuid", source = "partnerUuid")
    @Mapping(target = "account", source = "source.account")
    @Mapping(target = "bank.name", source = "source.bankName")
    @Mapping(target = "bank.bic", source = "source.bic")
    @Mapping(target = "bank.bankAccount.account", source = "source.bankAccount")
    @Mapping(target = "priorityAccount", constant = "false")
    @Mapping(target = "state", expression = "java(toSigned(source.isSigned()))")
    @Mapping(target = "search", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "idLinks", ignore = true)
    @Mapping(target = "partnerType", constant = "PARTNER")
    AccountEntity toAccountEntity(String digitalId, UUID partnerUuid, MigrationCorrespondentCandidate source);

    default AccountStateType toSigned(boolean signed) {
        return AccountStateType.of(signed);
    }

    default List<PartnerEmailEntity> toEmail(String email, String digitalId) {
        if (ObjectUtils.isEmpty(email)) {
            return Collections.emptyList();
        } else {
            var partnerEmail = new PartnerEmailEntity();
            partnerEmail.setEmail(email);
            partnerEmail.setDigitalId(digitalId);
            return Collections.singletonList(partnerEmail);
        }
    }

    default List<PartnerPhoneEntity> toPhone(String phone, String digitalId) {
        if (ObjectUtils.isEmpty(phone)) {
            return Collections.emptyList();
        } else {
            var partnerPhone = new PartnerPhoneEntity();
            partnerPhone.setPhone(phone);
            partnerPhone.setDigitalId(digitalId);
            return Collections.singletonList(partnerPhone);
        }
    }

    @Mapping(target = "type", constant = "PARTNER")
    @Mapping(target = "citizenship", constant = "UNKNOWN")
    @Mapping(target = "comment", source = "source.description")
    @Mapping(target = "inn", expression = "java(stringNotEmpty(source.getInn()))")
    @Mapping(target = "kpp", expression = "java(stringNotEmpty(source.getKpp()))")
    @Mapping(target = "legalType", ignore = true)
    @Mapping(target = "version", source = "source.version")
    @Mapping(target = "phones", expression = "java(toPhone(partner.getPhones(), source.getCorrPhoneNumber(), digitalId))")
    @Mapping(target = "emails", expression = "java(toEmail(partner.getEmails(), source.getCorrEmail(), digitalId))")
    @Mapping(target = "orgName", ignore = true)
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "secondName", ignore = true)
    @Mapping(target = "middleName", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "migrationDate", ignore = true)
    void updatePartnerEntity(String digitalId, MigrationCorrespondentCandidate source, @MappingTarget PartnerEntity partner);

    default List<PartnerEmailEntity> toEmail(List<PartnerEmailEntity> emails, String email, String digitalId) {
        if (ObjectUtils.isEmpty(email)) {
            return emails;
        } else if (CollectionUtils.isEmpty(emails)) {
            var partnerEmail = new PartnerEmailEntity();
            partnerEmail.setEmail(email);
            partnerEmail.setDigitalId(digitalId);
            return Collections.singletonList(partnerEmail);
        }
        var partnerEmail = emails.get(0);
        partnerEmail.setEmail(email);
        partnerEmail.setDigitalId(digitalId);
        return emails;
    }

    default List<PartnerPhoneEntity> toPhone(List<PartnerPhoneEntity> phones, String phone, String digitalId) {
        if (ObjectUtils.isEmpty(phone)) {
            return phones;
        } else if (CollectionUtils.isEmpty(phones)) {
            var partnerPhone = new PartnerPhoneEntity();
            partnerPhone.setPhone(phone);
            partnerPhone.setDigitalId(digitalId);
            return Collections.singletonList(partnerPhone);
        }
        var partnerPhone = phones.get(0);
        partnerPhone.setPhone(phone);
        partnerPhone.setDigitalId(digitalId);
        return phones;
    }

    @Mapping(target = "digitalId", source = "digitalId")
    @Mapping(target = "partnerUuid", source = "partnerUuid")
    @Mapping(target = "account", source = "source.account")
    @Mapping(target = "bank.name", source = "source.bankName")
    @Mapping(target = "bank.bic", source = "source.bic")
    @Mapping(target = "bank.bankAccount.account", source = "source.bankAccount")
    @Mapping(target = "priorityAccount", constant = "false")
    @Mapping(target = "state", expression = "java(toSigned(source.isSigned()))")
    @Mapping(target = "search", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    void updateAccountEntity(String digitalId, UUID partnerUuid, MigrationCorrespondentCandidate source, @MappingTarget AccountEntity account);

    @AfterMapping
    default void mapBidirectional(@MappingTarget AccountEntity account) {
        var searchSubString = saveSearchString(
            account.getPartnerUuid().toString(),
            account.getAccount()
        );
        var bank = account.getBank();
        if (bank != null) {
            bank.setAccount(account);
            searchSubString = saveSearchString(
                searchSubString,
                bank.getBic()
            );
            var bankAccount = bank.getBankAccount();
            if (bankAccount != null) {
                searchSubString = saveSearchString(
                    searchSubString,
                    bankAccount.getAccount()
                );
            }
        }
        account.setSearch(searchSubString);
    }

    @AfterMapping
    default void mapBidirectional(@MappingTarget BankEntity bank) {
        var bankAccount = bank.getBankAccount();
        if (bankAccount != null) {
            bankAccount.setBank(bank);
        }
    }

    @Mapping(target = "name", expression = "java(toName(partner))")
    @Mapping(target = "inn", source = "partner.inn")
    @Mapping(target = "kpp", source = "partner.kpp")
    @Mapping(target = "account", source = "account.account")
    @Mapping(target = "bic", source = "account.bank.bic")
    @Mapping(target = "description", source = "partner.comment")
    @Mapping(target = "pprbGuid", source = "account.uuid", qualifiedByName = "mapUuid")
    @Mapping(target = "bankAccount", source = "account.bank.bankAccount.account")
    @Mapping(target = "replicationGuid", source = "account.uuid", qualifiedByName = "mapUuid")
    @Mapping(target = "signed", expression = "java(toSigned(account.getState()))")
    @Mapping(target = "version", source = "account.version")
    @Mapping(target = "bankName", source = "account.bank.name")
    MigrationCorrespondentCandidate toCounterparty(PartnerEntity partner, AccountEntity account);

    default String toName(PartnerEntity partner) {
        if (partner == null) {
            return null;
        }
        if (partner.getLegalType() != LegalType.PHYSICAL_PERSON) {
            return partner.getOrgName();
        }
        return Stream.of(
                partner.getSecondName(),
                partner.getFirstName(),
                partner.getMiddleName()
            )
            .filter(Objects::nonNull)
            .collect(Collectors.joining(StringUtils.SPACE));
    }

    default boolean toSigned(AccountStateType signed) {
        return signed == AccountStateType.SIGNED;
    }

    default String stringNotEmpty(final String value) {
        return StringUtils.isEmpty(value) ? null : value;
    }

    default AccountEntity fillIdLinks(AccountEntity account, String externalId) {
        var externalUuid = StringUtils.isNotEmpty(externalId) ? mapUuid(externalId) : null;
        var idLink = new IdsHistoryEntity();
        idLink.setAccount(account);
        idLink.setParentType(ACCOUNT);
        idLink.setExternalId(externalUuid);
        idLink.setDigitalId(account.getDigitalId());
        var idLinks = account.getIdLinks();
        if (!CollectionUtils.isEmpty(idLinks) && Objects.nonNull(externalUuid)) {
            long count = idLinks.stream()
                .filter(link -> link.getExternalId().equals(externalUuid))
                .count();
            if (count != 0) {
                return account;
            }
            idLinks.add(idLink);
            return account;
        }
        account.getIdLinks().add(idLink);
        return account;
    }
}
