package ru.sberbank.pprb.sbbol.partners.adapter.audit.config;

import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.sberbank.pprb.sbbol.audit.api.DefaultApi;
import ru.sberbank.pprb.sbbol.audit.invoker.ApiClient;
import ru.sberbank.pprb.sbbol.audit.model.BaseResponse;
import ru.sberbank.pprb.sbbol.partners.audit.AuditAdapter;
import ru.sberbank.pprb.sbbol.partners.audit.AuditAdapterImpl;
import ru.sberbank.pprb.sbbol.partners.audit.mapper.AuditMapper;
import ru.sberbank.pprb.sbbol.partners.audit.mapper.AuditMapperImpl;

import javax.annotation.PostConstruct;

import static org.mockito.Mockito.when;


@TestConfiguration
public class AuditAdapterConfiguration {

    @MockBean(name = "auditRestTemplate")
    RestTemplate auditRestTemplate;

    @PostConstruct
    void initMock() {
        when(auditRestTemplate.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());
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
    AuditAdapter auditAdapter(
        @Value("classpath:/audit/auditMetamodelTest.json") Resource metaModel
    ) {
        return new AuditAdapterImpl(true, metaModel, "local", auditApi(), auditMapper());
    }
}
