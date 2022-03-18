package ru.sberbank.pprb.sbbol.migration.correspondents.mapper;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.migration.correspondents.enums.MigrationLegalType;
import ru.sberbank.pprb.sbbol.migration.correspondents.entity.MigrationPartnerEntity;
import ru.sberbank.pprb.sbbol.migration.correspondents.model.MigrationCorrespondentCandidate;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import static org.assertj.core.api.Assertions.assertThat;

public class MigrationPartnerMapperTest {

    private static final PodamFactory factory = new PodamFactoryImpl();
    private static final MigrationPartnerMapper mapper = Mappers.getMapper(MigrationPartnerMapper.class);
    private static final String DIGITAL_ID = RandomStringUtils.random(20);

    @Test
    void toMigrationPartnerEntityTest() {
        MigrationCorrespondentCandidate migrationCorrespondentCandidate = factory.manufacturePojo(MigrationCorrespondentCandidate.class);
        MigrationPartnerEntity migrationPartnerEntity = mapper.toMigrationPartnerEntity(DIGITAL_ID, migrationCorrespondentCandidate);
        Long expectedVersion = migrationCorrespondentCandidate.getVersion();
        assertThat(migrationPartnerEntity.getVersion()).isEqualTo(expectedVersion);
        assertThat(migrationPartnerEntity.getDigitalId()).isEqualTo(DIGITAL_ID);
        assertThat(migrationPartnerEntity.getAccount().getVersion()).isEqualTo(expectedVersion);
        assertThat(migrationPartnerEntity.getAccount().getAccount()).isEqualTo(migrationCorrespondentCandidate.getAccount());
        assertThat(migrationPartnerEntity.getAccount().getDigitalId()).isEqualTo(DIGITAL_ID);
        assertThat(migrationPartnerEntity.getAccount().getBank().getVersion()).isEqualTo(expectedVersion);
        assertThat(migrationPartnerEntity.getAccount().getBank().getBic()).isEqualTo(migrationCorrespondentCandidate.getBic());
        assertThat(migrationPartnerEntity.getAccount().getBank().getIntermediary()).isFalse();
        assertThat(migrationPartnerEntity.getAccount().getBank().getBankAccount().getAccount()).isEqualTo(migrationCorrespondentCandidate.getBankAccount());
        assertThat(migrationPartnerEntity.getEmail().getVersion()).isEqualTo(expectedVersion);
        assertThat(migrationPartnerEntity.getEmail().getEmail()).isEqualTo(migrationCorrespondentCandidate.getCorrEmail());
        assertThat(migrationPartnerEntity.getPhone().getVersion()).isEqualTo(expectedVersion);
        assertThat(migrationPartnerEntity.getPhone().getPhone()).isEqualTo(migrationCorrespondentCandidate.getCorrPhoneNumber());
        assertThat(migrationPartnerEntity.getLegalType()).isEqualTo(migrationCorrespondentCandidate.getLegalType());
        assertThat(migrationPartnerEntity.getPhone().getPartner()).isNotNull();
        assertThat(migrationPartnerEntity.getEmail().getPartner()).isNotNull();
        assertThat(migrationPartnerEntity.getAccount().getPartner()).isNotNull();
        assertThat(migrationPartnerEntity.getAccount().getBank().getAccount()).isNotNull();
        assertThat(migrationPartnerEntity.getAccount().getBank().getBankAccount().getBank()).isNotNull();
    }

    @Test
    void toMigrationPartnerEntityWithoutInnerEntitiesTest() {
        MigrationCorrespondentCandidate migrationCorrespondentCandidate = factory.manufacturePojo(MigrationCorrespondentCandidate.class);
        migrationCorrespondentCandidate.setAccount(null);
        migrationCorrespondentCandidate.setCorrEmail(null);
        migrationCorrespondentCandidate.setCorrPhoneNumber(null);
        MigrationPartnerEntity migrationPartnerEntity = mapper.toMigrationPartnerEntity(DIGITAL_ID, migrationCorrespondentCandidate);
        assertThat(migrationPartnerEntity.getPhone()).isNull();
        assertThat(migrationPartnerEntity.getEmail()).isNull();
        assertThat(migrationPartnerEntity.getAccount()).isNull();
    }

    @Test
    void toPartnerEntityWithLegalEntityTest() {
        MigrationCorrespondentCandidate migrationCorrespondentCandidate = factory.manufacturePojo(MigrationCorrespondentCandidate.class);
        migrationCorrespondentCandidate.setLegalType(MigrationLegalType.LEGAL_ENTITY);
        MigrationPartnerEntity migrationPartnerEntity = mapper.toMigrationPartnerEntity(DIGITAL_ID, migrationCorrespondentCandidate);
        assertThat(migrationPartnerEntity.getOrgName()).isEqualTo(migrationCorrespondentCandidate.getName());
        assertThat(migrationPartnerEntity.getFirstName()).isNull();
    }

    @Test
    void toPartnerEntityWithEntrepreneurTest() {
        MigrationCorrespondentCandidate migrationCorrespondentCandidate = factory.manufacturePojo(MigrationCorrespondentCandidate.class);
        migrationCorrespondentCandidate.setLegalType(MigrationLegalType.ENTREPRENEUR);
        MigrationPartnerEntity migrationPartnerEntity = mapper.toMigrationPartnerEntity(DIGITAL_ID, migrationCorrespondentCandidate);
        assertThat(migrationPartnerEntity.getOrgName()).isEqualTo(migrationCorrespondentCandidate.getName());
        assertThat(migrationPartnerEntity.getFirstName()).isNull();
    }

    @Test
    void toPartnerEntityWithPhysicalPersonTest() {
        MigrationCorrespondentCandidate migrationCorrespondentCandidate = factory.manufacturePojo(MigrationCorrespondentCandidate.class);
        migrationCorrespondentCandidate.setLegalType(MigrationLegalType.PHYSICAL_PERSON);
        MigrationPartnerEntity migrationPartnerEntity = mapper.toMigrationPartnerEntity(DIGITAL_ID, migrationCorrespondentCandidate);
        assertThat(migrationPartnerEntity.getFirstName()).isEqualTo(migrationCorrespondentCandidate.getName());
        assertThat(migrationPartnerEntity.getOrgName()).isNull();
    }
}
