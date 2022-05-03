package ru.sberbank.pprb.sbbol.partners.audit.adapter;

import io.qameta.allure.AllureId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;
import ru.dcbqa.allureee.annotations.layers.UnitTestLayer;
import ru.sberbank.pprb.sbbol.audit.api.DefaultApi;
import ru.sberbank.pprb.sbbol.audit.invoker.ApiClient;
import ru.sberbank.pprb.sbbol.audit.model.BaseResponse;
import ru.sberbank.pprb.sbbol.partners.audit.AuditAdapter;
import ru.sberbank.pprb.sbbol.partners.audit.AuditAdapterImpl;
import ru.sberbank.pprb.sbbol.partners.audit.mapper.AuditMapper;
import ru.sberbank.pprb.sbbol.partners.audit.mapper.AuditMapperImpl;
import ru.sberbank.pprb.sbbol.partners.audit.model.Event;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import javax.annotation.PostConstruct;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;


@UnitTestLayer
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
    AuditAdapterTest.AdapterConfigurationTest.class
})
@TestPropertySource(value = "classpath:audit/auditMetamodel.json")
class AuditAdapterTest {

    public static final PodamFactory factory = new PodamFactoryImpl();

    @Autowired
    private AuditAdapter adapter;

    /**
     * {@link ru.sberbank.pprb.sbbol.partners.audit.AuditAdapter#sand(Event)}
     */
    @Test
    @AllureId("34036")
    @DisplayName("Адаптер Аудит. Проверка отправки Event")
    void checkMigrationTest() {
        var event = factory.manufacturePojo(Event.class);
        assertDoesNotThrow(() -> adapter.sand(event));
    }


    @TestConfiguration
    static class AdapterConfigurationTest {

        @MockBean(name = "auditRestTemplate")
        RestTemplate auditRestTemplate;

        @PostConstruct
        public void initMock() {
            when(auditRestTemplate.exchange(ArgumentMatchers.<RequestEntity<Object>>any(), ArgumentMatchers.<ParameterizedTypeReference<BaseResponse>>any()))
                .thenReturn(new ResponseEntity<>(new BaseResponse(), HttpStatus.CREATED));
        }

        @Bean
        ApiClient auditApiClient() {
            return new ApiClient(auditRestTemplate);
        }

        @Bean
        DefaultApi auditApi() {
            return new DefaultApi(auditApiClient());
        }

        @Bean
        AuditMapper auditMapper() {
            return new AuditMapperImpl();
        }

        @Bean
        AuditAdapter auditAdapter() {
            return new AuditAdapterImpl(true, "local", auditApi(), auditMapper());
        }
    }
}
