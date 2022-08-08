package ru.sberbank.pprb.sbbol.partners.legacy;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.sberbank.pprb.sbbol.partners.legacy.exception.SbbolException;
import ru.sberbank.pprb.sbbol.partners.legacy.model.Counterparty;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartyCheckRequisites;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartyCheckRequisitesResult;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartyFilter;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartySignData;
import ru.sberbank.pprb.sbbol.partners.legacy.model.CounterpartyView;
import ru.sberbank.pprb.sbbol.partners.legacy.model.ListResponse;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class LegacySbbolAdapterImpl implements LegacySbbolAdapter {

    private static final String ROOT_URL = "/counterparty";
    private static final String CHECK_REQUISITES = ROOT_URL + "/check-requisites";
    private static final String UPDATE = ROOT_URL + "/update/{digitalId}";
    private static final String CREATE = ROOT_URL + "/create/{digitalId}";
    private static final String BY_PPRB_GUID = ROOT_URL + "/{digitalId}/{pprbGuid}";
    private static final String DELETE = ROOT_URL + "/delete/{digitalId}/{pprbGuid}";
    private static final String COUNTERPARTY_BY_DIGITAL_ID = ROOT_URL + "/list/{digitalId}";
    private static final String SIGN_SAVE = ROOT_URL + "/sign/save/{digitalId}";
    private static final String SIGN_REMOVE = ROOT_URL + "/sign/remove/{digitalId}/{pprbGuid}";
    private static final String VIEW = ROOT_URL + "/view/{digitalId}";
    private static final String CHECK_MIGRATION = ROOT_URL + "/check-migration/{digitalId}";
    private static final String GET_HOUSING_INN = ROOT_URL + "/housing/{digitalId}";

    private final RestTemplate restTemplate;
    private final HttpHeaders httpHeaders;


    public LegacySbbolAdapterImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    }

    @Override
    public void delete(String digitalId, String pprbGuid) {
        try {
            restTemplate.exchange(
                DELETE,
                HttpMethod.DELETE,
                new HttpEntity<>(httpHeaders),
                Void.class,
                digitalId,
                pprbGuid);
        } catch (HttpClientErrorException e) {
            throw new SbbolException(e.getStatusCode(), e.getMessage(), e);
        } catch (Exception e) {
            throw new SbbolException(e.getMessage(), e);
        }
    }

    @Override
    public Counterparty create(String digitalId, Counterparty counterparty) {
        try {
            ResponseEntity<Counterparty> response = restTemplate.exchange(
                CREATE,
                HttpMethod.POST,
                new HttpEntity<>(counterparty, httpHeaders),
                new ParameterizedTypeReference<>() {
                },
                digitalId
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new SbbolException(e.getStatusCode(), e.getMessage(), e);
        } catch (Exception e) {
            throw new SbbolException(e.getMessage(), e);
        }
    }

    @Override
    public Counterparty update(String digitalId, Counterparty counterparty) {
        try {
            ResponseEntity<Counterparty> response = restTemplate.exchange(
                UPDATE,
                HttpMethod.PUT,
                new HttpEntity<>(counterparty, httpHeaders),
                new ParameterizedTypeReference<>() {
                },
                digitalId
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new SbbolException(e.getStatusCode(), e.getMessage(), e);
        } catch (Exception e) {
            throw new SbbolException(e.getMessage(), e);
        }
    }

    @Override
    public List<CounterpartyView> list(String digitalId) {
        try {
            ResponseEntity<List<CounterpartyView>> response = restTemplate.exchange(
                COUNTERPARTY_BY_DIGITAL_ID,
                HttpMethod.GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<>() {
                },
                digitalId
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new SbbolException(e.getStatusCode(), e.getMessage(), e);
        } catch (Exception e) {
            throw new SbbolException(e.getMessage(), e);
        }
    }

    @Override
    public Counterparty getByPprbGuid(String digitalId, String pprbGuid) {
        try {
            ResponseEntity<Counterparty> response = restTemplate.exchange(
                BY_PPRB_GUID,
                HttpMethod.GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<>() {
                },
                digitalId,
                pprbGuid
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new SbbolException(e.getStatusCode(), e.getMessage(), e);
        } catch (Exception e) {
            throw new SbbolException(e.getMessage(), e);
        }
    }

    @Override
    public void saveSign(String digitalId, CounterpartySignData signData) {
        try {
            restTemplate.exchange(
                SIGN_SAVE,
                HttpMethod.POST,
                new HttpEntity<>(signData, httpHeaders),
                Void.class,
                digitalId);
        } catch (HttpClientErrorException e) {
            throw new SbbolException(e.getStatusCode(), e.getMessage(), e);
        } catch (Exception e) {
            throw new SbbolException(e.getMessage(), e);
        }
    }

    @Override
    public void removeSign(String digitalId, String pprbGuid) {
        try {
            restTemplate.exchange(
                SIGN_REMOVE,
                HttpMethod.DELETE,
                new HttpEntity<>(httpHeaders),
                Void.class,
                digitalId,
                pprbGuid);
        } catch (HttpClientErrorException e) {
            throw new SbbolException(e.getStatusCode(), e.getMessage(), e);
        } catch (Exception e) {
            throw new SbbolException(e.getMessage(), e);
        }
    }

    @Override
    public ListResponse<CounterpartyView> viewRequest(String digitalId, CounterpartyFilter filter) {
        try {
            ResponseEntity<ListResponse<CounterpartyView>> response = restTemplate.exchange(
                VIEW,
                HttpMethod.POST,
                new HttpEntity<>(filter, httpHeaders),
                new ParameterizedTypeReference<>() {
                },
                digitalId
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new SbbolException(e.getStatusCode(), e.getMessage(), e);
        } catch (Exception e) {
            throw new SbbolException(e.getMessage(), e);
        }
    }

    @Override
    @Cacheable("migration")
    public boolean checkNotMigration(String digitalId) {
        try {
            ResponseEntity<Boolean> response = restTemplate.exchange(
                CHECK_MIGRATION,
                HttpMethod.GET,
                new HttpEntity<>(httpHeaders),
                new ParameterizedTypeReference<>() {
                },
                digitalId
            );
            return Boolean.FALSE.equals(response.getBody());
        } catch (HttpClientErrorException e) {
            throw new SbbolException(e.getStatusCode(), e.getMessage(), e);
        } catch (Exception e) {
            throw new SbbolException(e.getMessage(), e);
        }
    }

    @Override
    @Cacheable("housingInns")
    public Set<String> getHousingInn(String digitalId, Set<String> counterpartyInns) {
        try {
            ResponseEntity<Set<String>> response = restTemplate.exchange(
                GET_HOUSING_INN,
                HttpMethod.POST,
                new HttpEntity<>(counterpartyInns, httpHeaders),
                new ParameterizedTypeReference<>() {
                },
                digitalId
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new SbbolException(e.getStatusCode(), e.getMessage(), e);
        } catch (Exception e) {
            throw new SbbolException(e.getMessage(), e);
        }
    }

    @Override
    public CounterpartyCheckRequisitesResult checkRequisites(CounterpartyCheckRequisites request) {
        try {
            ResponseEntity<CounterpartyCheckRequisitesResult> responseEntity = restTemplate.exchange(
                CHECK_REQUISITES,
                HttpMethod.POST,
                new HttpEntity<>(request, httpHeaders),
                new ParameterizedTypeReference<>() {
                });
            return responseEntity.getBody();
        } catch (HttpClientErrorException e) {
            throw new SbbolException(e.getStatusCode(), e.getMessage(), e);
        } catch (Exception e) {
            throw new SbbolException(e.getMessage(), e);
        }
    }
}
