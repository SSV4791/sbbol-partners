package ru.sberbank.pprb.sbbol.partners.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.entity.partner.IdsHistoryEntity;
import ru.sberbank.pprb.sbbol.partners.repository.partner.GuidsHistoryRepository;
import ru.sberbank.pprb.sbbol.partners.service.ids.history.IdsHistoryService;

import java.util.List;
import java.util.UUID;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

public class IdsHistoryServiceTest extends AbstractIntegrationTest {

    @Autowired
    private IdsHistoryService idsHistoryService;

    @Autowired
    private GuidsHistoryRepository repository;

    @Test
    void addTest() {
        var digitalId = step("Подготовка тестовых данных. Создание digitalId", () -> podamFactory.manufacturePojo(String.class));
        var externalId = step("Подготовка тестовых данных. Создание externalId", UUID::randomUUID);
        var pprbId = step("Подготовка тестовых данных. Создание pprbId", UUID::randomUUID);
        var expected = step("Подготовка объекта для сравнения", () -> {
            var expectedIdHistory = new IdsHistoryEntity();
            expectedIdHistory.setDigitalId(digitalId);
            expectedIdHistory.setExternalId(externalId);
            expectedIdHistory.setPprbEntityId(pprbId);
            return expectedIdHistory;
        });
        step("Выполнение запроса. Сохранение сущности", () -> idsHistoryService.create(digitalId, externalId, pprbId));

        var ids = step("Получение сохраненной сущности из БД",
            () -> repository.findByDigitalIdAndPprbEntityId(digitalId, pprbId));

        step("Проверка полученного результата", () -> {
            assertThat(ids)
                .isNotNull()
                .hasSize(1);

            var idHistory = ids.get(0);
            assertThat(idHistory)
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("uuid", "version", "lastModifiedDate")
                .isEqualTo(expected);
        });
    }

    @Test
    void deleteTest() {
        var digitalId = step("Подготовка тестовых данных. Создание digitalId", () -> podamFactory.manufacturePojo(String.class));
        var externalId = step("Подготовка тестовых данных. Создание externalId", UUID::randomUUID);
        var pprbId = step("Подготовка тестовых данных. Создание pprbId", UUID::randomUUID);

        step("Выполнение запроса. Сохранение сущности", () -> idsHistoryService.create(digitalId, externalId, pprbId));
        step("Удаление сущности", () -> idsHistoryService.delete(digitalId, pprbId));

        var idsAfterDelete = step("Получение сохраненной сущности из БД",
            () -> repository.findByDigitalIdAndPprbEntityId(digitalId, pprbId));

        step("Проверка полученного результата", () -> {
            assertThat(idsAfterDelete)
                .isNotNull()
                .hasSize(0);
        });
    }

    @Test
    void deleteMassiveTest() {
        var digitalId = step("Подготовка тестовых данных. Создание digitalId", () -> podamFactory.manufacturePojo(String.class));
        var externalId = step("Подготовка тестовых данных. Создание externalId", UUID::randomUUID);
        var externalId2 = step("Подготовка тестовых данных. Создание externalId2", UUID::randomUUID);
        var pprbId = step("Подготовка тестовых данных. Создание pprbId", UUID::randomUUID);
        var pprbId2 = step("Подготовка тестовых данных. Создание pprbId2", UUID::randomUUID);

        step("Выполнение запроса. Сохранение сущности", () -> idsHistoryService.create(digitalId, externalId, pprbId));
        step("Выполнение запроса. Сохранение сущности2", () -> idsHistoryService.create(digitalId, externalId2, pprbId2));
        step("Удаление сущности", () -> idsHistoryService.delete(digitalId, List.of(pprbId, pprbId2)));

        var idsAfterDelete = step("Получение сохраненной сущности из БД",
            () -> repository.findByDigitalIdAndPprbEntityId(digitalId, pprbId));

        var idsAfterDelete2 = step("Получение сохраненной сущности из БД",
            () -> repository.findByDigitalIdAndPprbEntityId(digitalId, pprbId2));

        step("Проверка полученного результата", () -> {
            assertThat(idsAfterDelete)
                .isNotNull()
                .hasSize(0);
            assertThat(idsAfterDelete2)
                .isNotNull()
                .hasSize(0);
        });
    }
}
