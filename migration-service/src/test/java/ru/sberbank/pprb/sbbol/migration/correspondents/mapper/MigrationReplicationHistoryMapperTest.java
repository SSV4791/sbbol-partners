package ru.sberbank.pprb.sbbol.migration.correspondents.mapper;

import io.qameta.allure.AllureId;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.dcbqa.allureee.annotations.layers.UnitTestLayer;
import ru.sberbank.pprb.sbbol.migration.correspondents.entity.MigrationReplicationHistoryEntity;
import ru.sberbank.pprb.sbbol.migration.correspondents.entity.MigrationPartnerEntity;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTestLayer
public class MigrationReplicationHistoryMapperTest {

    private static final PodamFactory factory = new PodamFactoryImpl();
    private static final MigrationReplicationHistoryMapper mapper = Mappers.getMapper(MigrationReplicationHistoryMapper.class);

    @Test
    @AllureId("34032")
    void toReplicationHistoryEntityTest() {
        MigrationPartnerEntity migrationPartnerEntity = factory.manufacturePojo(MigrationPartnerEntity.class);
        MigrationReplicationHistoryEntity replicationHistoryEntity = mapper.toReplicationHistoryEntity(migrationPartnerEntity);
        assertThat(migrationPartnerEntity.getAccount().getUuid()).isEqualTo(replicationHistoryEntity.getAccountUuid());
        assertThat(migrationPartnerEntity.getAccount().getBank().getUuid()).isEqualTo(replicationHistoryEntity.getBankUuid());
        assertThat(migrationPartnerEntity.getAccount().getBank().getBankAccount().getUuid()).isEqualTo(replicationHistoryEntity.getBankAccountUuid());
        assertThat(migrationPartnerEntity.getEmail().getUuid()).isEqualTo(replicationHistoryEntity.getEmailUuid());
        assertThat(migrationPartnerEntity.getPhone().getUuid()).isEqualTo(replicationHistoryEntity.getPhoneUuid());
        assertThat(migrationPartnerEntity.getUuid()).isEqualTo(replicationHistoryEntity.getPartnerUuid());
    }
}
