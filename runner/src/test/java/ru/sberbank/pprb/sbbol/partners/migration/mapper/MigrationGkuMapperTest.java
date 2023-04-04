package ru.sberbank.pprb.sbbol.partners.migration.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.sberbank.pprb.sbbol.migration.gku.mapper.MigrationGkuMapper;
import ru.sberbank.pprb.sbbol.migration.gku.model.MigrationGkuCandidate;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.entity.partner.GkuInnEntity;

import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

class MigrationGkuMapperTest extends BaseUnitConfiguration {

    private static final MigrationGkuMapper mapper = Mappers.getMapper(MigrationGkuMapper.class);

    @Test
    @DisplayName("Проверка мапинга MigrationGkuCandidate -> GkuInnEntity")
    void toMigrationGkuEntityTest() {
        @SuppressWarnings("unchecked")
        List<MigrationGkuCandidate> migrationGkuCandidate =
            step("Подготовка тестовых данных", () -> factory.manufacturePojo(List.class, MigrationGkuCandidate.class));
        List<GkuInnEntity> migrationGkuInnEntities =
            step("Выполнение мапинга MigrationGkuCandidate -> GkuInnEntity", () -> mapper.toDictionary(migrationGkuCandidate));
        step("Проверка корректности ответа", () -> {
            for (GkuInnEntity migrationGkuInnEntity : migrationGkuInnEntities) {
                assertThat(migrationGkuInnEntities)
                    .contains(migrationGkuInnEntity);
            }
        });
    }
}
