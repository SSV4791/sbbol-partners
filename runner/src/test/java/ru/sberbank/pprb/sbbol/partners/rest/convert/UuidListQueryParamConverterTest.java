package ru.sberbank.pprb.sbbol.partners.rest.convert;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import ru.sberbank.pprb.sbbol.partners.config.BaseUnitConfiguration;
import ru.sberbank.pprb.sbbol.partners.partners.configuration.UuidListQueryParamConverter;

import java.util.List;
import java.util.UUID;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = UuidListQueryParamConverter.class)
class UuidListQueryParamConverterTest extends BaseUnitConfiguration {

    @Autowired
    private UuidListQueryParamConverter converter;

    @Test
    @DisplayName("Конвертация строки UUIDs в List<UUID>")
    void convertTest() {
        var id1 = step("Подготовка тестовых данных. Создание первого id", UUID::randomUUID);
        var id2 = step("Подготовка тестовых данных. Создание второго id", UUID::randomUUID);
        var id3 = step("Подготовка тестовых данных. Создание третьего id", UUID::randomUUID);

        var uuidsString = step("Подготовка тестовых данных. Создание строки из списка Uuids", () -> {
            var ids = List.of(id1, id2, id3);
            return ids.toString();
        });

        var convertedIds = step("Конвертация строки в список UUID", () -> converter.convert(uuidsString));

        step("Проверка результата", () ->
            assertThat(convertedIds)
            .isNotNull()
            .hasSize(3)
            .contains(id1, id2, id3));
    }
}
