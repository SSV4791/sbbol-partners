package ru.sberbank.pprb.sbbol.partners.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.sberbank.pprb.sbbol.partners.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.LegacySbbolAdapterImpl;

import java.util.Collections;

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
    RestTemplate restTemplate(@Value("${sbbol.url.root}") String rootLegacyUrl, @Value("${sbbol.url.synapse.system.session}") String synapseSystemSession) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(Collections.singletonList(mappingJacksonHttpMessageConverter()));
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(rootLegacyUrl + synapseSystemSession));
        return restTemplate;
    }

    @Bean
    public LegacySbbolAdapter legacySbbolAdapter(RestTemplate restTemplate) {
        return new LegacySbbolAdapterImpl(restTemplate);
    }
}
