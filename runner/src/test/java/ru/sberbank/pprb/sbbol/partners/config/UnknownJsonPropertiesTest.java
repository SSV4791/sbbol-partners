package ru.sberbank.pprb.sbbol.partners.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.dcbqa.allureee.annotations.layers.ConfigurationTestLayer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ConfigurationTestLayer
class UnknownJsonPropertiesTest extends AbstractIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @AllureId("34160")
    @DisplayName("Тест для проверки конфигурации ObjectMapper'а в контексте Spring")
    void testUnknownProperty() throws JsonProcessingException {
        var jsonContent = """
              {
                  "field": "expected",
                  "unknownField": "unexpected"
              }
            """;
        MyClass value = objectMapper.readValue(jsonContent, MyClass.class);
        assertNotNull(value);
        assertEquals("expected", value.getField());
    }

    private static class MyClass {
        private String field;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }
    }
}
