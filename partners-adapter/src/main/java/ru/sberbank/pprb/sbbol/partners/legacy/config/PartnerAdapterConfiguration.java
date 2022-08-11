package ru.sberbank.pprb.sbbol.partners.legacy.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
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
        @Value("${sbbol.url.synapse.system.session}") String synapseSystemSession,
        @Value("${sbbol.time_out:5000}") long timeOut,
        RestTemplateBuilder restTemplateBuilder
    ) {
        return restTemplateBuilder
            .setReadTimeout(Duration.ofMillis(timeOut))
            .setConnectTimeout(Duration.ofMillis(timeOut))
            .messageConverters(mappingJacksonHttpMessageConverter())
            .uriTemplateHandler(
                new DefaultUriBuilderFactory("http://" + rootLegacyUrl + synapseSystemSession)
            )
            .build();
    }

    @Bean
    public LegacySbbolAdapter legacySbbolAdapter(RestTemplate restTemplate) {
        return new LegacySbbolAdapterImpl(restTemplate);
    }
}
