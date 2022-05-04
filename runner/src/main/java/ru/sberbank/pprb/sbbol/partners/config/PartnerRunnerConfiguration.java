package ru.sberbank.pprb.sbbol.partners.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;


import static org.springframework.http.converter.json.Jackson2ObjectMapperBuilder.json;

@Configuration
public class PartnerRunnerConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilder builder() {
        Jackson2ObjectMapperBuilder builder = json();
        builder.modules(new JavaTimeModule());
        builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        builder.featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        builder.featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        builder.featuresToDisable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        builder.serializationInclusion(JsonInclude.Include.NON_NULL);
        return builder;
    }

    @Bean
    public MappingJackson2HttpMessageConverter jsonMessageConverter() {
        var converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(builder().build());
        return converter;
    }

    @Bean
    public HttpMessageConverters customConverters() {
        return new HttpMessageConverters(jsonMessageConverter());
    }
}
