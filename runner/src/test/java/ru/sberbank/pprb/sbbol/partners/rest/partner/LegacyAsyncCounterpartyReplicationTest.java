package ru.sberbank.pprb.sbbol.partners.rest.partner;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.KafkaTemplate;
import ru.sberbank.pprb.sbbol.partners.config.props.ReplicationKafkaProducerProperties;
import ru.sberbank.pprb.sbbol.partners.config.props.ReplicationKafkaSecurityProperties;
import ru.sberbank.pprb.sbbol.partners.config.props.ValidationInterceptorProperties;
import ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.legacy.model.Counterparty;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.BaseAccountSignControllerTest.createValidAccountsSign;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.BaseAccountSignControllerTest.deleteAccountSign;
import static ru.sberbank.pprb.sbbol.partners.rest.partner.PartnerControllerTest.createValidPartner;

class LegacyAsyncCounterpartyReplicationTest extends BaseAccountControllerTest {

    @Autowired
    private ReplicationKafkaProducerProperties replicationKafkaProducerProperties;

    @SpyBean
    private LegacySbbolAdapter legacySbbolAdapter;

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void testReplicationKafkaProducerProperties() {
        var expectedReplicationKafkaProducerProperties = getTestingReplicationKafkaProducerProperties();

        assertThat(replicationKafkaProducerProperties)
            .usingRecursiveComparison()
            .isEqualTo(expectedReplicationKafkaProducerProperties);
    }

    @Test
    void testAsyncCounterpartyReplication() throws JsonProcessingException {
        doReturn(false)
            .when(legacySbbolAdapter)
            .checkNotMigration(any());
        doReturn(null)
            .when(legacySbbolAdapter)
            .getByPprbGuid(any(), any());
        doThrow(RuntimeException.class)
            .when(legacySbbolAdapter)
            .create(any(), any());
        doThrow(RuntimeException.class)
            .when(legacySbbolAdapter)
            .delete(any(), any());
        doThrow(RuntimeException.class)
            .when(legacySbbolAdapter)
            .saveSign(any(), any());
        doThrow(RuntimeException.class)
            .when(legacySbbolAdapter)
            .removeSign(any(), any());
        when(kafkaTemplate.send(any(), any()))
            .thenReturn(null);

        var partner = createValidPartner();
        var account = createValidAccount(partner.getId(), partner.getDigitalId());

        doReturn(new Counterparty())
            .when(legacySbbolAdapter)
            .getByPprbGuid(any(), any());

        changeAccount(updateAccount(account));
        createValidAccountsSign(account.getDigitalId(), account.getId(), getBase64FraudMetaData());
        deleteAccountSign(account.getDigitalId(), account.getId());
        deleteAccount(account.getDigitalId(), account.getId());

        verify(kafkaTemplate, times(5)).send(any(), any());
    }

    private ReplicationKafkaProducerProperties getTestingReplicationKafkaProducerProperties() {
        var validationInterceptorProperties = new ValidationInterceptorProperties();
        validationInterceptorProperties.setEnable(true);
        validationInterceptorProperties.setClasses(List.of("ru.sbt.ss.kafka.validator.interceptor.ValidatorProducerInterceptor"));
        validationInterceptorProperties.setConfig("synapse-replication-kafka/counterparty-validator.conf");

        var kafkaSecurityProperties = new ReplicationKafkaSecurityProperties();
        kafkaSecurityProperties.setEnable(false);
        kafkaSecurityProperties.setProtocol("SSL");
        kafkaSecurityProperties.setSslKeystoreLocation("/opt/keystore/kafka/server.keystore.jks");
        kafkaSecurityProperties.setSslTruststoreLocation("/opt/keystore/kafka/trust.jks");
        kafkaSecurityProperties.setSslKeystoreType("JKS");
        kafkaSecurityProperties.setSslTruststoreType("JKS");
        kafkaSecurityProperties.setSslProtocol("TLS");
        kafkaSecurityProperties.setSslEnabledProtocols("TLSv1.2");

        var kafkaProducerProperties = new ReplicationKafkaProducerProperties(
            validationInterceptorProperties,
            kafkaSecurityProperties
        );
        kafkaProducerProperties.setEnable(true);
        kafkaProducerProperties.setServer("localhost:9092");
        kafkaProducerProperties.setProducerId("counterparty-producer");
        kafkaProducerProperties.setTopic("counterparty");

        return kafkaProducerProperties;
    }
}
