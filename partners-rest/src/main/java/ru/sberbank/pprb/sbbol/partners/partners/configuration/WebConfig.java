package ru.sberbank.pprb.sbbol.partners.partners.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.sberbank.pprb.sbbol.partners.model.FraudMetaData;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    ObjectMapper httpHeaderFraudMetaDataObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION);
        return mapper;
    }

    @Bean
    Converter<String, FraudMetaData> headerFraudMetaDataConverter() {
        return new HeaderFraudMetaDataConverter(httpHeaderFraudMetaDataObjectMapper());
    }

    @Bean
    Converter<String, List<UUID>> idsQueryParamConverter() {
        return new UuidListQueryParamConverter();
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        WebMvcConfigurer.super.addFormatters(registry);
        registry.addConverter(headerFraudMetaDataConverter());
        registry.addConverter(idsQueryParamConverter());
    }
}
