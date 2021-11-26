package ru.sberbank.pprb.sbbol.partners;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.sberbank.pprb.sbbol.partners.model.CounterpartyCheckRequisitesResult;
import ru.sberbank.pprb.sbbol.partners.model.CounterpartyCheckRequisites;

import java.util.Collections;

public class LegacySbbolAdapter {

    private final RestTemplate restTemplate;
    private final HttpHeaders httpHeaders;

    public LegacySbbolAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    }

    public boolean checkMigration(String digitalId) {
        //TODO Проверка подключенности
        return true;
    }

    /**
     * Проверка контрагента по реквизитам
     *
     * @param request запрос на проверку
     * @return результат проверки контрагента
     */
    public CounterpartyCheckRequisitesResult checkRequisites(CounterpartyCheckRequisites request) {
        try {
            ResponseEntity<CounterpartyCheckRequisitesResult> responseEntity = restTemplate.exchange(
                "/counterparty/check-requisites",
                HttpMethod.POST,
                new HttpEntity<>(request, httpHeaders),
                new ParameterizedTypeReference<>() {
                });
            return responseEntity.getBody();
        } catch (HttpClientErrorException e) {
            String msg = String.format("Error execute http-request to SBBOL: StatusCode: %s, Message: %s", e.getStatusCode(), e.getMessage());
            throw new RuntimeException(msg);
        } catch (Exception e) {
            throw new RuntimeException("Error sending request to SBBOL: " + e.getMessage(), e);
        }
    }
}
