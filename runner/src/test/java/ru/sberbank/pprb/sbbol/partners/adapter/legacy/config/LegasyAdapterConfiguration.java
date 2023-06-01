package ru.sberbank.pprb.sbbol.partners.adapter.legacy.config;

import org.mockito.ArgumentMatchers;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.sberbank.pprb.sbbol.partners.config.PodamConfiguration;
import ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapter;
import ru.sberbank.pprb.sbbol.partners.legacy.LegacySbbolAdapterImpl;
import ru.sberbank.pprb.sbbol.partners.legacy.model.Counterparty;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartyCheckRequisitesResult;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartyView;
import ru.sberbank.pprb.sbbol.partners.legacy.model.ListResponse;
import uk.co.jemos.podam.api.PodamFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

@TestConfiguration
public class LegasyAdapterConfiguration extends PodamConfiguration {

    @Autowired
    private PodamFactory factory;

    private static final String ROOT_URL = "/counterparty";
    private static final String CHECK_REQUISITES = ROOT_URL + "/check-requisites";
    private static final String UPDATE = ROOT_URL + "/update/{digitalId}";
    private static final String CREATE = ROOT_URL + "/create/{digitalId}";
    private static final String BY_PPRB_GUID = ROOT_URL + "/{digitalId}/{pprbGuid}";
    private static final String DELETE = ROOT_URL + "/delete/{digitalId}/{pprbGuid}";
    private static final String COUNTERPARTY_BY_DIGITAL_ID = ROOT_URL + "/list/{digitalId}";
    private static final String SIGN_SAVE = ROOT_URL + "/sign/save/{digitalUserId}";
    private static final String SIGN_REMOVE = ROOT_URL + "/sign/remove/{digitalId}/{pprbGuid}";
    private static final String VIEW = ROOT_URL + "/view/{digitalId}";
    private static final String CHECK_MIGRATION = ROOT_URL + "/check-migration/{digitalId}";
    private static final String GET_HOUSING_INN = ROOT_URL + "/housing/{digitalId}";

    @MockBean(name = "restTemplate", reset = MockReset.NONE)
    RestTemplate restTemplate;

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void initMock() {
        when(restTemplate.exchange(ArgumentMatchers.eq(CHECK_MIGRATION), ArgumentMatchers.eq(GET), ArgumentMatchers.<HttpEntity<Object>>any(), ArgumentMatchers.<ParameterizedTypeReference<Object>>any(), ArgumentMatchers.anyString()))
            .thenReturn(new ResponseEntity<>(Boolean.class, HttpStatus.OK));

        when(restTemplate.exchange(ArgumentMatchers.eq(CHECK_REQUISITES), ArgumentMatchers.eq(POST), ArgumentMatchers.any(), ArgumentMatchers.<ParameterizedTypeReference<CounterpartyCheckRequisitesResult>>any()))
            .thenReturn(new ResponseEntity<>(new CounterpartyCheckRequisitesResult(UUID.randomUUID().toString(), false), HttpStatus.OK));

        when(restTemplate.exchange(ArgumentMatchers.eq(UPDATE), ArgumentMatchers.eq(PUT), ArgumentMatchers.any(), ArgumentMatchers.<ParameterizedTypeReference<CounterpartyCheckRequisitesResult>>any(), ArgumentMatchers.anyString()))
            .thenAnswer((Answer<ResponseEntity<Counterparty>>) invocation -> {
                HttpEntity<Counterparty> httpEntity = invocation.getArgument(2);
                return new ResponseEntity<>(httpEntity.getBody(), HttpStatus.OK);
            });

        when(restTemplate.exchange(ArgumentMatchers.eq(CREATE), ArgumentMatchers.eq(POST), ArgumentMatchers.any(), ArgumentMatchers.<ParameterizedTypeReference<CounterpartyCheckRequisitesResult>>any(), ArgumentMatchers.anyString()))
            .thenAnswer((Answer<ResponseEntity<Counterparty>>) invocation -> {
                HttpEntity<Counterparty> httpEntity = invocation.getArgument(2);
                return new ResponseEntity<>(httpEntity.getBody(), HttpStatus.OK);
            });

        when(restTemplate.exchange(ArgumentMatchers.eq(BY_PPRB_GUID), ArgumentMatchers.eq(GET), ArgumentMatchers.any(), ArgumentMatchers.<ParameterizedTypeReference<Counterparty>>any(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
            .thenAnswer((Answer<ResponseEntity<Counterparty>>) invocation -> {
                String digitalId = invocation.getArgument(4, String.class);
                String pprbGuid = invocation.getArgument(5, String.class);
                var counterparty = new Counterparty();
                counterparty.setPprbGuid(pprbGuid);
                return new ResponseEntity<>(counterparty, HttpStatus.OK);
            });

        when(restTemplate.exchange(ArgumentMatchers.eq(COUNTERPARTY_BY_DIGITAL_ID), ArgumentMatchers.eq(GET), ArgumentMatchers.any(), ArgumentMatchers.<ParameterizedTypeReference<CounterpartyView>>any(), anyString()))
            .thenAnswer((Answer<ResponseEntity<List<CounterpartyView>>>) invocation -> {
                List<CounterpartyView> counterparty = factory.manufacturePojo(ArrayList.class, CounterpartyView.class);
                return new ResponseEntity<>(counterparty, HttpStatus.OK);
            });

        when(restTemplate.exchange(ArgumentMatchers.eq(SIGN_SAVE), ArgumentMatchers.eq(HttpMethod.POST), ArgumentMatchers.any(), ArgumentMatchers.<ParameterizedTypeReference<Void>>any(), anyString()))
            .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        when(restTemplate.exchange(ArgumentMatchers.eq(DELETE), ArgumentMatchers.eq(HttpMethod.DELETE), ArgumentMatchers.any(), ArgumentMatchers.<ParameterizedTypeReference<Void>>any(), anyString()))
            .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        when(restTemplate.exchange(ArgumentMatchers.eq(SIGN_REMOVE), ArgumentMatchers.eq(HttpMethod.DELETE), ArgumentMatchers.any(), ArgumentMatchers.<ParameterizedTypeReference<Void>>any(), anyString()))
            .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        when(restTemplate.exchange(ArgumentMatchers.eq(GET_HOUSING_INN), ArgumentMatchers.eq(HttpMethod.POST), ArgumentMatchers.any(), ArgumentMatchers.<ParameterizedTypeReference<Void>>any(), anyString()))
            .thenAnswer((Answer<ResponseEntity<Set<String>>>) invocation -> {
                HttpEntity<Set<String>> httpEntity = invocation.getArgument(2);
                return new ResponseEntity<>(httpEntity.getBody(), HttpStatus.OK);
            });

        when(restTemplate.exchange(ArgumentMatchers.eq(VIEW), ArgumentMatchers.eq(HttpMethod.POST), ArgumentMatchers.any(), ArgumentMatchers.<ParameterizedTypeReference<Void>>any(), anyString()))
            .thenAnswer((Answer<ResponseEntity<ListResponse<CounterpartyView>>>) invocation -> {
                ListResponse<CounterpartyView> counterparty = factory.manufacturePojo(ListResponse.class, CounterpartyView.class);
                return new ResponseEntity<>(counterparty, HttpStatus.OK);
            });
    }

    @Bean
    public RetryTemplate sbbolLegacyRetryTemplate(
        @Value("${sbbol.retry.max_attempts:3}") Integer maxAttempts,
        @Value("${sbbol.retry.interval:5000}") Long retryTimeInterval
    ) {
        return RetryTemplate.builder()
            .notRetryOn(HttpClientErrorException.BadRequest.class)
            .traversingCauses()
            .maxAttempts(maxAttempts)
            .fixedBackoff(retryTimeInterval)
            .build();
    }

    @Bean
    public LegacySbbolAdapter legacySbbolAdapter(
        RestTemplate restTemplate,
        RetryTemplate sbbolLegacyRetryTemplate
    ) {
        return new LegacySbbolAdapterImpl(restTemplate, sbbolLegacyRetryTemplate);
    }
}
