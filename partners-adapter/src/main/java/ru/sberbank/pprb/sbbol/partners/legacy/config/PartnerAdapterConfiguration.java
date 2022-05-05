package ru.sberbank.pprb.sbbol.partners.legacy.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapterImpl;

@Configuration
public class PartnerAdapterConfiguration {

    @Bean
    ObjectMapper objectMapper() {
        return JsonMapper.builder()
            .configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build();
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
        RestTemplateBuilder restTemplateBuilder
    ) {
        return restTemplateBuilder
            .messageConverters(mappingJacksonHttpMessageConverter())
            .uriTemplateHandler(new DefaultUriBuilderFactory("http://" + rootLegacyUrl + synapseSystemSession))
            .build();
    }

    @Bean
    public LegacySbbolAdapter legacySbbolAdapter(RestTemplate restTemplate) {
        return new LegacySbbolAdapterImpl(restTemplate);
    }
}
