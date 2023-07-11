package ru.sberbank.pprb.sbbol.partners.service.mapper.partner;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.entity.partner.IdsHistoryEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.IdsHistoryMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.IdsHistoryMapperImpl;

import java.util.UUID;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(
    classes = IdsHistoryMapperImpl.class
)
public class IdsHistoryMapperTest extends BaseUnitConfiguration {

    @Autowired
    private IdsHistoryMapper mapper;

    @Test
    void accountToIdsHistoryEntityTest() {
        var digitalId = step("Подготовка тестовых данных. Создание digitalId", () -> RandomStringUtils.random(10));
        var externalId = step("Подготовка тестовых данных. Создание externalId", UUID::randomUUID);
        var pprbId = step("Подготовка тестовых данных. Создание pprbId", UUID::randomUUID);
        var expected = step("Подготовка тестовых данных. Создание IdsHistoryEntity", () -> {
            var idsHistoryEntity = new IdsHistoryEntity();
            idsHistoryEntity.setDigitalId(digitalId);
            idsHistoryEntity.setExternalId(externalId);
            idsHistoryEntity.setPprbEntityId(pprbId);
            return idsHistoryEntity;
        });
        var actual = step("Выполнение маппинга", () -> mapper.toIdsHistoryEntity(digitalId, externalId, pprbId));

        step("Проверка результата", () ->
            assertThat(actual)
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("uuid", "lastModifiedDate")
                .isEqualTo(expected)
        );
    }
}
