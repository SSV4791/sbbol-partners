package ru.sberbank.pprb.sbbol.partners.legacy.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactorySupplier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapterImpl;

import java.text.SimpleDateFormat;
import java.time.Duration;

@Configuration
public class PartnerAdapterConfiguration {

    @Bean
    ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION);
        return mapper;
    }

    @Bean
    MappingJackson2HttpMessageConverter mappingJacksonHttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper());
        return converter;
    }

    @Bean
    RestTemplate restTemplate(
        @Value("${sbbol.url}") String rootLegacyUrl,
        @Value("${sbbol.read_time_out:5000}") long readTimeOut,
        @Value("${sbbol.connection_time_out:1000}") long connectionTimeOut,
        RestTemplateBuilder restTemplateBuilder
    ) {
        return restTemplateBuilder
            .setReadTimeout(Duration.ofMillis(readTimeOut))
            .setConnectTimeout(Duration.ofMillis(connectionTimeOut))
            .messageConverters(mappingJacksonHttpMessageConverter())
            .requestFactory(new ClientHttpRequestFactorySupplier())
            .uriTemplateHandler(
                new DefaultUriBuilderFactory("http://" + rootLegacyUrl)
            )
            .build();
    }

    @Bean
    public RetryTemplate sbbolLegacyRetryTemplate(
        @Value("${sbbol.retry.max_attempts:3}") Integer maxAttempts,
        @Value("${sbbol.retry.interval:5000}") Long retryTimeInterval
    ) {
        return RetryTemplate.builder()
            .notRetryOn(HttpClientErrorException.BadRequest.class)
            .traversingCauses()
            .maxAttempts(maxAttempts)
            .fixedBackoff(retryTimeInterval)
            .build();
    }

    @Bean
    public LegacySbbolAdapter legacySbbolAdapter(RestTemplate restTemplate, RetryTemplate sbbolLegacyRetryTemplate) {
        return new LegacySbbolAdapterImpl(restTemplate, sbbolLegacyRetryTemplate);
    }
}
