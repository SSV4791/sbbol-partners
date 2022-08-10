package ru.sberbank.pprb.sbbol.migration.correspondents.mapper;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.migration.correspondents.enums.MigrationLegalType;
import ru.sberbank.pprb.sbbol.migration.correspondents.model.MigrationCorrespondentCandidate;
import ru.sberbank.pprb.sbbol.partners.entity.partner.AccountEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.BankEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEmailEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerPhoneEntity;
import ru.sberbank.pprb.sbbol.partners.entity.partner.enums.AccountStateType;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.AccountMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.PartnerMapper;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(
    componentModel = "spring",
    imports = {
        AccountMapper.class,
        PartnerMapper.class,
        MigrationLegalType.class
    }
)
public interface MigrationPartnerMapper {

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "type", constant = "PARTNER")
    @Mapping(target = "citizenship", constant = "UNKNOWN")
    @Mapping(target = "comment", source = "source.description")
    @Mapping(target = "legalType", source = "source.legalType")
    @Mapping(target = "version", source = "source.version")
    @Mapping(target = "phones", expression = "java(toPhone(source.getCorrPhoneNumber(), digitalId))")
    @Mapping(target = "emails", expression = "java(toEmail(source.getCorrEmail(), digitalId))")
    @Mapping(target = "orgName", expression = "java(source.getLegalType() != MigrationLegalType.PHYSICAL_PERSON ? source.getName() : null)")
    @Mapping(target = "firstName", expression = "java(source.getLegalType() == MigrationLegalType.PHYSICAL_PERSON ? source.getName() : null)")
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    PartnerEntity toPartnerEntity(String digitalId, MigrationCorrespondentCandidate source);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "digitalId", source = "digitalId")
    @Mapping(target = "partnerUuid", source = "partnerUuid")
    @Mapping(target = "account", source = "source.account")
    @Mapping(target = "bank.name", source = "source.bankName")
    @Mapping(target = "bank.bic", source = "source.bic")
    @Mapping(target = "bank.intermediary", constant = "false")
    @Mapping(target = "bank.bankAccount.account", source = "source.bankAccount")
    @Mapping(target = "priorityAccount", constant = "false")
    @Mapping(target = "state", expression = "java(toSigned(source.isSigned()))")
    @Mapping(target = "search", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
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
    @Mapping(target = "legalType", source = "source.legalType")
    @Mapping(target = "version", source = "source.version")
    @Mapping(target = "phones", expression = "java(toPhone(partner.getPhones(), source.getCorrPhoneNumber(), digitalId))")
    @Mapping(target = "emails", expression = "java(toEmail(partner.getEmails(), source.getCorrEmail(), digitalId))")
    @Mapping(target = "orgName", expression = "java(source.getLegalType() != MigrationLegalType.PHYSICAL_PERSON ? source.getName() : null)")
    @Mapping(target = "firstName", expression = "java(source.getLegalType() == MigrationLegalType.PHYSICAL_PERSON ? source.getName() : null)")
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
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

    @AfterMapping
    default void mapBidirectional(@MappingTarget AccountEntity account) {
        var searchSubString = Stream.of(
                account.getPartnerUuid().toString(),
                account.getAccount()
            )
            .filter(Objects::nonNull)
            .collect(Collectors.joining(StringUtils.EMPTY));
        var bank = account.getBank();
        if (bank != null) {
            bank.setAccount(account);
            searchSubString = Stream.of(
                    searchSubString,
                    bank.getBic()
                )
                .filter(Objects::nonNull)
                .collect(Collectors.joining(StringUtils.EMPTY));
            var bankAccount = bank.getBankAccount();
            if (bankAccount != null) {
                searchSubString = Stream.of(
                        searchSubString,
                        bankAccount.getAccount()
                    )
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(StringUtils.EMPTY));
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

}
