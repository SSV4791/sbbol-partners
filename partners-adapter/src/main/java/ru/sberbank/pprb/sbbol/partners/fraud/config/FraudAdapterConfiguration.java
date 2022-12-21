package ru.sberbank.pprb.sbbol.partners.fraud.config;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.googlecode.jsonrpc4j.ProxyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOG = LoggerFactory.getLogger(FraudAdapterConfiguration.class);

    private final FraudProperties fraudProperties;

    public FraudAdapterConfiguration(FraudProperties fraudProperties) {
        this.fraudProperties = fraudProperties;
    }

    @ConditionalOnProperty(prefix = "fraud", name = "enabled")
    @Bean
    JsonRpcHttpClient fraudJsonRpcHttpClient() throws MalformedURLException {
        try {
            URL fraudURL = new URL("http://" + fraudProperties.getUrl() + fraudProperties.getEndpoint());
            return new JsonRpcHttpClient(fraudURL);
        } catch (MalformedURLException e) {
            LOG.error("Невозможно создать бин fraudJsonRpcHttpClient. Невалидный URL АС Агрегатора данных ФРОД-мониторинга: {}", fraudProperties);
            return null;
        }
    }

    @ConditionalOnProperty(prefix = "fraud", name = "enabled")
    @Bean
    CounterPartyService fraudRpcProxy(@Autowired(required = false) JsonRpcHttpClient fraudRpcClient) {
        if (fraudRpcClient == null) {
            LOG.error("Невозможно создать бин fraudRpcProxy. В контексте отсутствует бин fraudJsonRpcHttpClient.");
            return null;
        }
        return ProxyUtil.createClientProxy(
            fraudRpcClient.getClass().getClassLoader(),
            CounterPartyService.class,
            fraudRpcClient);
    }

    @Bean
    FraudAdapter fraudAdapter(@Autowired(required = false)  CounterPartyService fraudRpcProxy) {
        return new FraudAdapterImpl(fraudRpcProxy, fraudProperties);
    }
}
