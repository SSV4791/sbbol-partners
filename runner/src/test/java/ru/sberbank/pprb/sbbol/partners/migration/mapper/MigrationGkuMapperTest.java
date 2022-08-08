package ru.sberbank.pprb.sbbol.partners.migration.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.migration.gku.entity.MigrationGkuInnEntity;
import ru.sberbank.pprb.sbbol.migration.gku.mapper.MigrationGkuMapper;
import ru.sberbank.pprb.sbbol.migration.gku.model.MigrationGkuCandidate;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MigrationGkuMapperTest extends BaseUnitConfiguration {

    private static final MigrationGkuMapper mapper = Mappers.getMapper(MigrationGkuMapper.class);

    @Test
    void toMigrationGkuEntityTest() {
        @SuppressWarnings("unchecked")
        List<MigrationGkuCandidate> migrationGkuCandidate = factory.manufacturePojo(List.class, MigrationGkuCandidate.class);
        List<MigrationGkuInnEntity> migrationGkuInnEntities = mapper.toDictionary(migrationGkuCandidate);

        for (MigrationGkuInnEntity migrationGkuInnEntity : migrationGkuInnEntities) {
            assertThat(migrationGkuInnEntities)
                .contains(migrationGkuInnEntity);
        }
    }
}
