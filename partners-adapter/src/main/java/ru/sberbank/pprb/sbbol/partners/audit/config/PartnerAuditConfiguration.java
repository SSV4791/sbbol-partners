package ru.sberbank.pprb.sbbol.partners.audit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.client.RestTemplate;
import ru.sberbank.pprb.sbbol.audit.api.DefaultApi;
import ru.sberbank.pprb.sbbol.audit.invoker.ApiClient;
import ru.sberbank.pprb.sbbol.partners.audit.AuditAdapter;
import ru.sberbank.pprb.sbbol.partners.audit.AuditAdapterImpl;
import ru.sberbank.pprb.sbbol.partners.audit.mapper.AuditMapper;
import ru.sberbank.pprb.sbbol.partners.audit.mapper.AuditMapperImpl;

import java.time.Duration;

@Configuration
public class PartnerAuditConfiguration {

    @Bean
    RestTemplate auditRestTemplate(
        @Value("${audit.time_out}") long timeOut,
        RestTemplateBuilder restTemplateBuilder
    ) {
        return restTemplateBuilder
            .setConnectTimeout(Duration.ofMillis(timeOut))
            .setReadTimeout(Duration.ofMillis(timeOut))
            .build();
    }

    @Bean
    ApiClient auditApiClient(
        @Value("${audit.url}") String baseUrl,
        RestTemplate auditRestTemplate
    ) {
        var client = new ApiClient(auditRestTemplate);
        client.setBasePath("http://" + baseUrl);
        return client;
    }

    @Bean
    DefaultApi auditApi(ApiClient auditApiClient) {
        return new DefaultApi(auditApiClient);
    }

    @Bean
    AuditMapper auditMapper() {
        return new AuditMapperImpl();
    }

    @Bean
    AuditAdapter auditAdapter(
        @Value("${audit.enabled}") boolean auditEnabled,
        @Value("classpath:/audit/auditMetamodel.json") Resource metaModel,
        @Value("${audit.x-node-id}") String defaultXNodeId,
        DefaultApi auditApi
    ) {
        return new AuditAdapterImpl(auditEnabled, metaModel, defaultXNodeId, auditApi, auditMapper());
    }
}
