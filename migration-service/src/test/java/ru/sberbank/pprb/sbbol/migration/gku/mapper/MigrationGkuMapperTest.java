package ru.sberbank.pprb.sbbol.migration.gku.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.migration.gku.entity.MigrationGkuInnEntity;
import ru.sberbank.pprb.sbbol.migration.gku.model.MigrationGkuCandidate;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MigrationGkuMapperTest {

    private static final PodamFactory factory = new PodamFactoryImpl();
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
