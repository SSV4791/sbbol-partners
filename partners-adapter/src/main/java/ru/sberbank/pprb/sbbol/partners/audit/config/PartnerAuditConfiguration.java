package ru.sberbank.pprb.sbbol.partners.audit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.sberbank.pprb.sbbol.audit.api.DefaultApi;
import ru.sberbank.pprb.sbbol.audit.invoker.ApiClient;
import ru.sberbank.pprb.sbbol.partners.audit.AuditAdapter;
import ru.sberbank.pprb.sbbol.partners.audit.AuditAdapterImpl;
import ru.sberbank.pprb.sbbol.partners.audit.mapper.AuditMapper;
import ru.sberbank.pprb.sbbol.partners.audit.mapper.AuditMapperImpl;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class PartnerAuditConfiguration {

    @Bean
    RestTemplate auditRestTemplate(
        @Value("${audit.read_time_out:5000}") long readTimeOut,
        @Value("${audit.connection_time_out:1000}") long connectionTimeOut,
        RestTemplateBuilder restTemplateBuilder
    ) {
        return restTemplateBuilder
            .setReadTimeout(Duration.ofMillis(readTimeOut))
            .setConnectTimeout(Duration.ofMillis(connectionTimeOut))
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
        DefaultApi auditApi,
        RetryTemplate auditPublishEventRetryTemplate,
        ExecutorService auditPublishExecutorService

    ) {
        return new AuditAdapterImpl(
            auditEnabled,
            metaModel,
            defaultXNodeId,
            auditApi,
            auditMapper(),
            auditPublishEventRetryTemplate,
            auditPublishExecutorService
        );
    }

    @Bean
    public RetryTemplate auditPublishEventRetryTemplate(
        @Value("${audit.event.publish.retry.max_attempts:10}") Integer maxAttempts,
        @Value("${audit.event.publish.retry.interval:5000}") Long retryTimeInterval
    ) {
        return RetryTemplate.builder()
            .notRetryOn(HttpClientErrorException.BadRequest.class)
            .traversingCauses()
            .maxAttempts(maxAttempts)
            .fixedBackoff(retryTimeInterval)
            .build();
    }

    @Bean
    public ExecutorService auditPublishExecutorService(
        @Value("${audit.event.publish.executor.threads:2}") Integer threads
    ) {
        return Executors.newFixedThreadPool(threads);
    }
}
