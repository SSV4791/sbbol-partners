package ru.sberbank.pprb.sbbol.partners.migration.service;

import io.restassured.common.mapper.TypeRef;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.sberbank.pprb.sbbol.migration.gku.model.MigrationGkuCandidate;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationTest;
import ru.sberbank.pprb.sbbol.partners.entity.partner.GkuInnEntity;
import ru.sberbank.pprb.sbbol.partners.migration.model.JsonRpcRequest;
import ru.sberbank.pprb.sbbol.partners.migration.model.JsonRpcResponse;
import ru.sberbank.pprb.sbbol.partners.migration.model.MigrateGkuRequest;
import ru.sberbank.pprb.sbbol.partners.repository.partner.GkuInnDictionaryRepository;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class GkuMigrationServiceTest extends AbstractIntegrationTest {

    @Autowired
    private PodamFactory podamFactory;

    @Autowired
    private GkuInnDictionaryRepository gkuInnDictionaryRepository;

    private static final String JSON_RPC_REQUEST_ID = RandomStringUtils.randomAlphanumeric(10);
    private static final String JSON_RPC_VERSION = "2.0";
    private static final String REMOTE_MIGRATE_METHOD_NAME = "migrate";
    private static final String REMOTE_DELETE_METHOD_NAME = "delete";
    private static final String URI_REMOTE_SERVICE = "/gku/migrate";

    private final JsonRpcRequest<MigrateGkuRequest> requestMigrate = new JsonRpcRequest<>(
        JSON_RPC_REQUEST_ID,
        JSON_RPC_VERSION,
        REMOTE_MIGRATE_METHOD_NAME,
        null
    );

    private final JsonRpcRequest<MigrateGkuRequest> requestDelete = new JsonRpcRequest<>(
        JSON_RPC_REQUEST_ID,
        JSON_RPC_VERSION,
        REMOTE_DELETE_METHOD_NAME,
        null
    );

    @Test
    void migrationGKU() {
        @SuppressWarnings("unchecked")
        Collection<MigrationGkuCandidate> collection = podamFactory.manufacturePojo(Collection.class, MigrationGkuCandidate.class);
        requestMigrate.setParams(new MigrateGkuRequest(collection));
        JsonRpcResponse<Void> response = post(URI_REMOTE_SERVICE, requestMigrate, new TypeRef<>() {});
        assertNotNull(response);
        assertEquals(response.getId(), JSON_RPC_REQUEST_ID);
        assertEquals(response.getJsonrpc(), JSON_RPC_VERSION);
    }

    @Test
    void deleteGKU() {
        var entity = podamFactory.manufacturePojo(GkuInnEntity.class);
        var savedEntity = gkuInnDictionaryRepository.save(entity);
        var foundInnBeforeDelete = gkuInnDictionaryRepository.getByInn(savedEntity.getInn());
        assertNotNull(foundInnBeforeDelete);

        JsonRpcResponse<Void> response = post(URI_REMOTE_SERVICE, requestDelete, new TypeRef<>() {});
        assertNotNull(response);
        assertEquals(response.getId(), JSON_RPC_REQUEST_ID);
        assertEquals(response.getJsonrpc(), JSON_RPC_VERSION);

        var foundInnAfterDelete = gkuInnDictionaryRepository.getByInn(savedEntity.getInn());
        assertNull(foundInnAfterDelete);
    }
}
