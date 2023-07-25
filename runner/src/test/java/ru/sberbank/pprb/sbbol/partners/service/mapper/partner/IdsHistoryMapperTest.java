package ru.sberbank.pprb.sbbol.partners.service.mapper.partner;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.entity.partner.GuidsHistoryEntity;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.IdsHistoryMapper;
import ru.sberbank.pprb.sbbol.partners.mapper.partner.IdsHistoryMapperImpl;
import ru.sberbank.pprb.sbbol.partners.model.ExternalInternalIdLink;
import ru.sberbank.pprb.sbbol.partners.model.ExternalInternalIdLinksResponse;

import java.util.List;
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
            var idsHistoryEntity = new GuidsHistoryEntity();
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

    @Test
    void toAccountIdsByExternalIdsResponse() {
        var externalIds = step("Подготовка тестовых данных. Создание externalIds", () ->
            List.of(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
                ));
        var idsHistoryEntities = step("Подготовка тестовых данных. Создание idsHistoryEntities", () -> {
            var idsHistoryEntity1 = factory.manufacturePojo(GuidsHistoryEntity.class);
            idsHistoryEntity1.setExternalId(UUID.fromString(externalIds.get(0)));
            var idsHistoryEntity2 = factory.manufacturePojo(GuidsHistoryEntity.class);
            return List.of(idsHistoryEntity1, idsHistoryEntity2);
        });
        var expected = step("Подготовка тестовых данных. Создание IdsHistoryEntity", () ->
            new ExternalInternalIdLinksResponse()
                .addIdLinksItem(
                    new ExternalInternalIdLink()
                        .externalId(externalIds.get(0))
                        .internalId(idsHistoryEntities.get(0).getPprbEntityId().toString())
                )
                .addIdLinksItem(
                    new ExternalInternalIdLink()
                        .externalId(externalIds.get(1))
                ));
        var actual = step("Выполнение маппинга", () -> mapper.toAccountIdsByExternalIdsResponse(externalIds, idsHistoryEntities));
        step("Проверка результата", () ->
            assertThat(actual)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(expected)
        );
    }
}
