package ru.sberbank.pprb.sbbol.partners.migration.service;

import io.restassured.common.mapper.TypeRef;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import ru.sberbank.pprb.sbbol.migration.gku.model.MigrationGkuCandidate;
import ru.sberbank.pprb.sbbol.partners.config.AbstractIntegrationWithOutSbbolTest;
import ru.sberbank.pprb.sbbol.partners.migration.model.JsonRpcRequest;
import ru.sberbank.pprb.sbbol.partners.migration.model.JsonRpcResponse;
import ru.sberbank.pprb.sbbol.partners.migration.model.MigrateGkuRequest;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GkuMigrationServiceTest extends AbstractIntegrationWithOutSbbolTest {

    private static final String JSON_RPC_REQUEST_ID = RandomStringUtils.randomAlphanumeric(10);
    private static final String JSON_RPC_VERSION = "2.0";
    private static final String REMOTE_METHOD_NAME = "migrate";
    private static final String URI_REMOTE_SERVICE = "/gku/migrate";

    private final JsonRpcRequest<MigrateGkuRequest> request = new JsonRpcRequest<>(
        JSON_RPC_REQUEST_ID,
        JSON_RPC_VERSION,
        REMOTE_METHOD_NAME,
        null
    );

    @Test
    void migrateFewCorrespondentsTest() {
        @SuppressWarnings("unchecked")
        Collection<MigrationGkuCandidate> collection = factory.manufacturePojo(Collection.class, MigrationGkuCandidate.class);
        request.setParams(new MigrateGkuRequest(collection));
        JsonRpcResponse<Void> response = post(URI_REMOTE_SERVICE, request, new TypeRef<>() {});
        assertNotNull(response);
        assertEquals(response.getId(), JSON_RPC_REQUEST_ID);
        assertEquals(response.getJsonrpc(), JSON_RPC_VERSION);
    }
}
