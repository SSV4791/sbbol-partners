package ru.sberbank.pprb.sbbol.migration.correspondents.mapper;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.sberbank.pprb.sbbol.migration.correspondents.enums.MigrationAccountStateType;
import ru.sberbank.pprb.sbbol.migration.correspondents.enums.MigrationLegalType;
import ru.sberbank.pprb.sbbol.migration.correspondents.entity.MigrationBankAccountEntity;
import ru.sberbank.pprb.sbbol.migration.correspondents.entity.MigrationBankEntity;
import ru.sberbank.pprb.sbbol.migration.correspondents.entity.MigrationPartnerAccountEntity;
import ru.sberbank.pprb.sbbol.migration.correspondents.entity.MigrationPartnerEmailEntity;
import ru.sberbank.pprb.sbbol.migration.correspondents.entity.MigrationPartnerEntity;
import ru.sberbank.pprb.sbbol.migration.correspondents.entity.MigrationPartnerPhoneEntity;
import ru.sberbank.pprb.sbbol.migration.correspondents.model.MigrationCorrespondentCandidate;

import java.time.OffsetDateTime;

@Mapper(componentModel = "spring", imports = {MigrationLegalType.class, OffsetDateTime.class})
public interface MigrationPartnerMapper {

    @Mapping(target = "type", constant = "PARTNER")
    @Mapping(target = "citizenship", constant = "RUSSIA")
    @Mapping(target = "comment", source = "source.description")
    @Mapping(target = "legalType", source = "source.legalType")
    @Mapping(target = "version", source = "source.version")
    @Mapping(target = "phone", expression = "java(toMigrationPartnerPhoneEntity(source.getCorrPhoneNumber(), source.getVersion(), digitalId))")
    @Mapping(target = "email", expression = "java(toMigrationPartnerEmailEntity(source.getCorrEmail(), source.getVersion(), digitalId))")
    @Mapping(target = "account", expression = "java(toMigrationPartnerAccountEntity(digitalId, source))")
    @Mapping(target = "orgName", expression = "java(source.getLegalType() != MigrationLegalType.PHYSICAL_PERSON ? source.getName() : null)")
    @Mapping(target = "firstName", expression = "java(source.getLegalType() == MigrationLegalType.PHYSICAL_PERSON ? source.getName() : null)")
    @Mapping(target = "createDate", expression = "java(OffsetDateTime.now())")
    @Mapping(target = "lastModifiedDate", expression = "java(OffsetDateTime.now())")
    MigrationPartnerEntity toMigrationPartnerEntity(String digitalId, MigrationCorrespondentCandidate source);

    default MigrationPartnerAccountEntity toMigrationPartnerAccountEntity(String digitalId, MigrationCorrespondentCandidate source) {
        String account = source.getAccount();
        if (StringUtils.isEmpty(account)) {
            return null;
        }
        MigrationPartnerAccountEntity accountEntity = new MigrationPartnerAccountEntity();
        accountEntity.setAccount(account);
        accountEntity.setDigitalId(digitalId);
        accountEntity.setState(MigrationAccountStateType.of(source.isSigned()));
        accountEntity.setBank(toMigrationBankEntities(source.getBic(), source.getBankAccount(), source.getVersion()));
        accountEntity.setVersion(source.getVersion());
        mapBidirectional(accountEntity);
        return accountEntity;
    }

    default MigrationBankEntity toMigrationBankEntities(String bic, String bankAccount, Long version) {
        if (StringUtils.isAllEmpty(bic, bankAccount)) {
            return null;
        }
        MigrationBankEntity migrationBankEntity = new MigrationBankEntity();
        migrationBankEntity.setBankAccount(toMigrationBankAccountEntity(bankAccount, version));
        migrationBankEntity.setBic(bic);
        migrationBankEntity.setIntermediary(Boolean.FALSE);
        migrationBankEntity.setVersion(version);
        mapBidirectional(migrationBankEntity);
        return migrationBankEntity;
    }

    default MigrationBankAccountEntity toMigrationBankAccountEntity(String account, Long version) {
        if (account == null || version == null) {
            return null;
        }
        MigrationBankAccountEntity migrationBankAccountEntity = new MigrationBankAccountEntity();
        migrationBankAccountEntity.setAccount(account);
        migrationBankAccountEntity.setVersion(version);
        return migrationBankAccountEntity;
    }

    default MigrationPartnerPhoneEntity toMigrationPartnerPhoneEntity(String phone, Long version, String digitalId) {
        if (phone == null || version == null) {
            return null;
        }
        MigrationPartnerPhoneEntity migrationPartnerPhoneEntity = new MigrationPartnerPhoneEntity();
        migrationPartnerPhoneEntity.setPhone(phone);
        migrationPartnerPhoneEntity.setDigitalId(digitalId);
        migrationPartnerPhoneEntity.setVersion(version);
        return migrationPartnerPhoneEntity;
    }

    default MigrationPartnerEmailEntity toMigrationPartnerEmailEntity(String email, Long version, String digitalId) {
        if (email == null || version == null) {
            return null;
        }
        MigrationPartnerEmailEntity migrationPartnerEmailEntity = new MigrationPartnerEmailEntity();
        migrationPartnerEmailEntity.setEmail(email);
        migrationPartnerEmailEntity.setDigitalId(digitalId);
        migrationPartnerEmailEntity.setVersion(version);
        return migrationPartnerEmailEntity;
    }

    @AfterMapping
    default void mapBidirectional(@MappingTarget MigrationPartnerEntity partner) {
        OffsetDateTime currentDate = OffsetDateTime.now();
        partner.setCreateDate(currentDate);
        partner.setLastModifiedDate(currentDate);
        MigrationPartnerPhoneEntity phone = partner.getPhone();
        if (phone != null) {
            phone.setPartner(partner);
        }
        MigrationPartnerEmailEntity email = partner.getEmail();
        if (email != null) {
            email.setPartner(partner);
        }
        MigrationPartnerAccountEntity account = partner.getAccount();
        if (account != null) {
            account.setPartner(partner);
        }
    }

    default void mapBidirectional(MigrationBankEntity bank) {
        var bankAccount = bank.getBankAccount();
        if (bankAccount != null) {
            bankAccount.setBank(bank);
        }
    }

    default void mapBidirectional(MigrationPartnerAccountEntity account) {
        OffsetDateTime currentDate = OffsetDateTime.now();
        account.setCreateDate(currentDate);
        account.setLastModifiedDate(currentDate);
        MigrationBankEntity bank = account.getBank();
        if (bank != null) {
            bank.setAccount(account);
        }
    }
}
