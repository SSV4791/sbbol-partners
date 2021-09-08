package ru.sberbank.pprb.sbbol.partners.config.endpoint;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

public class DataSpaceCoreIndicator implements HealthIndicator {

    private final String server;

    private final RestTemplate restTemplate;
    private final HttpEntity<String> requestEntity;

    public DataSpaceCoreIndicator(String server) {
        this.server = server;
        this.restTemplate = new RestTemplate();
        this.requestEntity = new HttpEntity<>("", new HttpHeaders());
    }

    @Override
    public Health health() {
        String messageKey = "DataSpaceCore";
        if (!isRunningDataSpaceCore()) {
            return Health.down().withDetail(messageKey, "Not Available").build();
        }
        return Health.up().withDetail(messageKey, "Available").build();
    }

    private boolean isRunningDataSpaceCore() {
        ResponseEntity<String> responseEntity;
        try {
            String healthCheck = "/actuator/health/readiness";
            responseEntity = restTemplate.exchange(server + healthCheck, HttpMethod.GET, requestEntity, String.class);
        } catch (HttpServerErrorException x) {
            return false;
        }
        return responseEntity.getStatusCode() == HttpStatus.OK;
    }
}
