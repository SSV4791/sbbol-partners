package ru.sberbank.pprb.sbbol.partners.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

    @Bean
    GenericFilterBean genericFilterBean() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(
                @NotNull HttpServletRequest request,
                @NotNull HttpServletResponse response,
                @NotNull FilterChain filterChain
            ) throws ServletException, IOException {
                MDC.put("requestUid", request.getHeader("x-request-id"));
                MDC.put("ufsTraceId", request.getHeader("ufs-trace-parent-id"));
                MDC.put("ufsSessionId", request.getHeader("ufs_forward_sid"));
                MDC.put("ufsSubsystemCode", request.getHeader("ufs-initiatingsubsystemcode"));
                MDC.put("pod", request.getHeader("pod"));
                try {
                    filterChain.doFilter(request, response);
                } finally {
                    MDC.remove("requestUid");
                    MDC.remove("ufsTraceId");
                    MDC.remove("ufsSessionId");
                    MDC.remove("ufsSubsystemCode");
                    MDC.remove("pod");
                }
            }
        };
    }
}
