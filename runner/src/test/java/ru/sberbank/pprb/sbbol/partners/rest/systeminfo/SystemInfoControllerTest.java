package ru.sberbank.pprb.sbbol.partners.rest.systeminfo;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.service.systeminfo.SystemInfo;
import ru.sbrf.journal.standin.ResourceNotAllowedException;
import ru.sbrf.journal.standin.StandinResourceHelper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class SystemInfoControllerTest extends AbstractIntegrationTest {
    private static final String URL = "/system-info";

    @MockBean
    private StandinResourceHelper<String> standinResourceHelper;

    @Test
    void dateBaseModTest() {
        Allure.step("Подготовка тестовых условий", () -> {
            when(standinResourceHelper.getResource()).thenReturn("main");
        });

        var response = Allure.step("Выполнение get-запроса /system-info, код ответа 200", () -> get(
            URL,
            HttpStatus.OK,
            SystemInfo.class
        ));

        Allure.step("Проверка полученного результата", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getDataSourceMode())
                .isEqualTo(standinResourceHelper.getResource());
        });
    }

    @Test
    void dateBaseStopModTest() {
        Allure.step("Подготовка тестовых условий", () -> {
            when(standinResourceHelper.getResource()).thenThrow(ResourceNotAllowedException.class);
        });

        var response = Allure.step("Выполнение get-запроса /system-info, код ответа 200", () -> get(
            URL,
            HttpStatus.OK,
            SystemInfo.class
        ));

        Allure.step("Проверка полученного результата", () -> {
            assertThat(response)
                .isNotNull();
            assertThat(response.getDataSourceMode())
                .isEqualTo("stop");
        });
    }
}
