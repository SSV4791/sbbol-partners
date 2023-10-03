package ru.sberbank.pprb.sbbol.partners.fraud.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.googlecode.jsonrpc4j.ProxyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sberbank.pprb.sbbol.antifraud.rpc.document.DocumentWithOutSavingService;
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
    JsonRpcHttpClient fraudJsonRpcHttpClient(
        @Value("${fraud.read_time_out:5000}") int readTimeOut,
        @Value("${fraud.connection_time_out:1000}") int connectionTimeOut
    ) throws MalformedURLException {
        URL fraudURL = new URL("http://" + fraudProperties.getUrl() + fraudProperties.getEndpoint());
        JsonRpcHttpClient client = new JsonRpcHttpClient(fraudObjectMapper(), fraudURL, new HashMap<>());
        client.setReadTimeoutMillis(readTimeOut);
        client.setConnectionTimeoutMillis(connectionTimeOut);
        return client;
    }

    @ConditionalOnProperty(prefix = "fraud", name = "enabled")
    @Bean
    DocumentWithOutSavingService fraudRpcProxy(JsonRpcHttpClient fraudRpcClient) {
        return ProxyUtil.createClientProxy(
            fraudRpcClient.getClass().getClassLoader(),
            DocumentWithOutSavingService.class,
            fraudRpcClient);
    }

    @Bean
    FraudAdapter fraudAdapter(@Autowired(required = false) DocumentWithOutSavingService fraudRpcProxy) {
        return new FraudAdapterImpl(fraudRpcProxy);
    }
}
