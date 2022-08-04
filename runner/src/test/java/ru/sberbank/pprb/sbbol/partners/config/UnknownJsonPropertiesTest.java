package ru.sberbank.pprb.sbbol.partners.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.dcbqa.allureee.annotations.layers.ConfigurationTestLayer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@ContextConfiguration(classes =
    {
        PodamConfiguration.class,
        TestReplicationConfiguration.class
    },
    initializers = {
        HibernatePluginCleanerInitializer.class
    }
)
@ExtendWith({SpringExtension.class})
@ConfigurationTestLayer
class UnknownJsonPropertiesTest {

    @Autowired
    private ObjectMapper objectMapper;


    @Test
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
