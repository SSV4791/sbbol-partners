package ru.sberbank.pprb.sbbol.partners.migration.mapper;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.migration.correspondents.mapper.MigrationPartnerMapper;
import ru.sberbank.pprb.sbbol.migration.correspondents.model.MigrationCorrespondentCandidate;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.entity.partner.PartnerEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MigrationPartnerMapperTest extends BaseUnitConfiguration {

    private static final MigrationPartnerMapper mapper = Mappers.getMapper(MigrationPartnerMapper.class);
    private static final String DIGITAL_ID = RandomStringUtils.random(20);

    @Test
    void toMigrationPartnerEntityTest() {
        var migrationCorrespondentCandidate = factory.manufacturePojo(MigrationCorrespondentCandidate.class);
        var migrationPartnerEntity = mapper.toPartnerEntity(DIGITAL_ID, migrationCorrespondentCandidate);
        var expectedVersion = migrationCorrespondentCandidate.getVersion();
        assertThat(migrationPartnerEntity.getVersion()).isEqualTo(expectedVersion);
        assertThat(migrationPartnerEntity.getDigitalId()).isEqualTo(DIGITAL_ID);
        assertThat(migrationPartnerEntity.getEmails().get(0).getEmail())
            .isEqualTo(migrationCorrespondentCandidate.getCorrEmail());
        assertThat(migrationPartnerEntity.getPhones().get(0).getPhone())
            .isEqualTo(migrationCorrespondentCandidate.getCorrPhoneNumber());
        assertThat(migrationPartnerEntity.getPhones().get(0).getPartner()).isNotNull();
        assertThat(migrationPartnerEntity.getEmails().get(0).getPartner()).isNotNull();
    }

    @Test
    void toMigrationAccountEntityTest() {
        MigrationCorrespondentCandidate migrationCorrespondentCandidate = factory.manufacturePojo(MigrationCorrespondentCandidate.class);
        var partnerUuid = factory.manufacturePojo(UUID.class);
        var migrationAccountEntity = mapper.toAccountEntity(DIGITAL_ID, partnerUuid, migrationCorrespondentCandidate);
        var expectedVersion = migrationCorrespondentCandidate.getVersion();
        assertThat(migrationAccountEntity.getVersion()).isEqualTo(expectedVersion);
        assertThat(migrationAccountEntity.getAccount()).isEqualTo(migrationCorrespondentCandidate.getAccount());
        assertThat(migrationAccountEntity.getDigitalId()).isEqualTo(DIGITAL_ID);
        assertThat(migrationAccountEntity.getBank().getBic()).isEqualTo(migrationCorrespondentCandidate.getBic());
        assertThat(migrationAccountEntity.getBank().getName()).isEqualTo(migrationCorrespondentCandidate.getBankName());
        assertThat(migrationAccountEntity.getBank().getIntermediary()).isFalse();
        assertThat(migrationAccountEntity.getBank().getBankAccount().getAccount()).isEqualTo(migrationCorrespondentCandidate.getBankAccount());
        assertThat(migrationAccountEntity.getBank().getAccount()).isNotNull();
        assertThat(migrationAccountEntity.getBank().getBankAccount().getBank()).isNotNull();
    }

    @Test
    void toMigrationPartnerEntityWithoutInnerEntitiesTest() {
        var migrationCorrespondentCandidate = factory.manufacturePojo(MigrationCorrespondentCandidate.class);
        migrationCorrespondentCandidate.setAccount(null);
        migrationCorrespondentCandidate.setBankName(null);
        migrationCorrespondentCandidate.setBic(null);
        migrationCorrespondentCandidate.setBankAccount(null);
        migrationCorrespondentCandidate.setCorrEmail(null);
        migrationCorrespondentCandidate.setCorrPhoneNumber(null);
        var migrationPartnerEntity = mapper.toPartnerEntity(DIGITAL_ID, migrationCorrespondentCandidate);
        assertThat(migrationPartnerEntity.getPhones()).isEmpty();
        assertThat(migrationPartnerEntity.getEmails()).isEmpty();
    }

    @Test
    void toMigrationAccountEntityWithoutInnerEntitiesTest() {
        var partnerUuid = factory.manufacturePojo(UUID.class);
        var migrationCorrespondentCandidate = factory.manufacturePojo(MigrationCorrespondentCandidate.class);
        migrationCorrespondentCandidate.setAccount(null);
        migrationCorrespondentCandidate.setBankName(null);
        migrationCorrespondentCandidate.setBic(null);
        migrationCorrespondentCandidate.setBankAccount(null);
        migrationCorrespondentCandidate.setCorrEmail(null);
        migrationCorrespondentCandidate.setCorrPhoneNumber(null);
        var migrationAccountEntity = mapper.toAccountEntity(DIGITAL_ID, partnerUuid, migrationCorrespondentCandidate);
        assertThat(migrationAccountEntity.getAccount()).isNull();
    }

    @Test
    void toPartnerEntityWithLegalEntityTest() {
        var migrationCorrespondentCandidate = factory.manufacturePojo(MigrationCorrespondentCandidate.class);
        var migrationPartnerEntity = mapper.toPartnerEntity(DIGITAL_ID, migrationCorrespondentCandidate);
        assertThat(migrationPartnerEntity.getOrgName()).
            isEqualTo(migrationCorrespondentCandidate.getName());
        assertThat(migrationPartnerEntity.getFirstName()).isNull();
    }

    @Test
    void toPartnerEntityWithEntrepreneurTest() {
        var migrationCorrespondentCandidate =
            factory.manufacturePojo(MigrationCorrespondentCandidate.class);
        migrationCorrespondentCandidate.setInn("012345678912");
        migrationCorrespondentCandidate.setAccount("40700000000000000001");
        var migrationPartnerEntity = mapper.toPartnerEntity(DIGITAL_ID, migrationCorrespondentCandidate);
        assertThat(migrationPartnerEntity.getOrgName()).isEqualTo(migrationCorrespondentCandidate.getName());
        assertThat(migrationPartnerEntity.getFirstName()).isNull();
    }

    @Test
    void toPartnerEntityWithPhysicalPersonTest() {
        var migrationCorrespondentCandidate =
            factory.manufacturePojo(MigrationCorrespondentCandidate.class);
        migrationCorrespondentCandidate.setInn("012345678912");
        migrationCorrespondentCandidate.setAccount("40800000000000000001");
        var migrationPartnerEntity = mapper.toPartnerEntity(DIGITAL_ID, migrationCorrespondentCandidate);
        assertThat(migrationPartnerEntity.getFirstName()).isEqualTo(migrationCorrespondentCandidate.getName());
        assertThat(migrationPartnerEntity.getOrgName()).isNull();
    }

    @Test
    void toUpdateEmptyMigrationPartnerEntityTest() {
        var migrationCorrespondentCandidate = factory.manufacturePojo(MigrationCorrespondentCandidate.class);
        var migrationPartnerEntity = new PartnerEntity();
        mapper.updatePartnerEntity(DIGITAL_ID, migrationCorrespondentCandidate, migrationPartnerEntity);
        Long expectedVersion = migrationCorrespondentCandidate.getVersion();
        assertThat(migrationPartnerEntity.getVersion()).isEqualTo(expectedVersion);
        assertThat(migrationPartnerEntity.getDigitalId()).isEqualTo(DIGITAL_ID);
        assertThat(migrationPartnerEntity.getEmails().get(0).getEmail())
            .isEqualTo(migrationCorrespondentCandidate.getCorrEmail());
        assertThat(migrationPartnerEntity.getPhones().get(0).getPhone())
            .isEqualTo(migrationCorrespondentCandidate.getCorrPhoneNumber());
        assertThat(migrationPartnerEntity.getPhones().get(0).getPartner()).isNotNull();
        assertThat(migrationPartnerEntity.getEmails().get(0).getPartner()).isNotNull();
    }
}
