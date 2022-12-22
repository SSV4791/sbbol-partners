package ru.sberbank.pprb.sbbol.partners.fraud.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.googlecode.jsonrpc4j.ProxyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sberbank.pprb.sbbol.antifraud.rpc.counterparty.CounterPartyService;
import ru.sberbank.pprb.sbbol.partners.fraud.FraudAdapter;
import ru.sberbank.pprb.sbbol.partners.fraud.FraudAdapterImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;

@Configuration
@EnableConfigurationProperties(FraudProperties.class)
public class FraudAdapterConfiguration {

    private final FraudProperties fraudProperties;

    public FraudAdapterConfiguration(FraudProperties fraudProperties) {
        this.fraudProperties = fraudProperties;
    }

    @Bean
    ObjectMapper fraudObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION);
        return mapper;
    }

    @ConditionalOnProperty(prefix = "fraud", name = "enabled")
    @Bean
    JsonRpcHttpClient fraudJsonRpcHttpClient() throws MalformedURLException {
        URL fraudURL = new URL("http://" + fraudProperties.getUrl() + fraudProperties.getEndpoint());
        return new JsonRpcHttpClient(fraudObjectMapper(), fraudURL,  new HashMap<>());
    }

    @ConditionalOnProperty(prefix = "fraud", name = "enabled")
    @Bean
    CounterPartyService fraudRpcProxy(JsonRpcHttpClient fraudRpcClient) {
        return ProxyUtil.createClientProxy(
            fraudRpcClient.getClass().getClassLoader(),
            CounterPartyService.class,
            fraudRpcClient);
    }

    @Bean
    FraudAdapter fraudAdapter(@Autowired(required = false)  CounterPartyService fraudRpcProxy) {
        return new FraudAdapterImpl(fraudRpcProxy);
    }
}
