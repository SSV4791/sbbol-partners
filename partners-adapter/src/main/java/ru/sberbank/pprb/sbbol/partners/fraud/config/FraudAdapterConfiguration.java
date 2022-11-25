package ru.sberbank.pprb.sbbol.partners.fraud.config;

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

@Configuration
@EnableConfigurationProperties(FraudProperties.class)
public class FraudAdapterConfiguration {

    private final FraudProperties fraudProperties;

    public FraudAdapterConfiguration(FraudProperties fraudProperties) {
        this.fraudProperties = fraudProperties;
    }

    @ConditionalOnProperty(prefix = "fraud", name = "enabled")
    @Bean
    JsonRpcHttpClient fraudJsonRpcHttpClient() throws MalformedURLException {
        URL fraudURL = new URL(fraudProperties.getUrl() + fraudProperties.getEndpoint());
        return new JsonRpcHttpClient(fraudURL);
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
